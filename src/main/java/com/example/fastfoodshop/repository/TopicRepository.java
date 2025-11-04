package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByIsDeletedFalse();
}
