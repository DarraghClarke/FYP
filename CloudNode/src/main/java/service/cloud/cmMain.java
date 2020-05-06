package service.cloud;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.net.URI;

@CommandLine.Command(name = "cmMain", mixinStandardHelpOptions = true, version = "0.6")
public class cmMain implements Runnable {

    @Option(names = {"-s", "--secure"},
            description = "Secure mode, only engages with orchestrator using SSL")
    private boolean secure;

    @Parameters(index = "0", paramLabel = "address", description = "The address of the orchestrator format wss://{ip}:{port}")
    private URI address;

    @Parameters(index = "1", paramLabel = "file", description = "The location of the service you wish to run")
    private File file;

    @Parameters(index = "2", paramLabel = "serviceAddress", description = "The address any services will run out of on this machine {ip}:{port}")
    private URI port;

    @Override
    public void run() {
        if (!secure) {
            Cloud cloud = new Cloud(address, file, port,secure);
            cloud.run();
        } else {
            try {
                new SSLMain(address, file, port,secure);
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
