package com.example.swp391_d01_g3.repository;


import com.example.swp391_d01_g3.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmployerRepository extends JpaRepository<Employer, Integer> {

}
