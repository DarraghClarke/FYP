package service.orchestrator;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.net.UnknownHostException;

@CommandLine.Command(name = "cmMain", mixinStandardHelpOptions = true, version = "0.7")
public class cmMain implements Runnable {

    @Option(names = {"-s", "--secure"},
            description = "Secure mode, only engages with orchestrators using SSL")
    private boolean secure;

    @Parameters(index = "0", paramLabel = "port", description = "The port the orchestrator should run on")
    private int port;

    @Option(names = {"--RollingAverage"}, paramLabel = "Rolling Average", description = "The value that should be used in the rolling average")
    int archive;

    @Override
    public void run() {
        if (!secure) {
            Orchestrator orchestrator = null;
            try {
                orchestrator = new Orchestrator(port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            orchestrator.run();
        } else {
            try {
                new SSLMain(port);
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
