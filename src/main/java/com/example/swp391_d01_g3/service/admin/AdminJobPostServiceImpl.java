package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.repository.IJobPostRepository;
import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminJobPostServiceImpl implements IAdminJobPostService {

    @Autowired
    private IJobPostRepository jobPostRepository;

    @Autowired
    private IJobpostService jobpostService;

    private static final Logger logger = LoggerFactory.getLogger(AdminJobPostServiceImpl.class);

    @Override
    public Page<JobPost> getPendingJobPosts(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return jobPostRepository.findByApprovalStatusOrderByCreatedAtDesc(
                    JobPost.ApprovalStatus.PENDING, pageable);
        } catch (Exception e) {
            logger.error("Error getting pending job posts: ", e);
            throw new RuntimeException("Error getting pending job posts: " + e.getMessage());
        }
    }

    @Override
    public Page<JobPost> getJobPostsByStatus(JobPost.ApprovalStatus status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return jobPostRepository.findByApprovalStatusOrderByCreatedAtDesc(status, pageable);
        } catch (Exception e) {
            logger.error("Error getting job posts by status: ", e);
            throw new RuntimeException("Error getting job posts by status: " + e.getMessage());
        }
    }

    @Override
    public Page<JobPost> searchJobPosts(String keyword, JobPost.ApprovalStatus status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (status != null) {
                    return jobPostRepository.searchJobPostsByKeywordAndStatus(keyword.trim(), status, pageable);
                } else {
                    return jobPostRepository.searchJobPostsByKeyword(keyword.trim(), pageable);
                }
            } else {
                if (status != null) {
                    return jobPostRepository.findByApprovalStatusOrderByCreatedAtDesc(status, pageable);
                } else {
                    return jobPostRepository.findByApprovalStatusOrderByCreatedAtDesc(
                            JobPost.ApprovalStatus.PENDING, pageable);
                }
            }
        } catch (Exception e) {
            logger.error("Error searching job posts: ", e);
            throw new RuntimeException("Error searching job posts: " + e.getMessage());
        }
    }

    @Override
    public Page<JobPost> getAllJobPosts(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return jobPostRepository.findAllByOrderByCreatedAtDesc(pageable);
        } catch (Exception e) {
            logger.error("Error getting all job posts: ", e);
            throw new RuntimeException("Error getting all job posts: " + e.getMessage());
        }
    }

    @Override
    public Page<JobPost> getAllJobPostsWithDetails(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return jobPostRepository.findAllByOrderByCreatedAtDesc(pageable);
        } catch (Exception e) {
            logger.error("Error getting job posts with details: ", e);
            throw new RuntimeException("Error getting job posts with details: " + e.getMessage());
        }
    }

    @Override
    public void deleteJobPost(Integer jobPostId) {
        try {
            JobPost jobPost = jobpostService.findById(jobPostId)
                    .orElseThrow(() -> new RuntimeException("Job post not found with ID: " + jobPostId));

            logger.info("Deleting job post - ID: {}, Title: {}, Company: {}",
                    jobPostId, jobPost.getJobTitle(), jobPost.getEmployer().getCompanyName());

            jobpostService.deleteById(jobPostId);

            logger.info("Successfully deleted job post with ID: {}", jobPostId);
        } catch (Exception e) {
            logger.error("Error deleting job post with ID: {}", jobPostId, e);
            throw new RuntimeException("Error deleting job post: " + e.getMessage());
        }
    }

    @Override
    public boolean canDeleteJobPost(Integer jobPostId) {
        try {
            JobPost jobPost = jobpostService.findById(jobPostId)
                    .orElseThrow(() -> new RuntimeException("Job post not found with ID: " + jobPostId));

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime createdAt = jobPost.getCreatedAt();

            // Can delete if:
            // 1. Status is REJECTED
            // 2. Older than 30 days
            // 3. PENDING for more than 7 days

            if (jobPost.getApprovalStatus() == JobPost.ApprovalStatus.REJECTED) {
                return true;
            }

            if (createdAt.isBefore(now.minusDays(30))) {
                return true;
            }

            if (jobPost.getApprovalStatus() == JobPost.ApprovalStatus.PENDING &&
                    createdAt.isBefore(now.minusDays(7))) {
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.error("Error checking if job post can be deleted: {}", jobPostId, e);
            return false;
        }
    }

    @Override
    public void approveJobPost(Integer jobPostId) {
        try {
            JobPost jobPost = jobpostService.findById(jobPostId)
                    .orElseThrow(() -> new RuntimeException("Job post not found with ID: " + jobPostId));

            jobPost.setApprovalStatus(JobPost.ApprovalStatus.APPROVED);
            jobPost.setDisplayStatus(JobPost.DisplayStatus.ACTIVE);
            jobpostService.save(jobPost);

            logger.info("Successfully approved job post with ID: {}", jobPostId);
        } catch (Exception e) {
            logger.error("Error approving job post with ID: {}", jobPostId, e);
            throw new RuntimeException("Error approving job post: " + e.getMessage());
        }
    }

    @Override
    public void rejectJobPost(Integer jobPostId, String reason) {
        try {
            JobPost jobPost = jobpostService.findById(jobPostId)
                    .orElseThrow(() -> new RuntimeException("Job post not found with ID: " + jobPostId));

            jobPost.setApprovalStatus(JobPost.ApprovalStatus.REJECTED);
            jobPost.setDisplayStatus(JobPost.DisplayStatus.INACTIVE);
            jobpostService.save(jobPost);

            logger.info("Successfully rejected job post with ID: {}, reason: {}", jobPostId, reason);
        } catch (Exception e) {
            logger.error("Error rejecting job post with ID: {}", jobPostId, e);
            throw new RuntimeException("Error rejecting job post: " + e.getMessage());
        }
    }

    @Override
    public JobPost getJobPostById(Integer jobPostId) {
        try {
            return jobpostService.findById(jobPostId)
                    .orElseThrow(() -> new RuntimeException("Job post not found with ID: " + jobPostId));
        } catch (Exception e) {
            logger.error("Error getting job post by ID: {}", jobPostId, e);
            throw new RuntimeException("Job post not found with ID: " + jobPostId);
        }
    }

    @Override
    public long getPendingCount() {
        return jobPostRepository.countByApprovalStatus(JobPost.ApprovalStatus.PENDING);
    }

    @Override
    public long getApprovedCount() {
        return jobPostRepository.countByApprovalStatus(JobPost.ApprovalStatus.APPROVED);
    }

    @Override
    public long getRejectedCount() {
        return jobPostRepository.countByApprovalStatus(JobPost.ApprovalStatus.REJECTED);
    }
}
