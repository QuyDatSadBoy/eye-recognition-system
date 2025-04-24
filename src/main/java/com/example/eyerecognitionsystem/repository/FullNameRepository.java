package com.example.eyerecognitionsystem.repository;


import com.example.eyerecognitionsystem.entity.FullName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FullNameRepository extends JpaRepository<FullName, Integer> {
}
