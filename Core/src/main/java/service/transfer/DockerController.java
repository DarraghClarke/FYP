package service.transfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class DockerController {
    private Runtime rt = Runtime.getRuntime();
    Process pr;
    public void launchServiceOnNode(File newService) {
        //todo shutdown any old service before loading new one
        //load new service into docker
        try {
            System.out.println("in the launch phase2 "+newService.getName()+" and its at "+newService.getAbsolutePath());
            rt.exec("docker load < service.tar");
            Thread.sleep(5000);
            //System.out.println("in the launch phas3");
            pr = rt.exec("docker run sample");//todo make generic
            //System.out.println("in the launch phas4");
            //This while loop prints the output of the docker image a real file would be different
            while (true) {
                BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line = r.readLine();
                if (line != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader sendInput(String input) throws IOException {
        pr = rt.exec(input);
        return new BufferedReader(new InputStreamReader(pr.getInputStream()));
    }
}
