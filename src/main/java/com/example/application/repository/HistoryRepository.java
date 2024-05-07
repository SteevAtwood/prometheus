package com.example.application.repository;

import org.springframework.stereotype.Repository;

import com.example.application.data.History;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

}
