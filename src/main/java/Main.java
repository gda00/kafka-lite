import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
     ServerSocket serverSocket = null;
     Socket clientSocket = null;
     int port = 9092;
     try {
       serverSocket = new ServerSocket(port);
       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept();
       InputStream in = clientSocket.getInputStream();

       byte[] messageSizeBytes = in.readNBytes(4);
       byte[] apiKey = in.readNBytes(2);
       byte[] apiVersion = in.readNBytes(2);
       byte[] correlationId = in.readNBytes(4);
       byte[] error_code = new byte[2];
       short errorValue = 35;

       short apiVersionDecimal = (short) (((apiVersion[0] & 0xFF) << 8) | (apiVersion[1] & 0xFF));

       if(apiVersionDecimal < 0 || apiVersionDecimal > 4) {
           error_code[0] = (byte) (errorValue >> 8);
           error_code[1] = (byte) errorValue;
       }

       clientSocket.getOutputStream().write(messageSizeBytes);
       clientSocket.getOutputStream().write(correlationId);
       clientSocket.getOutputStream().write(error_code);
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     } finally {
       try {
         if (clientSocket != null) {
           clientSocket.close();
         }
       } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
       }
     }
  }
}