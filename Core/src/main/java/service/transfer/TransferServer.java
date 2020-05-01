package service.transfer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;

public class TransferServer extends WebSocketServer {
    private File service;

    public TransferServer(InetSocketAddress address, File service) {
        super(address);
        this.service = service;

        System.out.println("Launched the transfer server");
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("connected to tempClient" + " TIME AT START " + System.currentTimeMillis());
        byte[] bytesArray = new byte[(int) service.length()];

        FileInputStream fis;
        System.out.println("Trying to send");
        try {
            System.out.println("Trying to send 2");
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
        webSocket.close();
        try {
            stop();//if this is unstable this could be moved to the owner of this object and they could close it
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
