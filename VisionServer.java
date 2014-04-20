import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;

public class VisionServer {

	public static void main(String args[]) throws IOException {
		ServerSocket connection = new ServerSocket(1180);
		InputStream is = connection.accept().getInputStream();
		
		while(true) {
			try {
        		int ch = 0;
				byte[] b = new byte[1024];
				double timeout = 10.0;
				
				while(is.available() > 0) {
					int read = is.read(b);
					for (int i = 0; i < read; ++i) {
						byte reading = b[i];
						boolean leftStatus = (reading & (1 << 1)) > 0;
						boolean rightStatus = (reading & (1 << 0)) > 0;

						System.out.println("Left Status:  " + leftStatus);
						System.out.println("Right Status: " + rightStatus);
            		}
				}
			} finally {

			}
    	}
	}
}