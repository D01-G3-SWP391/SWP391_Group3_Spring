package com.example.swp391_d01_g3.service.favorite;

import com.example.swp391_d01_g3.model.FavoriteJob;
import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.FavoriteJobRepository;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public boolean toggleFavorite(Integer studentId, Integer jobPostId) {
        try {
            // Kiểm tra xem đã favorite chưa
            Optional<FavoriteJob> existingFavorite = favoriteJobRepository.findByStudentIdAndJobPostId(studentId, jobPostId);
            
            if (existingFavorite.isPresent()) {
                // Nếu đã favorite thì remove
                favoriteJobRepository.deleteByStudentIdAndJobPostId(studentId, jobPostId);
                return false; // Đã remove favorite
            } else {
                // Nếu chưa favorite thì add
                FavoriteJob favoriteJob = new FavoriteJob(studentId, jobPostId);
                favoriteJobRepository.save(favoriteJob);
                return true; // Đã add favorite
            }
        } catch (Exception e) {
            // Xử lý lỗi duplicate key hoặc các lỗi khác
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("duplicate key")) {
                // Nếu là lỗi duplicate, có nghĩa là record đã tồn tại
                // Kiểm tra lại trạng thái hiện tại và return cho phù hợp
                boolean currentlyFavorited = favoriteJobRepository.existsByStudentIdAndJobPostId(studentId, jobPostId);
                return currentlyFavorited;
            }
            // Re-throw exception khác
            throw e;
        }
    }

    @Override
    public boolean isFavorited(Integer studentId, Integer jobPostId) {
        return favoriteJobRepository.existsByStudentIdAndJobPostId(studentId, jobPostId);
    }

    @Override
    public List<FavoriteJob> getFavoriteJobsByStudent(Integer studentId) {
        return favoriteJobRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Override
    public List<FavoriteJob> getRecentFavoriteJobs(Integer studentId) {
        // Lấy top 5 favorite jobs gần đây sử dụng Pageable
        return favoriteJobRepository.findByStudentIdOrderByCreatedAtDesc(studentId, PageRequest.of(0, 5));
    }

    @Override
    public List<JobPost> getFavoriteJobPostsByStudent(Integer studentId) {
        // Lấy danh sách jobPostId từ favorite jobs
        List<Integer> jobPostIds = favoriteJobRepository.findJobPostIdsByStudentId(studentId);
        
        // Nếu không có favorite jobs thì return empty list
        if (jobPostIds.isEmpty()) {
            return List.of();
        }
        
        // Lấy job posts với employer details từ jobPostIds
        return jobPostRepository.findByJobPostIdInWithEmployer(jobPostIds);
    }

    @Override
    public long getFavoriteCount(Integer studentId) {
        return favoriteJobRepository.countByStudentId(studentId);
    }

    @Override
    public boolean removeFavorite(Integer studentId, Integer jobPostId) {
        Optional<FavoriteJob> favoriteJob = favoriteJobRepository.findByStudentIdAndJobPostId(studentId, jobPostId);
        
        if (favoriteJob.isPresent()) {
            favoriteJobRepository.deleteByStudentIdAndJobPostId(studentId, jobPostId);
            return true;
        }
        return false;
    }

    @Override
    public FavoriteJob addFavorite(Integer studentId, Integer jobPostId) {
        try {
            // Kiểm tra xem đã favorite chưa
            if (favoriteJobRepository.existsByStudentIdAndJobPostId(studentId, jobPostId)) {
                // Nếu đã favorite rồi thì return favorite hiện tại
                return favoriteJobRepository.findByStudentIdAndJobPostId(studentId, jobPostId).orElse(null);
            }
            
            // Tạo favorite job mới
            FavoriteJob favoriteJob = new FavoriteJob(studentId, jobPostId);
            return favoriteJobRepository.save(favoriteJob);
        } catch (Exception e) {
            // Xử lý lỗi duplicate key
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("duplicate key")) {
                // Nếu là lỗi duplicate, return record hiện tại
                return favoriteJobRepository.findByStudentIdAndJobPostId(studentId, jobPostId).orElse(null);
            }
            // Re-throw exception khác
            throw e;
        }
    }
} 