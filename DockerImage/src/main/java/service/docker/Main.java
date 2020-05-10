package service.docker;

import java.util.Scanner;

public class Main {

    /**
     * This file simply served as a testing base for a docker image, which saved time on transporting dockerfiles that are multiple GB in size,
     * it simply prints when it launches and then can
     * @param args
     */
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        System.out.println("Sample Service launched, ready for input.....");
        String input = inputScanner.nextLine();
        while(!input.isEmpty())  {
            System.out.println("Input recieved " +input);
        }
    }
}
