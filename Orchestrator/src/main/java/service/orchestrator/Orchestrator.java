package service.orchestrator;

import java.io.File;
import java.util.*;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


//@RestController
public class Orchestrator {

    public static void main(String[] args) {
       /** RestTemplate restTemplate = new RestTemplate();

        System.out.println("tried");


        //File service= new File("main/resources/docker.tar");
        //System.out.println(service.getName());
        HttpEntity<String> request = new HttpEntity<>("lol");

        restTemplate.postForObject("http://192.168.99.100:8080/go", request, String.class);

        System.out.println("Sent at all");*/

        RestTemplate restTemplate = new RestTemplate();

        System.out.println("tried");


        File service= new File("main/resources/docker.tar");
        System.out.println(service.getName());
        HttpEntity<File> request = new HttpEntity<>(service);

        restTemplate.postForObject("http://192.168.99.1:8081/Service", request, File.class);
        System.out.println("trisad");

    }
    /**private int numberOfApplications = 0;
    public List<Quotation> getQuotations(ClientInfo info) {
        List<Quotation> quotations = new LinkedList<>();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ClientInfo> request = new HttpEntity<>(info);
        String[] hosts= new String[]{"auldfellas:8081","dodgydrivers:8082","girlpower:8083"};

        for(String host: hosts){
            quotations.add(restTemplate.postForObject("http://"+host+"/quotations", request, Quotation.class));
        }

        return quotations;
    }
    private Map<Integer, ClientApplication> applications = new HashMap<>();

    @RequestMapping(value="/applications", method= RequestMethod.POST)
    public ClientApplication createQuotation(@RequestBody ClientInfo info){
        System.out.println("made it");
        ClientApplication clientApplication = new ClientApplication(info, numberOfApplications, getQuotations(info));
        //clientApplication.setQuotations(getQuotations(info));

        applications.put(clientApplication.getApplicationNumber(), clientApplication);


        increment();
        return clientApplication;
    }

    @RequestMapping(value="/applications/{reference}",method=RequestMethod.GET)
    public ClientApplication getResource(@PathVariable("reference") Integer reference) throws NoSuchQuotationException {
        ClientApplication clientApplication = applications.get(reference);
        if (clientApplication == null) {
            throw new NoSuchQuotationException();
        }
        return clientApplication;
    }

    @RequestMapping(value="/applications",method=RequestMethod.GET)
    public ArrayList<ClientApplication> listApplications() {
        ArrayList<ClientApplication> list = new ArrayList<>();

        for(ClientApplication application: applications.values()) {
            list.add(application);
        }

        return list;
    }


    public void increment(){
        numberOfApplications++;
    }**/
}
