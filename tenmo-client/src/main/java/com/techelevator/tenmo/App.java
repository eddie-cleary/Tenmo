package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final AccountService accountService = new AccountService(API_BASE_URL);

    private final ConsoleService consoleService = new ConsoleService(API_BASE_URL, accountService);

    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    // Instantiate an accountService for handling all account related app functions

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("\nPlease register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("\nRegistration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            // on successful login, set the current user in the account and console services
            accountService.setCurrentUser(currentUser);
            consoleService.setCurrentUser(currentUser);
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        // Print out account balance of current logged in user
        System.out.println("\nYour current balance is: $" + accountService.getUserBalance());
	}

	private void viewTransferHistory() {
		// View transfers that were sent or received by logged in user
        consoleService.printCompletedTransfers();
    }

	private void viewPendingRequests() {
		// Print all pending requests
        consoleService.printPendingTransfers();
	}

	private void sendBucks() {
        consoleService.printAllUsers();
        Transfer transfer = consoleService.promptForSendTransfer();
        if (accountService.handleSendTransfer(transfer)){
            System.out.println("\nSend transfer complete.");
        } else {
            consoleService.printErrorMessage();
        }
	}

	private void requestBucks() {
        Transfer newTransfer = gatherRequestTransferInfo();
        if (newTransfer == null) {
            return;
        }
        if (accountService.handleSendTransfer(newTransfer)) {
            System.out.println("Transfer request complete.");
        }
	}

//    private Transfer gatherSendTransferInfo() {
//
//        if (!validateReceiver(receiverId)) {
//            return null;
//        }
//        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount to send: ");
//        if (!validateAmount(amount)) {
//            return null;
//        }
//        Transfer newTransfer = new Transfer();
//        newTransfer.setReceiver(accountService.getUserByUserId(receiverId));
//        newTransfer.setAmount(amount);
//        newTransfer.setType(TransferType.SEND);
//        newTransfer.setStatus(TransferStatus.APPROVED);
//        return newTransfer;
//    }

    private Transfer gatherRequestTransferInfo() {
        consoleService.printAllUsers();
        Long senderId = consoleService.promptForUserId("Enter ID of user you are requesting from (0 to cancel): ");
        if (!validateReceiver(senderId)) {
            return null;
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount to request: ");
        if (!validateAmount(amount)) {
            return null;
        }
        Transfer newTransfer = new Transfer();
        newTransfer.setSender(accountService.getUserByUserId(senderId));
        newTransfer.setAmount(amount);
        newTransfer.setType(TransferType.REQUEST);
        newTransfer.setStatus(TransferStatus.PENDING);
        return newTransfer;
    }

    private boolean validateReceiver(Long receiverId) {
        User receiver = accountService.getUserByUserId(receiverId);
        if (receiverId.equals(0L)) {
            return false;
        } else if (currentUser.getUser().getId().equals(receiver.getId())) {
            System.err.println("You can not send money to yourself. Please try again.");
            System.out.println();
            return false;
        }
        return true;
    }

    private boolean validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        System.err.println("Please enter an amount greater than $0.00");
        return false;
    }
}
