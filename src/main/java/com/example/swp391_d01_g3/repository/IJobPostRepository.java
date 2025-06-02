package com.example.swp391_d01_g3.repository;

import com.example.swp391_d01_g3.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IJobPostRepository extends JpaRepository<JobPost,Long> {

    @Query("SELECT jp FROM JobPost jp JOIN FETCH jp.employer WHERE jp.jobPostId = ?1")
    List<JobPost> findAllWithEmployer(Long id);
}



