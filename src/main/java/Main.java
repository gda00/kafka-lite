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

       clientSocket.getOutputStream().write(messageSizeBytes);
       clientSocket.getOutputStream().write(correlationId);
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