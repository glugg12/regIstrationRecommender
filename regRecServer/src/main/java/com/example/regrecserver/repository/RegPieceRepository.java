package com.example.regrecserver.repository;

import com.example.regrecserver.entity.ElementNode;
import com.example.regrecserver.entity.RegPiece;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RegPieceRepository extends CrudRepository<RegPiece, Integer> {
    Optional<RegPiece> findRegPieceByContentIgnoreCase(String content);
    List<RegPiece> findAllByParent(ElementNode parent);
}
