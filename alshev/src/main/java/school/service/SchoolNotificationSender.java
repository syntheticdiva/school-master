package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;


//todo: написать самой на url из subscriberDto отправить schoolUpdateDto

@Slf4j
@Service
public class SchoolNotificationSender {

    private final RestTemplate restTemplate;

    @Autowired
    public SchoolNotificationSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public void sendCreate(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        log.info("Sending creation to " + subscriberDto.getUrl() + " message: " + schoolEntityDTO.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SchoolEntityDTO> requestEntity = new HttpEntity<>(schoolEntityDTO, headers);

        try {
            restTemplate.postForObject(subscriberDto.getUrl(), requestEntity, Void.class);
            log.info("Creation notification sent successfully to " + subscriberDto.getUrl());
        } catch (Exception e) {
            log.error("Failed to send creation notification to " + subscriberDto.getUrl(), e);
        }
    }
    public void sendUpdate(SchoolUpdateDto schoolUpdateDto, SubscriberDto subscriberDto) {
        log.info("Sending to " + subscriberDto.getUrl() + " message: " + schoolUpdateDto.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SchoolUpdateDto> requestEntity = new HttpEntity<>(schoolUpdateDto, headers);

        try {
            restTemplate.postForObject(subscriberDto.getUrl(), requestEntity, Void.class);
            log.info("Notification sent successfully to " + subscriberDto.getUrl());
        } catch (Exception e) {
            log.error("Failed to send notification to " + subscriberDto.getUrl(), e);
        }
    }
    public void sendDelete(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        log.info("Sending deletion to " + subscriberDto.getUrl() + " message: " + schoolEntityDTO.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SchoolEntityDTO> requestEntity = new HttpEntity<>(schoolEntityDTO, headers);

        try {
            restTemplate.postForObject(subscriberDto.getUrl(), requestEntity, Void.class);
            log.info("Deletion notification sent successfully to " + subscriberDto.getUrl());
        } catch (Exception e) {
            log.error("Failed to send deletion notification to " + subscriberDto.getUrl(), e);
        }
    }
}