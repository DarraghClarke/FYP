package service.edge;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.security.KeyStore;
//based on TooTallNate example https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/SSLClientExample.java
public class SSLMain {

    SSLMain(URI address) throws Exception{
        Edge node = new Edge(address);

        // load up the key store
        String STORETYPE = "JKS";
        String KEYSTORE = "src/main/java/keystore.jks";
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

        SSLSocketFactory factory = sslContext.getSocketFactory();

        node.setSocketFactory( factory );

        node.connectBlocking();
        node.setProxy(new Proxy( Proxy.Type.HTTP, new InetSocketAddress( "proxyaddressedge", 80 )  ));

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        while ( true ) {
            String line = reader.readLine();
            if( line.equals( "close" ) ) {
                node.closeBlocking();
            } else if ( line.equals( "open" ) ) {
                node.reconnect();
            } else {
                node.send( line );
            }
        }
    }
    public static void main( String[] args ) throws Exception {

    }
}
