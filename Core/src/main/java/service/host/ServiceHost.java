package service.host;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import service.transfer.DockerController;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ServiceHost extends WebSocketServer {
    DockerController dockerController;
    public ServiceHost(int port, DockerController dockerController) throws UnknownHostException{
        super(new InetSocketAddress(port));
        this.dockerController = dockerController;
        System.out.println(InetAddress.getLocalHost().getHostAddress().trim());
        System.out.println(this.getAddress().toString());

    }
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        try {
            dockerController.sendInput(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
