package service.cloud;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyStore;
import java.net.Proxy;
//based on TooTallNate example at https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/SSLClientExample.java

public class SSLMain {
    public static void main( String[] args ) throws Exception {
        Proxy proxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( "proxyaddresscloud", 80 )  );
        Cloud node = new Cloud( new URI( "wss://localhost:443" ) , new File("D:\\code\\practical 5\\FYP\\CloudNode\\src\\main\\resources\\docker.tar"));

        // load up the key store
        String STORETYPE = "JKS";
        String KEYSTORE = "D:/code/practical 5/FYP/CloudNode/src/main/java/keystore.jks";
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
        // sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates

        SSLSocketFactory factory = sslContext.getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();

        node.setSocketFactory( factory );

        node.connectBlocking();
        node.setProxy(proxy);
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
}
