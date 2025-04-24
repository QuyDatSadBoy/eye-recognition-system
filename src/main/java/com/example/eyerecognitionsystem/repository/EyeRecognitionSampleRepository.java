package com.example.eyerecognitionsystem.repository;



import com.example.eyerecognitionsystem.entity.EyeRecognitionSample;
import com.example.eyerecognitionsystem.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EyeRecognitionSampleRepository extends JpaRepository<EyeRecognitionSample, Integer> {

    List<EyeRecognitionSample> findByMember(Member member);

    List<EyeRecognitionSample> findByMemberId(Integer memberId);

    List<EyeRecognitionSample> findByIsActive(Integer isActive);
}
