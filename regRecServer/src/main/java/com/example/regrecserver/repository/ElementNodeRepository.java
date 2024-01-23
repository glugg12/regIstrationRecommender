package com.example.regrecserver.repository;

import com.example.regrecserver.entity.ElementNode;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ElementNodeRepository extends CrudRepository<ElementNode, Integer> {
    List<ElementNode> findAllByNameIgnoreCase(String name);
}
