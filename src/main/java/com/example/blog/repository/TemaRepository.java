package com.example.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.blog.model.Tema;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemaRepository extends JpaRepository<Tema, Long> {

    public List<Tema> findAllByDescricaoContainsIgnoreCase (@Param("descricao") String descricao);

}
