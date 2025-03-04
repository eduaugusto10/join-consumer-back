package com.join.consumer.repositories;

import com.join.consumer.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByCpf(String cpf);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByEmailOrCpf(String cpf, String email);
}
