package service.orchestrator;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

@CommandLine.Command(name = "cmMain", mixinStandardHelpOptions = true, version = "0.1")
public class cmMain implements Runnable{

    @Option(names = { "-v", "--verbose" },
            description = "Verbose mode. Helpful for troubleshooting.")
    private boolean[] verbose = new boolean[0];

    @Option(names = { "-s", "--secure" },
            description = "Secure mode, only engages with orchestrators using SSL")
    private boolean secure;

    //@Option(names = { "-r", "--request" }, paramLabel = "requestedService", description = "requestedService")
    //File requestedService;

    @Parameters(index = "0", paramLabel = "port", description = "The port the orchestrator should run on")
    private int port;



    @Override
    public void run() {
        if(!secure) {
            Orchestrator orchestrator = null;
            try {
                orchestrator = new Orchestrator(port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            orchestrator.run();
        }else{
            try {
                new SSLMain(port);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws URISyntaxException {
        // By implementing Runnable or Callable, parsing, error handling and handling user
        // requests for usage help or version help can be done with one line of code.

        int exitCode = new CommandLine(new cmMain()).execute(args);
        System.exit(exitCode);
    }
}
