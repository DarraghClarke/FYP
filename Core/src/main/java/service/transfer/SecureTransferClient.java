package service.transfer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.security.KeyStore;

public class SecureTransferClient {

    public SecureTransferClient(URI serverUri, DockerController dockerController) throws Exception {
        TransferClient transferClient = new TransferClient(serverUri,dockerController);

        // load up the key store
        String STORETYPE = "JKS";
        String STOREPASSWORD = "storepassword";
        String KEYPASSWORD = "keypassword";

        KeyStore ks = KeyStore.getInstance(STORETYPE);
        InputStream is = this.getClass().getResourceAsStream("/keystore.jks");
        System.out.println(is.toString());
        ks.load(is, STOREPASSWORD.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, KEYPASSWORD.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLSocketFactory factory = sslContext.getSocketFactory();

        transferClient.setSocketFactory(factory);

        transferClient.connectBlocking();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = reader.readLine();
            if (line.equals("close")) {
                transferClient.closeBlocking();
            } else if (line.equals("open")) {
                transferClient.reconnect();
            } else {
                transferClient.send(line);
            }
        }
    }
}
