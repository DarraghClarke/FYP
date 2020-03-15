package service.orchestrator;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.SSLParametersWebSocketServerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;


//taken from example by TooTallNate at https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/TwoWaySSLServerExample.java
public class SSLMain {

    public static void main( String[] args ) throws Exception {
        Orchestrator orchestrator = new Orchestrator( 443 ); // Firefox does allow multible ssl connection only via port 443 //tested on FF16

        // load up the key store
        String STORETYPE = "JKS";
        String KEYSTORE = "./orchestrator/src/main/java/keystore.jks";
        System.out.println(System.getProperty("user.dir"));
        String STOREPASSWORD = "storepassword";
        String KEYPASSWORD = "keypassword";

        KeyStore ks = KeyStore.getInstance( STORETYPE );
        File kf = new File( KEYSTORE );
        ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );

        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
        kmf.init( ks, KEYPASSWORD.toCharArray() );
        TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
        tmf.init( ks );

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance( "TLS" );
        sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );

        orchestrator.setWebSocketFactory( new DefaultSSLWebSocketServerFactory( sslContext ) );
        orchestrator.run();
    }
}
