package com.example.eyerecognitionsystem.repository;

import com.example.eyerecognitionsystem.entity.RecognitionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface RecognitionEventRepository extends JpaRepository<RecognitionEvent, Integer> {

    List<RecognitionEvent> findByMemberId(Integer memberId);

    @Query("SELECT e FROM RecognitionEvent e WHERE e.member.id = :memberId AND e.isSuccessful = 1")
    List<RecognitionEvent> findSuccessfulRecognitionByMemberId(@Param("memberId") Integer memberId);

    @Query("SELECT e FROM RecognitionEvent e WHERE e.timeVerify BETWEEN :startDate AND :endDate")
    List<RecognitionEvent> findByTimeVerifyBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT e FROM RecognitionEvent e WHERE e.member.id = :memberId AND e.isSuccessful = 1 AND e.timeVerify BETWEEN :startDate AND :endDate")
    List<RecognitionEvent> findSuccessfulRecognitionByMemberIdAndTimeVerifyBetween(
            @Param("memberId") Integer memberId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT COUNT(e) FROM RecognitionEvent e WHERE e.member.id = :memberId AND e.isSuccessful = 1 AND e.timeVerify BETWEEN :startDate AND :endDate")
    Long countSuccessfulRecognitionByMemberIdAndTimeVerifyBetween(
            @Param("memberId") Integer memberId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}