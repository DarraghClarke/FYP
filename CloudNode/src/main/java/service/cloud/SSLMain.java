package service.cloud;

import org.apache.tomcat.jni.SSL;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.*;
import java.security.*;
//based on TooTallNate example at https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/SSLClientExample.java

public class SSLMain {
    SSLMain(URI address ,File serviceToRun, URI serviceAddress) throws Exception{

        Cloud node = new Cloud( address , serviceToRun,serviceAddress);//wss://137.43.49.51:443

        // load up the key store
        String STORETYPE = "JKS";
        String STOREPASSWORD = "storepassword";
        String KEYPASSWORD = "keypassword";

        KeyStore ks = KeyStore.getInstance( STORETYPE );
        InputStream is =  this.getClass().getResourceAsStream("/keystore.jks");
        System.out.println(is.toString());
        ks.load( is, STOREPASSWORD.toCharArray() );

        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
        kmf.init( ks, KEYPASSWORD.toCharArray() );
        TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
        tmf.init( ks );

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance( "TLS" );
        sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );

        SSLSocketFactory factory = (SSLSocketFactory) sslContext.getSocketFactory();

        node.setSocketFactory( factory );

        node.connectBlocking();
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
        //SSLMain sslMain=new SSLMain(new URI( "ws://localhost:443" ), new File("D:\\code\\practical 5\\FYP\\CloudNode\\src\\main\\resources\\docker.tar"),444);
    }
}
