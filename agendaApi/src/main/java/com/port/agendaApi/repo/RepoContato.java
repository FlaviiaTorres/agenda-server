package com.port.agendaApi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.port.agendaApi.model.Contato;

import java.util.Optional;


@Repository
public interface RepoContato extends JpaRepository<Contato, String> {
    Optional<Contato> findById(String id);
} 

