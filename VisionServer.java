import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class VisionServer implements Runnable {
	private static Thread visionServerThread;
	private static ArrayList<Thread> connections = new ArrayList<Thread>();

	private static boolean leftGoal = false;
	private static boolean isGoalHot = false;
	private static double lastHeartbeatTime = -1.0;
    private static int hotGoalCount = 0;

	public VisionServer() {
		visionServerThread = new Thread(this);
	}

	public void run() {
		ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1180);
            while(true) {
                Socket socket = serverSocket.accept();
                Thread connection = new Thread(new ConnectionHandler(socket));
                connection.start();
                connections.add(connection);
                try {
                    Thread.sleep(100); // Pause for 100ms
                } catch (InterruptedException ex) {
                    System.out.println("Thread sleep failed.");
                }
            }
        } catch(IOException e) {
            System.out.println("Socket failed!");
            e.printStackTrace();
        }
	}

	public static void main(String args[]) throws IOException {
		VisionServer server = new VisionServer();
		visionServerThread.start();

		double lastTime = System.currentTimeMillis();
		double period = 500.0;

		while(true) {
			if(System.currentTimeMillis() - lastTime > period) {
				System.out.println("Goal Status:  " + (isGoalHot ? "HOT!!!" : "cold"));
				lastTime = System.currentTimeMillis();
			}
		}
	}

	private class ConnectionHandler implements Runnable {
        Socket socket;
        private final double timeout = 10.0;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }
    
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] b = new byte[5];
                double lastHeartbeat = System.currentTimeMillis() / 1000.0;
                VisionServer.this.lastHeartbeatTime = lastHeartbeat;
                
                while (System.currentTimeMillis() / 1000.0 < lastHeartbeat + timeout) {
                    while (inputStream.available() > 0) {
                        int inputData = inputStream.read(b);
                        for (int i = 0; i < inputData; ++i) {
                            byte reading = b[i];
                            boolean goalStatus = (reading << 0) > 0;
                            VisionServer.isGoalHot = goalStatus;
                        }
                        lastHeartbeat = System.currentTimeMillis() / 1000.0;
                        VisionServer.this.lastHeartbeatTime = lastHeartbeat;
                    }
                    try {
                        Thread.sleep(50); // sleep a bit
                    } catch (InterruptedException ex) {
                        System.out.println("Thread sleep failed.");
                    }
                }
                
                inputStream.close();
                socket.close();
            }
            catch(IOException e) {
            }
        }
    }
}
