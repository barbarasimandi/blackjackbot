package com.greenfox.blackjackbot.controller;

import com.greenfox.blackjackbot.model.Message;
import com.greenfox.blackjackbot.model.MessageResponse;
import com.greenfox.blackjackbot.model.Messaging;
import com.greenfox.blackjackbot.model.Packet;
import com.greenfox.blackjackbot.util.LoggingRequestInterceptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/webhook")
public class Webhook {

        private static final String verify = "abc123";

        @GetMapping({"/", ""})
        public String verify(@RequestParam("hub.challenge") String challenge) {
            return challenge;
        }

        @PostMapping({"/", ""})
        public String receive(@RequestBody Packet p) {
            try {
                System.out.println(p.object);
                System.out.println("Message received from: " + p.entry.get(0).messaging.get(0).sender.id);
                sendResponse(p);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "received";
        }

        public void sendResponse(Packet p) {
            RestTemplate template = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
            String token = "EAAa1wwKnEQUBABrLJs37UOTpZBBcLhtga0UdYO0r0RhQOFRjOdXfS6vhxnbNeKcIZBGRlzSqk1ZB9UQFZBFXWn3SVre0gZBhLS3OZARGbzFdB1hoiyiZASxZCiaErCzpbGZBMe3g5G3vL1UDh2DTM6hLkYpG5LATVwwQjZC17HYuIlUyvBHOeTZAO42";

            Messaging response = new Messaging();
            response.recipient = p.entry.get(0).messaging.get(0).sender;
            response.message = new Message();
            response.message.text = "Well, you too with this: " + p.entry.get(0).messaging.get(0).message.text;
            System.out.println(response.message.text);

            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new LoggingRequestInterceptor());
            template.setInterceptors(interceptors);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Messaging> entity = new HttpEntity<>(response, headers);
            LoggingRequestInterceptor.log.debug("mukodik a logger");
            try {
                template.exchange(
                        "https://graph.facebook.com/v2.6/me/messages?access_token="+token,
                        HttpMethod.POST,
                        entity,
                        MessageResponse.class
                );
            } catch (ResourceAccessException exception) {
                exception.printStackTrace();
            }
        }
    }

