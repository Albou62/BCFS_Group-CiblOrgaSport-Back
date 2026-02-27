package com.ciblorgasport.demo.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String notificationBaseUrl;
    private final String incidentGroupName;

    public NotificationClient(
        RestTemplate restTemplate,
        ObjectMapper objectMapper,
        @Value("${notification.base-url}") String notificationBaseUrl,
        @Value("${notification.incident-group-name:Incidents}") String incidentGroupName
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.notificationBaseUrl = notificationBaseUrl.endsWith("/")
            ? notificationBaseUrl.substring(0, notificationBaseUrl.length() - 1)
            : notificationBaseUrl;
        this.incidentGroupName = incidentGroupName;
    }

    public Long findIncidentGroupId() {
        String endpoint = String.format(
            "%s/group?name=%s",
            notificationBaseUrl,
            URLEncoder.encode(incidentGroupName, StandardCharsets.UTF_8)
        );

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
            HttpStatusCode status = response.getStatusCode();
            LOGGER.info("notification_call endpoint={} status={}", endpoint, status.value());

            if (!status.is2xxSuccessful() || response.getBody() == null || response.getBody().isBlank()) {
                throw new NotificationClientException("Unexpected response while fetching incident group");
            }

            JsonNode payload = objectMapper.readTree(response.getBody());
            JsonNode groupIdNode = payload.get("groupId");
            if (groupIdNode == null || groupIdNode.isNull()) {
                throw new NotificationClientException("Missing groupId in notification response");
            }
            return Long.parseLong(groupIdNode.asText());
        } catch (HttpStatusCodeException e) {
            LOGGER.error("notification_call_failed endpoint={} status={} body={}", endpoint, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new NotificationClientException("Failed to fetch incident group ID", e);
        } catch (Exception e) {
            throw new NotificationClientException("Failed to fetch incident group ID", e);
        }
    }

    public void subscribeUserToGroup(Long userId, Long groupId) {
        String endpoint = notificationBaseUrl + "/subscription";
        Map<String, Long> payload = Map.of("userId", userId, "groupId", groupId);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, payload, String.class);
            HttpStatusCode status = response.getStatusCode();
            LOGGER.info("notification_call endpoint={} userId={} groupId={} status={}", endpoint, userId, groupId, status.value());

            if (!status.is2xxSuccessful()) {
                throw new NotificationClientException("Failed to subscribe user to incident group");
            }
        } catch (HttpStatusCodeException e) {
            LOGGER.error(
                "notification_call_failed endpoint={} userId={} groupId={} status={} body={}",
                endpoint,
                userId,
                groupId,
                e.getStatusCode().value(),
                e.getResponseBodyAsString()
            );
            throw new NotificationClientException("Failed to subscribe user to incident group", e);
        } catch (Exception e) {
            throw new NotificationClientException("Failed to subscribe user to incident group", e);
        }
    }

    public static class NotificationClientException extends RuntimeException {
        public NotificationClientException(String message) {
            super(message);
        }

        public NotificationClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
