package com.example.application.repository;

import com.example.application.data.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HistoryRepository extends JpaRepository<History, Integer>, JpaSpecificationExecutor<History> {
}
