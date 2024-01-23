package com.example.regrecserver.repository;

import com.example.regrecserver.entity.Edge;
import com.example.regrecserver.entity.ElementNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EdgeRepository extends CrudRepository<Edge, Integer> {

    @NotNull List<Edge> findAll();
    List<Edge> findByNode1OrNode2(ElementNode node1, ElementNode node2);

    Optional<Edge> findByNode1AndNode2(ElementNode node1, ElementNode node2);
}
