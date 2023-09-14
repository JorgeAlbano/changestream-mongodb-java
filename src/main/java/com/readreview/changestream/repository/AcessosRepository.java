package com.readreview.changestream.repository;

import com.readreview.changestream.domain.Acessos;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AcessosRepository extends MongoRepository<Acessos, String> {
}
