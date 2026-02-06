package com.exam.webhooksql;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {

        RestTemplate restTemplate = new RestTemplate();
        String generateWebhookUrl =
                "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "John Doe");
        requestBody.put("regNo", "REG12348"); 
        requestBody.put("email", "john@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        generateWebhookUrl,
                        requestEntity,
                        Map.class
                );

        String webhookUrl =
                response.getBody().get("webhook").toString();

        String accessToken =
                response.getBody().get("accessToken").toString();

        String finalQuery =
                "select d.DEPARTMENT_NAME, " +
                "avg(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())) as AVERAGE_AGE, " +
                "group_concat(concat(e.FIRST_NAME, ' ', e.LAST_NAME) " +
                "order by e.EMP_ID SEPARATOR ', ') as EMPLOYEE_LIST " +
                "from DEPARTMENT d " +
                "join EMPLOYEE e on d.DEPARTMENT_ID = e.DEPARTMENT " +
                "join PAYMENTS p on e.EMP_ID = p.EMP_ID " +
                "where p.AMOUNT > 70000 " +
                "group by d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
                "order by d.DEPARTMENT_ID desc";

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.set("Authorization", accessToken);

        Map<String, String> answerBody = new HashMap<>();
        answerBody.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> answerEntity =
                new HttpEntity<>(answerBody, authHeaders);

        restTemplate.postForEntity(
                webhookUrl,
                answerEntity,
                String.class
        );

        System.out.println("successfully submitted");
    }
}
