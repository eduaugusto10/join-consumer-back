package com.join.consumer.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.join.consumer.models.Client;
import com.join.consumer.repositories.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class ClientConsumerService {

    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    public ClientConsumerService(ClientRepository clientRepository, ObjectMapper objectMapper) {
        this.clientRepository = clientRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "true", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) {
        log.info("Mensagem recebida no consume: {}", message);
        try {
            Client client = objectMapper.readValue(message, Client.class);
            Client savedClient = clientRepository.save(client);
            log.info("Cliente salvo no banco de dados com sucesso. ID: {}", savedClient);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem do Kafka: {}", e.getMessage());
        }
    }

}
