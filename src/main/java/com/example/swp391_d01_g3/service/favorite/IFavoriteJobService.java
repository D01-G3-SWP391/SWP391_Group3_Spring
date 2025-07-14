package com.example.swp391_d01_g3.service.favorite;

import com.example.swp391_d01_g3.model.FavoriteJob;
import com.example.swp391_d01_g3.model.JobPost;

import java.util.List;

public interface IFavoriteJobService {

    /**
     * Toggle favorite status của một job post cho student
     * @param studentId ID của student
     * @param jobPostId ID của job post
     * @return true nếu đã add favorite, false nếu đã remove favorite
     */
    boolean toggleFavorite(Integer studentId, Integer jobPostId);

    /**
     * Kiểm tra xem student đã favorite job post này chưa
     * @param studentId ID của student
     * @param jobPostId ID của job post
     * @return true nếu đã favorite, false nếu chưa
     */
    boolean isFavorited(Integer studentId, Integer jobPostId);

    /**
     * Lấy danh sách favorite jobs của student
     * @param studentId ID của student
     * @return List<FavoriteJob> danh sách favorite jobs
     */
    List<FavoriteJob> getFavoriteJobsByStudent(Integer studentId);

    /**
     * Lấy favorite jobs gần đây của student (top 5)
     * @param studentId ID của student
     * @return List<FavoriteJob> danh sách favorite jobs gần đây
     */
    List<FavoriteJob> getRecentFavoriteJobs(Integer studentId);

    /**
     * Lấy danh sách JobPost từ favorite jobs của student
     * @param studentId ID của student
     * @return List<JobPost> danh sách job posts đã favorite
     */
    List<JobPost> getFavoriteJobPostsByStudent(Integer studentId);

    /**
     * Lấy số lượng favorite jobs của student
     * @param studentId ID của student
     * @return số lượng favorite jobs
     */
    long getFavoriteCount(Integer studentId);

    /**
     * Xóa favorite job
     * @param studentId ID của student
     * @param jobPostId ID của job post
     * @return true nếu xóa thành công, false nếu không tìm thấy
     */
    boolean removeFavorite(Integer studentId, Integer jobPostId);

    /**
     * Thêm job vào favorite
     * @param studentId ID của student
     * @param jobPostId ID của job post
     * @return FavoriteJob đã tạo
     */
    FavoriteJob addFavorite(Integer studentId, Integer jobPostId);
} 