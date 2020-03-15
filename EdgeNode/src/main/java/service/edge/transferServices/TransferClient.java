package service.edge.transferServices;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import service.edge.DockerController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

public class TransferClient extends WebSocketClient {
    DockerController dockerController;

    public TransferClient(URI serverUri, DockerController dockerController) {
        super(serverUri);
        this.dockerController=dockerController;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("connected to tempServer");
    }

    @Override
    public void onMessage(String file) {
        Gson gson = new Gson();
        File gsonFile = gson.fromJson(file, File.class);

        dockerController.launchServiceOnNode(gsonFile);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {

        byte[] b = bytes.array();

        try (FileOutputStream fos = new FileOutputStream("service.tar")) {
            fos.write(b);
            fos.close();
            dockerController.launchServiceOnNode(new File("service.tar"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

}
