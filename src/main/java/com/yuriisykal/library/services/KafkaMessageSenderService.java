package com.yuriisykal.library.services;

import com.yuriisykal.library.message.EmailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessageSenderService {
    @Value("${kafka.topic.emailReceived}")
    private String teamEmailReceivedTopic;

    private final KafkaOperations<String, EmailDTO> kafkaOperations;


    public void sendMessage(EmailDTO emailDTO) {
        try {
            kafkaOperations.send(teamEmailReceivedTopic, emailDTO);
        } catch (Exception e) {

        }
    }
}
