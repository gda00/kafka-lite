import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

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

            // Request
            // Message Size
            byte[] messageSizeBytes = in.readNBytes(4);

            // Request Header (v2)
            byte[] apiKey = in.readNBytes(2);
            byte[] apiVersion = in.readNBytes(2);
            byte[] correlationId = in.readNBytes(4);
            byte[] headerClientIdLength = in.readNBytes(2);
            byte[] headerClientIdContents = in.readNBytes(ByteBuffer.wrap(headerClientIdLength).getShort());
            byte[] headerTagBuffer = in.readNBytes(1);

            // Request Body (v4)
            byte[] bodyClientIdLength = in.readNBytes(1);
            int n = ByteBuffer.wrap(bodyClientIdLength).get() - 1;
            byte[] bodyClientIdContents = in.readNBytes(n);
            byte[] clientSoftwareVersionLength = in.readNBytes(1);
            n = ByteBuffer.wrap(clientSoftwareVersionLength).get() - 1;
            byte[] clientSoftwareVersionContents = in.readNBytes(n);
            byte[] bodyTagBuffer = in.readNBytes(1);

            // Response
            // Response Body (v4)
            // Error code
            short errorValue = 0; // No error by default
            byte[] errorCode = ByteBuffer.allocate(2).putShort(errorValue).array();

            short apiVersionDecimal = ByteBuffer.wrap(apiVersion).getShort();

            if(apiVersionDecimal < 0 || apiVersionDecimal > 4) {
                errorValue = 35;
                errorCode = ByteBuffer.allocate(2).putShort(errorValue).array();
            }

            // API Version Array
            // Array Length
            byte[] arrayLength = ByteBuffer.allocate(1).put((byte) (3 + 1)).array();

            // API Version #1
            short apiKeyVers1 = 1;
            short minSupApiVers1 = 0;
            short maxSupApiVers1 = 17;
            byte tagBufferApiVers1 = 0;

            ByteBuffer buffer1 = ByteBuffer.allocate(7);
            buffer1.putShort(apiKeyVers1);
            buffer1.putShort(minSupApiVers1);
            buffer1.putShort(maxSupApiVers1);
            buffer1.put(tagBufferApiVers1);

            byte[] apiVers1 = buffer1.array();

            // API Version #2
            short apiKeyVers2 = 18;
            short minSupApiVers2 = 0;
            short maxSupApiVers2 = 4;
            byte tagBufferApiVers2 = 0;

            ByteBuffer buffer2 = ByteBuffer.allocate(7);
            buffer2.putShort(apiKeyVers2);
            buffer2.putShort(minSupApiVers2);
            buffer2.putShort(maxSupApiVers2);
            buffer2.put(tagBufferApiVers2);

            byte[] apiVers2 = buffer2.array();

            // API Version #3
            short apiKeyVers3 = 75;
            short minSupApiVers3 = 0;
            short maxSupApiVers3 = 0;
            byte tagBufferApiVers3 = 0;

            ByteBuffer buffer3 = ByteBuffer.allocate(7);
            buffer3.putShort(apiKeyVers3);
            buffer3.putShort(minSupApiVers3);
            buffer3.putShort(maxSupApiVers3);
            buffer3.put(tagBufferApiVers3);

            byte[] apiVers3 = buffer3.array();

            // Throttle Time
            byte[] throttleTime = ByteBuffer.allocate(4).putInt(0).array();

            // Tag Buffer
            byte responseTagBuffer = 0;

            byte[] messageSizeOutput = ByteBuffer.allocate(4).putInt(
                    correlationId.length +
                    errorCode.length +
                    arrayLength.length +
                    apiVers1.length +
                    apiVers2.length +
                    apiVers3.length +
                    throttleTime.length + 1
            ).array();

            clientSocket.getOutputStream().write(messageSizeOutput);
            clientSocket.getOutputStream().write(correlationId);
            clientSocket.getOutputStream().write(errorCode);
            clientSocket.getOutputStream().write(arrayLength);
            clientSocket.getOutputStream().write(apiVers1);
            clientSocket.getOutputStream().write(apiVers2);
            clientSocket.getOutputStream().write(apiVers3);
            clientSocket.getOutputStream().write(throttleTime);
            clientSocket.getOutputStream().write(responseTagBuffer);
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