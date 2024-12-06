package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.dto.*;
import school.mapper.SubscriberMapper;
import school.repository.SubscriberRepository;

import java.time.LocalDateTime;


//todo: написать самой на url из subscriberDto отправить schoolUpdateDto
@Slf4j
@Service
public class SchoolNotificationSender {

    private final RestTemplate restTemplate;
    private final SubscriberRepository subscriberRepository;
    private final SubscriberMapper subscriberMapper;

    @Autowired
    public SchoolNotificationSender(
            RestTemplate restTemplate,
            SubscriberRepository subscriberRepository,
            SubscriberMapper subscriberMapper
    ) {
        this.restTemplate = restTemplate;
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
    }

    public void sendCreate(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        log.info("Sending creation to {} message: {}", subscriberDto.getUrl(), schoolEntityDTO);

        SchoolOnCreateDto createDto = new SchoolOnCreateDto(
                schoolEntityDTO.getId(),
                schoolEntityDTO.getName(),
                schoolEntityDTO.getAddress(),
                LocalDateTime.now()
        );

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    subscriberDto.getUrl(),
                    createDto,
                    String.class
            );

            log.info("Creation notification sent successfully to {}. Response: {}",
                    subscriberDto.getUrl(), response);
        } catch (RestClientException e) {
            log.error("Failed to send creation notification to {}", subscriberDto.getUrl(), e);
        }
    }

    public void sendUpdate(SchoolUpdateDto schoolUpdateDto, SubscriberDto subscriberDto) {
        log.info("Sending update to {} message: {}", subscriberDto.getUrl(), schoolUpdateDto);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    subscriberDto.getUrl(),
                    schoolUpdateDto,
                    String.class
            );

            log.info("Update notification sent successfully to {}. Response: {}",
                    subscriberDto.getUrl(), response);
        } catch (RestClientException e) {
            log.error("Failed to send update notification to {}", subscriberDto.getUrl(), e);
        }
    }

    public void sendDelete(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
        log.info("Sending deletion to {} message: {}", subscriberDto.getUrl(), schoolEntityDTO);

        SchoolOnDeleteDto deleteDto = new SchoolOnDeleteDto(
                schoolEntityDTO.getId(),
                schoolEntityDTO.getName(),
                schoolEntityDTO.getAddress()
        );

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    subscriberDto.getUrl(),
                    deleteDto,
                    String.class
            );

            log.info("Deletion notification sent successfully to {}. Response: {}",
                    subscriberDto.getUrl(), response);
        } catch (RestClientException e) {
            log.error("Failed to send deletion notification to {}", subscriberDto.getUrl(), e);
        }
    }
}
//@Slf4j
//@Service
//public class SchoolNotificationSender {
//
//    private final RestTemplate restTemplate;
//    private final SubscriberRepository subscriberRepository;
//    private final SubscriberMapper subscriberMapper;
//
//    @Autowired
//    public SchoolNotificationSender(RestTemplate restTemplate, SubscriberRepository subscriberRepository, SubscriberMapper subscriberMapper) {
//        this.restTemplate = restTemplate;
//        this.subscriberRepository = subscriberRepository;
//        this.subscriberMapper = subscriberMapper;
//    }
//
//    public void sendCreate(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
//        log.info("Sending creation to {} message: {}", subscriberDto.getUrl(), schoolEntityDTO);
//
//        SchoolOnCreateDto createDto = new SchoolOnCreateDto();
//        createDto.setSchoolId(schoolEntityDTO.getId());
//        createDto.setName(schoolEntityDTO.getName());
//        createDto.setAddress(schoolEntityDTO.getAddress());
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                    subscriberDto.getUrl(),
//                    createDto,
//                    String.class
//            );
//
//            log.info("Notification sent successfully to {}. Response: {}",
//                    subscriberDto.getUrl(), response);
//        } catch (RestClientException e) {
//            log.error("Failed to send notification to {}", subscriberDto.getUrl(), e);
//        }
//    }
//
//    public void sendUpdate(SchoolUpdateDto schoolUpdateDto, SubscriberDto subscriberDto) {
//        log.info("Sending to " + subscriberDto.getUrl() + " message: " + schoolUpdateDto.toString());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<SchoolUpdateDto> requestEntity = new HttpEntity<>(schoolUpdateDto, headers);
//
//        try {
//            restTemplate.postForObject(subscriberDto.getUrl(), requestEntity, Void.class);
//            log.info("Notification sent successfully to " + subscriberDto.getUrl());
//        } catch (Exception e) {
//            log.error("Failed to send notification to " + subscriberDto.getUrl(), e);
//        }
//    }
//
//    public void sendDelete(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriberDto) {
//        try {
//            SchoolOnDeleteDto deleteDto = new SchoolOnDeleteDto(
//                    schoolEntityDTO.getId(),
//                    schoolEntityDTO.getName(),
//                    schoolEntityDTO.getAddress()
//            );
//
//            restTemplate.postForEntity(subscriberDto.getUrl(), deleteDto, Void.class);
//            log.info("Deletion notification sent successfully to {}", subscriberDto.getUrl());
//        } catch (RestClientException e) {
//            log.error("Failed to send deletion notification to {}", subscriberDto.getUrl(), e);
//        }
//    }
//}