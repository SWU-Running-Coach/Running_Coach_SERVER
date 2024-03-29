package com.example.runningcoach.repository;

import com.example.runningcoach.entity.Running;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RunningRepository extends JpaRepository<Running, Long> {

	List<Running> findByUserUserId(Long userId);
	List<Running> findByUserEmailAndDateTime(String email, LocalDateTime dateTime);

	@Query("SELECT r FROM Running r WHERE EXTRACT(MONTH FROM r.dateTime) = :month AND EXTRACT(YEAR FROM r.dateTime) = :year AND r.user.email = :email")
	List<Running> findByDateTimeYearAndMonthAndUserEmail(@Param("year") int year, @Param("month") int month, @Param("email")String email);

	@Query("SELECT r FROM Running r WHERE EXTRACT(MONTH FROM r.dateTime) = :month AND EXTRACT(YEAR FROM r.dateTime) = :year AND EXTRACT(DAY FROM r.dateTime) = :day AND r.user.email = :email")
	List<Running> findByDateTimeYearAndMonthAndDayAndUserEmail(@Param("year") int year, @Param("month") int month, @Param("day") int day, @Param("email")String email);

}
