package com.workhub.repository;

import com.workhub.model.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem, UUID> {
}
