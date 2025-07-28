package com.example.swp391_d01_g3.service.favorite;

import com.example.swp391_d01_g3.model.FavoriteJob;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.repository.FavoriteJobRepository;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FavoriteJobServiceImpl implements IFavoriteJobService {

    @Autowired
    private FavoriteJobRepository favoriteJobRepository;

    @Autowired
    private IJobPostRepository jobPostRepository;

    @Autowired
    private com.example.swp391_d01_g3.repository.IStudentRepository studentRepository;

    @Override
    public boolean toggleFavorite(Integer studentId, Integer jobPostId) {
        Student student = studentRepository.findById(studentId.longValue()).orElseThrow(() -> new RuntimeException("Student not found"));
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElseThrow(() -> new RuntimeException("JobPost not found"));
        try {
            Optional<FavoriteJob> existingFavorite = favoriteJobRepository.findByStudentAndJobPost(student, jobPost);
            if (existingFavorite.isPresent()) {
                favoriteJobRepository.deleteByStudentAndJobPost(student, jobPost);
                return false;
            } else {
                FavoriteJob favoriteJob = new FavoriteJob(student, jobPost);
                favoriteJobRepository.save(favoriteJob);
                return true;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("duplicate key")) {
                boolean currentlyFavorited = favoriteJobRepository.existsByStudentAndJobPost(student, jobPost);
                return currentlyFavorited;
            }
            throw e;
        }
    }

    @Override
    public boolean isFavorited(Integer studentId, Integer jobPostId) {
        Student student = studentRepository.findById(studentId.longValue()).orElseThrow(() -> new RuntimeException("Student not found"));
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElseThrow(() -> new RuntimeException("JobPost not found"));
        return favoriteJobRepository.existsByStudentAndJobPost(student, jobPost);
    }

    @Override
    public List<FavoriteJob> getFavoriteJobsByStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId.longValue()).orElseThrow(() -> new RuntimeException("Student not found"));
        return favoriteJobRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    @Override
    public List<JobPost> getFavoriteJobPostsByStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId.longValue()).orElseThrow(() -> new RuntimeException("Student not found"));
        List<FavoriteJob> favoriteJobs = favoriteJobRepository.findByStudentOrderByCreatedAtDesc(student);
        List<JobPost> jobPosts = new java.util.ArrayList<>();
        for (FavoriteJob favoriteJob : favoriteJobs) {
            jobPosts.add(favoriteJob.getJobPost());
        }
        return jobPosts;
    }

    @Override
    public long getFavoriteCount(Integer studentId) {
        Student student = studentRepository.findById(studentId.longValue()).orElseThrow(() -> new RuntimeException("Student not found"));
        return favoriteJobRepository.countByStudent(student);
    }




} 