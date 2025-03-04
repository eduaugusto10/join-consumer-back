package com.join.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.join.consumer.models.Client;
import com.join.consumer.repositories.ClientRepository;
import com.join.consumer.services.ClientConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientConsumerServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ClientConsumerService clientConsumerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(clientConsumerService, "topic", "test-topic");
        ReflectionTestUtils.setField(clientConsumerService, "groupId", "test-group");

    }

    @Test
    void consume_ValidMessage_ClientSavedSuccessfully() throws Exception {
        UUID uuid = UUID.randomUUID();
        String message = "{\"name\": \"Test Client\", \"email\": \"test@example.com\"}";
        Client client = Client.builder()
                .name("Test Client")
                .email("test@example.com")
                .build();
        Client savedClient = Client.builder()
                .id(uuid)
                .name("Test Client")
                .email("test@example.com")
                .build();
        when(objectMapper.readValue(message, Client.class)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(savedClient);

        clientConsumerService.consume(message);

        verify(objectMapper, times(1)).readValue(message, Client.class);
        verify(clientRepository, times(1)).save(any(Client.class));
    }


    @Test
    void consume_NullMessage_ThrowsException() {
        String message = null;
        assertDoesNotThrow(() -> clientConsumerService.consume(message));
        verify(clientRepository, never()).save(any(Client.class));
    }
}
