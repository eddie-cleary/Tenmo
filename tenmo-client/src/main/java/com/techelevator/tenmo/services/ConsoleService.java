package com.techelevator.tenmo.services;


import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final String baseUrl;
    public ConsoleService(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    private RestTemplate restTemplate = new RestTemplate();

    private AuthenticatedUser currentUser;

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.next());
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
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeader());
        ResponseEntity<User[]> response = restTemplate.exchange(baseUrl + "users", HttpMethod.GET, entity, User[].class);
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

    public Transfer[] printCompletedTransfers() {
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeader());
        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfer/completed", HttpMethod.GET, entity, Transfer[].class);
        Transfer[] completedTransfers = response.getBody();
        if (completedTransfers == null) {
            System.err.println("You have no completed transfers.");
            System.out.println();
            return null;
        }
        System.out.println("--------------------------------------------");
        System.out.printf("%-18s%-18s%-18s\n","ID","From/To","Amount");
        System.out.println("--------------------------------------------");
        for (Transfer transfer : completedTransfers) {
            boolean isCurrentUserSender = (currentUser.getUser().getId().equals(transfer.getSender().getId()));
            System.out.printf("%-18s%-18s%-18s\n",transfer.getTransferId(),(isCurrentUserSender ? "To: " : "From: ") + (isCurrentUserSender ? transfer.getReceiver().getUsername() : transfer.getSender().getUsername()),"$"+transfer.getAmount());
        }
        System.out.println("--------------------------------------------");
        return completedTransfers;
    }

    public Long promptForTransferDetails(Transfer[] transfers) {
        Long transactionId = 1L;
        while (!(transactionId.equals(0L))) {
            transactionId = promptForLong("Please enter transfer ID to view details (0 to cancel): ");
            if ((transactionId.equals(0L))) {
                return 0L;
            }
            printTransactionDetails(transactionId);
            pause();
            System.out.println();
        }
        return 0L;
    }

    public Transfer[] printPendingTransfers() {
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeader());
        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfer/pending", HttpMethod.GET, entity, Transfer[].class);
        Transfer[] pendingTransfers = response.getBody();
        if (pendingTransfers == null) {
            return null;
        }
        System.out.println("----------------------------------------------------");
        System.out.printf("%-12s%-32s%-24s\n","ID","From","Amount");
        System.out.println("----------------------------------------------------");
        for (Transfer transfer : pendingTransfers) {
            System.out.printf("%-12s%-32s%-24s\n",transfer.getTransferId(), "From: " + transfer.getReceiver().getUsername(), "$"+transfer.getAmount());
        }
        System.out.println("----------------------------------------------------");
        return pendingTransfers;
    }

    public Long handleRequestApproval(Transfer[] transfers, AccountService accountService) {
        Long userInput = promptForLong("Please enter transfer ID to approve/reject (0 to cancel): ");
        if (userInput.equals(0L)) {
            return userInput;
        }
        Transfer selectedTransfer = null;
        for (Transfer transfer : transfers) {
            if (userInput.equals(transfer.getTransferId())) {
                selectedTransfer = transfer;
            }
        }
        if (selectedTransfer == null) {
            System.err.println("Transfer ID not valid, please re-enter.");
            return 1L;
        }
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("-----------");
        int menuSelection = promptForInt("Please choose an option: ");
        switch (menuSelection) {
            case 1:
                selectedTransfer.setStatus(TransferStatus.APPROVED);
                if (accountService.sendTransfer(selectedTransfer)) {
                    System.out.println("Transfer approved.");
                    System.out.println();
                } else {
                    System.err.println("Error approving transfer.");
                }
                break;
            case 2:
                selectedTransfer.setStatus(TransferStatus.REJECTED);
                if (accountService.sendTransfer(selectedTransfer)) {
                    System.out.println("Transfer rejected.");
                    System.out.println();
                } else {
                    System.err.println("Error rejecting transfer.");
                }
                break;
            case 0:
                return 1L;
        }
        return 1L;
    }


    public void printTransactionDetails(Long transactionId) {
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeader());
        ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "transfer/" + transactionId, HttpMethod.GET, entity, Transfer.class);
        Transfer transfer = response.getBody();
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------");
        System.out.println("Id: " + transfer.getTransferId());
        System.out.println("From: " + transfer.getSender().getUsername());
        System.out.println("To: " + transfer.getReceiver().getUsername());
        System.out.println("Type: " + transfer.getType());
        System.out.println("Status: " + transfer.getStatus());
        System.out.println("Amount: " + transfer.getAmount());
        System.out.println("--------------------------------------------");
    }

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

    public Long promptForLong(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
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

    public HttpHeaders createAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return headers;
    }


    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public AuthenticatedUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }
}
