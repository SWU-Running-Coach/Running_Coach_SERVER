package com.example.runningcoach.repository;

import com.example.runningcoach.entity.Running;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunningRepository extends JpaRepository<Running, Long> {

	List<Running> findByUserUserId(Long userId);
}