package service.orchestrator;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.net.UnknownHostException;

@CommandLine.Command(name = "cmMain", mixinStandardHelpOptions = true, version = "0.7")
public class cmMain implements Runnable {

    @Option(names = {"--RollingAverage"}, defaultValue = "80", paramLabel = "Rolling Average", description = "The value that should be used in the rolling average, format: input 80 for 80/20 rolling average, Defaults to 80")
    int rollingAverage;

    @Option(names = {"-s", "--secure"},
            description = "Secure mode, only engages with orchestrators using SSL")
    private boolean secure;

    @Parameters(index = "0", paramLabel = "port", description = "The port the orchestrator should run on")
    private int port;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new cmMain()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        if (!secure) {
            Orchestrator orchestrator = null;
            orchestrator = new Orchestrator(port,rollingAverage);
            orchestrator.run();
        } else {
            try {
                new SecureOrchestrator(port,rollingAverage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
