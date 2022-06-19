package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final String baseUrl;

    private final AccountService accountService;

    private RestTemplate restTemplate = new RestTemplate();

    private AuthenticatedUser currentUser;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleService(String baseUrl, AccountService accountService) {
        this.baseUrl = baseUrl;
        this.accountService = accountService;
    }

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
        User[] users = accountService.getAllUsers();
        System.out.println("\n-------------------------------------------");
        System.out.println("Users");
        System.out.printf("%-18s%-18s\n","ID","Name");
        System.out.println("-------------------------------------------");
        for (User user : users) {
            System.out.printf("%-18s%-18s\n",user.getId(),user.getUsername());
        }
        System.out.println("-------------------------------------------");
    }

    public void printCompletedTransfers() {
        Transfer[] completedTransfers = accountService.getCompletedTransfers();
        if (completedTransfers == null) {
            System.out.println("\nYou have no transfer history.");
            return;
        }
        System.out.println("\n--------------------------------------------");
        System.out.printf("%-18s%-18s%-18s\n","ID","From/To","Amount");
        System.out.println("--------------------------------------------");
        for (Transfer transfer : completedTransfers) {
            boolean isCurrentUserSender = (currentUser.getUser().getId().equals(transfer.getSender().getId()));
            System.out.printf("%-18s%-18s%-18s\n",transfer.getTransferId(),(isCurrentUserSender ? "To: " : "From: ") + (isCurrentUserSender ? transfer.getReceiver().getUsername() : transfer.getSender().getUsername()),"$"+transfer.getAmount());
        }
        System.out.println("--------------------------------------------");
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

    public Transfer promptForSendTransfer() {
        User[] allUsers = accountService.getAllUsers();
        Transfer transfer = new Transfer();
        Long transferId = promptForLong("\nEnter ID of user you are sending to (0 to cancel): ");
        User selectedUser = null;
        for (User user : allUsers) {
            if (transferId.equals(user.getId())) {
                selectedUser = user;
            }
        }
        if (selectedUser == null) {
            System.out.println("User not found. Please try again.");
            return null;
        }
        transfer.setSender(currentUser.getUser());
        transfer.setReceiver(selectedUser);
        transfer.setStatus(TransferStatus.APPROVED);
        transfer.setType(TransferType.SEND);
        transfer.setAmount(promptForBigDecimal("Enter amount: "));
        return transfer;
    }

    public void printPendingTransfers() {
        Transfer[] pendingTransfers = accountService.getPendingTransfers();
        if (pendingTransfers == null) {
            System.out.println("\nThere are no pending requests.");
            return;
        }
        System.out.println("----------------------------------------------------");
        System.out.printf("%-12s%-32s%-24s\n","ID","From","Amount");
        System.out.println("----------------------------------------------------");
        for (Transfer transfer : pendingTransfers) {
            System.out.printf("%-12s%-32s%-24s\n",transfer.getTransferId(), "From: " + transfer.getReceiver().getUsername(), "$"+transfer.getAmount());
        }
        System.out.println("----------------------------------------------------");
        handleRequestApproval(pendingTransfers);
    }

    public void handleRequestApproval(Transfer[] transfers) {
        Long userInput = -1L;
        userInput = promptForLong("Please enter transfer ID to approve/reject (0 to cancel): ");
        if (userInput.equals(0L)) {
            return;
        } else {
            Transfer selectedTransfer = null;
            for (Transfer transfer : transfers) {
                if (userInput.equals(transfer.getTransferId())) {
                    selectedTransfer = transfer;
                }
            }
            if (selectedTransfer == null) {
                System.err.println("Transfer ID not valid, please re-enter.");
                return;
            }
            System.out.println("1: Approve");
            System.out.println("2: Reject");
            System.out.println("0: Don't approve or reject");
            System.out.println("-----------");
            int menuSelection = promptForInt("Please choose an option: ");
            switch (menuSelection) {
                case 1:
                    selectedTransfer.setStatus(TransferStatus.APPROVED);
                    if (accountService.handleSendTransfer(selectedTransfer)) {
                        System.out.println("Transfer approved.");
                    } else {
                        printErrorMessage();
                    }
                    break;
                case 2:
                    selectedTransfer.setStatus(TransferStatus.REJECTED);
                    if (accountService.handleSendTransfer(selectedTransfer)) {
                        System.out.println("Transfer rejected.");
                    } else {
                        printErrorMessage();
                    }
                    break;
                case 0:
                    break;
            }
        }
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
        String username = promptForString("\nUsername: ");
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
                return Long.parseLong(scanner.next());
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
        System.out.print("\nPress Enter to continue...");
        try {
            System.in.read();
        }
        catch(Exception e){

        }
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
