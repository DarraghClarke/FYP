package service.orchestrator;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;


//taken from example by TooTallNate at https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/TwoWaySSLServerExample.java
public class SSLMain {
    SSLMain(int port) throws Exception {
        Orchestrator orchestrator;
        orchestrator = new Orchestrator(port);

        // load up the key store
        String STORETYPE = "JKS";
        System.out.println(System.getProperty("user.dir"));
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

        orchestrator.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
        System.out.println(orchestrator.getWebSocketFactory());
        orchestrator.run();
    }

    public static void main(String[] args) throws Exception {
        SSLMain sslMain = new SSLMain(443);

    }
}
