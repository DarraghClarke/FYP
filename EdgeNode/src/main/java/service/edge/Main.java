package service.edge;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.net.URI;

@CommandLine.Command(name = "cmMain", mixinStandardHelpOptions = true, version = "0.7")
public class Main implements Runnable{

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
            Edge edge = new Edge(address,badAgent,port, !secure);
            edge.run();
        }else{
            try {
                new SecureEdge(address,badAgent,port,secure);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}