package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.JobPost;
import org.springframework.data.domain.Page;

public interface IAdminJobPostService {
    Page<JobPost> getPendingJobPosts(int page, int size);
    Page<JobPost> getJobPostsByStatus(JobPost.ApprovalStatus status, int page, int size);
    Page<JobPost> searchJobPosts(String keyword, JobPost.ApprovalStatus status, int page, int size);

    // View all job posts
    Page<JobPost> getAllJobPosts(int page, int size);
    Page<JobPost> getAllJobPostsWithDetails(int page, int size);

    // Delete functionality
    void deleteJobPost(Integer jobPostId);
    boolean canDeleteJobPost(Integer jobPostId);

    // Approve/Reject functionality
    void approveJobPost(Integer jobPostId);
    void rejectJobPost(Integer jobPostId, String reason);
    JobPost getJobPostById(Integer jobPostId);
    void changeJobPostStatus(Integer jobPostId, JobPost.ApprovalStatus newStatus);

    // Dashboard statistics
    long getPendingCount();
    long getApprovedCount();
    long getRejectedCount();
}
