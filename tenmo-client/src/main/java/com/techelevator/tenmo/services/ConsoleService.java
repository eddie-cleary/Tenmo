package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    // Shows a list of all users and their ids in the database
    public void printAllUsers() {
        ResponseEntity<User[]> response = restTemplate.getForEntity(baseUrl + "users", User[].class);
        User[] users = response.getBody();
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID\tName");
        System.out.println("-------------------------------------------");
        for (User user : users) {
            System.out.println(user.getId()+"\t"+user.getUsername());
        }
        System.out.println("-------------------------------------------");
    }

//    public void printCompletedTransfers() {
//        ResponseEntity<Transfer[]> response = restTemplate.getForEntity(baseUrl + "transfer/completed", Transfer[].class);
//        Transfer[] completedTransfers = response.getBody();
//        System.out.println("-------------------------------------------");
//        System.out.println("Transfers");
//        System.out.println("ID\tFrom/To\tAmount");
//        System.out.println("-------------------------------------------");
//        for (Transfer transfer : transfers) {
//            System.out.println(user.getId()+"\t"+user.getUsername());
//        }
//        System.out.println("-------------------------------------------");
//    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.next();
    }

    public Long promptForUserId(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                Long userId = scanner.nextLong();
                if (userId != 0L) {
                    return userId;
                } else {
                    return 0L;
                }
            } catch (NumberFormatException e) {
                System.err.println("Please enter a number.");
            }
        }
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.next());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }


    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
