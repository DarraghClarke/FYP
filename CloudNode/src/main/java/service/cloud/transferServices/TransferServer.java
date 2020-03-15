package service.cloud.transferServices;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.*;
import java.net.InetSocketAddress;

public class TransferServer extends WebSocketServer {
    private File service;

    public TransferServer(InetSocketAddress address, File service) {
        super(address);
        this.service = service;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("connected to tempClient");
        byte[] bytesArray = new byte[(int) service.length()];

        FileInputStream fis;
        try {
            fis = new FileInputStream(service);
            fis.read(bytesArray);
            fis.close();
            webSocket.send(bytesArray);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
