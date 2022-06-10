package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private AuthenticatedUser currentUser;

    public AccountService(String url) {
        this.baseUrl = url;
    }

    public String getUserBalance() {
        // Create a http entity that includes headers containing the current user's jwt
        HttpEntity<String> entity = new HttpEntity<String>(createAuthHeader());
        try {
            // use the exchange method of restTemplate to make a get request containing the logged-in user's jwt, to
            // obtain that specific user's balance
            ResponseEntity response =
                    restTemplate.exchange(baseUrl + "account", HttpMethod.GET, entity, String.class);
            return response.getBody().toString();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return "";
    }

    public String createNewTransfer(Long senderId, Long receiverId, BigDecimal amount) {
        Transfer transfer = new Transfer();
        transfer.setSenderId(senderId);
        transfer.setReceiverId(receiverId);
        transfer.setAmount(amount);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, createAuthHeader());
        transfer = restTemplate.postForObject(baseUrl + "transfer", entity, Transfer.class);
        return "";
    }

    // Sets headers of the currentUser's jwt
    public HttpHeaders createAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return headers;
    }

    public AuthenticatedUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }
}
