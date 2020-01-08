package service.cloud;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@RestController
public class Cloud {
    /**
     * This is the starting point for the application. Here, we must
     * get a reference to the Broker Service and then invoke the
     * getQuotations() method on that service.
     *
     * Finally, you should print out all quotations returned
     * by the service.
     *
     * @param args
     */

    //right now assuming only one service per node but that can change xxoxo kissy face

    private File service;
//    public static void main(String[] args) {
/**        RestTemplate restTemplate = new RestTemplate();


 for(int i=0; i<clients.length ;i++) {
 HttpEntity<ClientInfo> request = new HttpEntity<>(clients[i]);

 restTemplate.postForObject("http://192.168.99.100:8080/applications", request, ClientInfo.class);
 }

 ClientApplication[] clientApplications = restTemplate.getForObject("http://192.168.99.100:8080/applications", ClientApplication[].class);

 for(ClientApplication clientApplication: clientApplications){
 displayProfile(clientApplication.getClientInfo());
 quotations = clientApplication.getQuotations();

 for (Quotation quotation : quotations) {
 displayQuotation(quotation);
 }
 System.out.println("\n");
 }
 **/

//    sendService();
//    }
    @RequestMapping(value="/go", method= RequestMethod.POST)
    public static void sendService(@RequestBody String ok){
        RestTemplate restTemplate = new RestTemplate();

        System.out.println("tried"+ok);


        File service= new File("main/resources/docker.tar");
        System.out.println(service.getName());
        HttpEntity<File> request = new HttpEntity<>(service);

        restTemplate.postForObject("http://192.168.99.100:8081/service", request, File.class);

        System.out.println("Sent at all");

    }

    @RequestMapping(value="/go", method= RequestMethod.GET)
    public static String sendService(){
        RestTemplate restTemplate = new RestTemplate();



        File service= new File("main/resources/docker.tar");
        System.out.println(service.getName());
        HttpEntity<File> request = new HttpEntity<>(service);

        restTemplate.postForObject("http://192.168.99.100:8081/service", request, File.class);

        System.out.println("Sent at all");
return "ok";
    }


    @RequestMapping(value="/Service",method=RequestMethod.GET)
    public File getService(){


        return service;
    }

    @RequestMapping(value="/Service", method= RequestMethod.POST)
    public void addService(@RequestBody File service){
        this.service=service;
    }
}
