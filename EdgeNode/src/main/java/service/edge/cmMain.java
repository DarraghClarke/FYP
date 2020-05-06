package service.edge;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

@CommandLine.Command(name = "cmMain", mixinStandardHelpOptions = true, version = "0.6")
public class cmMain implements Runnable{

    @Option(names = { "-b", "--badagent" },
            description = "flags node as untrustworthy")
    private boolean badAgent;

    @Option(names = { "-s", "--secure" },
            description = "Secure mode, only engages with orchestrators using SSL")
    private boolean secure;

    @Parameters(index = "0", paramLabel = "address", description = "The address of the orchestrator,format wss://{ip}:{port}")
    private URI address;

    @Parameters(index = "1", paramLabel = "file", description = "The location of the service you wish to run")
    private File file;

    @Parameters(index = "2", paramLabel = "port", description = "The port any services will run out of on this machine")
    private URI port;

    @Override
    public void run() {
        if(!secure) {
            Edge edge = new Edge(address,!badAgent,port);
            edge.run();
        }else{
            try {
                new SSLMain(address,!badAgent,port);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new cmMain()).execute(args);
        System.exit(exitCode);
    }
}