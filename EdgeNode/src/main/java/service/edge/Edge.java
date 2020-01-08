package service.edge;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class Edge {
    /**
     * This is the starting point for the application. Here, we must
     * get a reference to the Broker Service and then invoke the
     * getQuotations() method on that service.
     * <p>
     * Finally, you should print out all quotations returned
     * by the service.
     *
     * @param args
     */

    //right now assuming only one service per node but that can change xxoxo kissy face

    private File service;
    //   public static void main(String[] args) {

    /**
     * RestTemplate restTemplate = new RestTemplate();
     * <p>
     * <p>
     * for(int i=0; i<clients.length ;i++) {
     * HttpEntity<ClientInfo> request = new HttpEntity<>(clients[i]);
     * <p>
     * restTemplate.postForObject("http://192.168.99.100:8080/applications", request, ClientInfo.class);
     * }
     * <p>
     * ClientApplication[] clientApplications = restTemplate.getForObject("http://192.168.99.100:8080/applications", ClientApplication[].class);
     * <p>
     * for(ClientApplication clientApplication: clientApplications){
     * displayProfile(clientApplication.getClientInfo());
     * quotations = clientApplication.getQuotations();
     * <p>
     * for (Quotation quotation : quotations) {
     * displayQuotation(quotation);
     * }
     * System.out.println("\n");
     * }
     **/


    // }
    @RequestMapping(value = "/Service", method = RequestMethod.GET)
    public File getService() {


        return service;
    }

    @RequestMapping(value = "/Service", method = RequestMethod.POST)
    public void addService(@RequestBody File service) throws InterruptedException {
        this.service = service;
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("docker load < " + service.getName());
            Thread.sleep(5000);
            Process pr = rt.exec("docker run sample");
            //This while loop prints the output of the docker image
            while (true) {
                BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line = r.readLine();
                if (line != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
