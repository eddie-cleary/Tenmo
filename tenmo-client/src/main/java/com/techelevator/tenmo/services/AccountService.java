package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import okhttp3.Response;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.http.HttpResponse;

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

    // Method called when user sends money to another user.
    public boolean sendTransfer(Transfer transfer) {
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, createAuthHeader());
        try {
            return restTemplate.postForObject(baseUrl + "transfer", entity, Boolean.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }
        return false;
    }

    public User getUserByUserId(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeader());
        try {
            ResponseEntity<User> response = restTemplate.exchange(baseUrl + "users/" + id, HttpMethod.GET, entity, User.class);
            return response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }
        return null;
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
