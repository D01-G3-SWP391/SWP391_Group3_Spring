package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.model.JobPost;
import com.example.swp391_d01_g3.service.admin.IAdminJobPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Admin")
public class AdminJobPostController {

    @Autowired
    private IAdminJobPostService adminJobPostService;

    private static final Logger logger = LoggerFactory.getLogger(AdminJobPostController.class);

    @GetMapping("/JobPosts")
    public String showJobPosts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "6") int size,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String status,
                               Model model) {
        try {
            Page<JobPost> jobPostPage;
            JobPost.ApprovalStatus approvalStatus = null;

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 6;

            if (status != null && !status.isEmpty()) {
                try {
                    approvalStatus = JobPost.ApprovalStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    approvalStatus = JobPost.ApprovalStatus.PENDING;
                }
            }

            jobPostPage = adminJobPostService.searchJobPosts(keyword, approvalStatus, page, size);

            // THÊM: Lấy số lượng cho badges
            long totalJobPosts = adminJobPostService.getTotalJobPostsCount();
            long pendingJobPosts = adminJobPostService.getPendingCount();
            long approvedJobPosts = adminJobPostService.getApprovedCount();
            long rejectedJobPosts = adminJobPostService.getRejectedCount();

            model.addAttribute("jobPostList", jobPostPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", jobPostPage.getTotalPages());
            model.addAttribute("totalItems", jobPostPage.getTotalElements());
            model.addAttribute("hasNext", jobPostPage.hasNext());
            model.addAttribute("hasPrevious", jobPostPage.hasPrevious());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword);
            model.addAttribute("approvalStatuses", JobPost.ApprovalStatus.values());

            // THÊM: Số lượng cho badges
            model.addAttribute("totalJobPosts", totalJobPosts);
            model.addAttribute("pendingJobPosts", pendingJobPosts);
            model.addAttribute("approvedJobPosts", approvedJobPosts);
            model.addAttribute("rejectedJobPosts", rejectedJobPosts);

            return "admin/viewJobPosts";

        } catch (Exception e) {
            model.addAttribute("error", "Error loading job posts: " + e.getMessage());

            model.addAttribute("jobPostList", java.util.Collections.emptyList());
            model.addAttribute("totalItems", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 0);
            model.addAttribute("hasNext", false);
            model.addAttribute("hasPrevious", false);
            model.addAttribute("selectedStatus", "");
            model.addAttribute("keyword", "");
            model.addAttribute("approvalStatuses", JobPost.ApprovalStatus.values());

            return "admin/viewJobPosts";
        }
    }

    @GetMapping("/AllJobPosts")
    public String viewAllJobPosts(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String status,
                                  Model model) {
        try {
            Page<JobPost> jobPostPage;
            JobPost.ApprovalStatus approvalStatus = null;

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 10;

            // Parse status parameter
            if (status != null && !status.isEmpty()) {
                try {
                    approvalStatus = JobPost.ApprovalStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status parameter: {}", status);
                }
            }

            // Search logic
            if (keyword != null && !keyword.trim().isEmpty() && approvalStatus != null) {
                jobPostPage = adminJobPostService.searchJobPostsByKeywordAndStatus(keyword.trim(), approvalStatus, page, size);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                jobPostPage = adminJobPostService.searchAllJobPosts(keyword.trim(), page, size);
            } else if (approvalStatus != null) {
                jobPostPage = adminJobPostService.getJobPostsByStatus(approvalStatus, page, size);
            } else {
                jobPostPage = adminJobPostService.getAllJobPostsWithDetails(page, size);
            }

            // THÊM: Lấy số lượng cho badges
            long totalJobPosts = adminJobPostService.getTotalJobPostsCount();
            long pendingJobPosts = adminJobPostService.getPendingCount();
            long approvedJobPosts = adminJobPostService.getApprovedCount();
            long rejectedJobPosts = adminJobPostService.getRejectedCount();

            model.addAttribute("jobPostList", jobPostPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", jobPostPage.getTotalPages());
            model.addAttribute("totalItems", jobPostPage.getTotalElements());
            model.addAttribute("hasNext", jobPostPage.hasNext());
            model.addAttribute("hasPrevious", jobPostPage.hasPrevious());
            model.addAttribute("keyword", keyword);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("approvalStatuses", JobPost.ApprovalStatus.values());

            // THÊM: Số lượng cho badges
            model.addAttribute("totalJobPosts", totalJobPosts);
            model.addAttribute("pendingJobPosts", pendingJobPosts);
            model.addAttribute("approvedJobPosts", approvedJobPosts);
            model.addAttribute("rejectedJobPosts", rejectedJobPosts);

            return "admin/viewAllJobPosts";

        } catch (Exception e) {
            model.addAttribute("error", "Error loading job posts: " + e.getMessage());

            model.addAttribute("jobPostList", java.util.Collections.emptyList());
            model.addAttribute("totalItems", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 0);
            model.addAttribute("hasNext", false);
            model.addAttribute("hasPrevious", false);

            return "admin/viewAllJobPosts";
        }
    }

    @GetMapping("/JobPost/{id}")
    public String viewJobPostDetails(@PathVariable("id") Integer jobPostId, Model model) {
        try {
            if (jobPostId == null || jobPostId <= 0) {
                return "redirect:/Admin/JobPosts";
            }

            JobPost jobPost = adminJobPostService.getJobPostById(jobPostId);
            model.addAttribute("jobPost", jobPost);
            return "admin/viewJobPostDetails";

        } catch (Exception e) {
            return "redirect:/Admin/JobPosts";
        }
    }

    @PostMapping("/ApproveJobPost/{id}")
    public String approveJobPost(@PathVariable("id") Integer jobPostId,
                                 RedirectAttributes redirectAttributes) {
        try {
            adminJobPostService.approveJobPost(jobPostId);
            redirectAttributes.addFlashAttribute("success", "Job post approved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error approving job post: " + e.getMessage());
        }
        return "redirect:/Admin/JobPosts";
    }

    @PostMapping("/RejectJobPost/{id}")
    public String rejectJobPost(@PathVariable("id") Integer jobPostId,
                                @RequestParam(required = false) String reason,
                                RedirectAttributes redirectAttributes) {
        try {
            adminJobPostService.rejectJobPost(jobPostId, reason);
            redirectAttributes.addFlashAttribute("success", "Job post rejected successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error rejecting job post: " + e.getMessage());
        }
        return "redirect:/Admin/JobPosts";
    }

    @PostMapping("/DeleteJobPost/{id}")
    public String deleteJobPost(@PathVariable("id") Integer jobPostId,
                                @RequestParam(required = false) String reason,
                                RedirectAttributes redirectAttributes) {
        try {
            if (!adminJobPostService.canDeleteJobPost(jobPostId)) {
                redirectAttributes.addFlashAttribute("error", "This job post cannot be deleted at this time.");
                return "redirect:/Admin/AllJobPosts";
            }

            adminJobPostService.deleteJobPost(jobPostId);
            redirectAttributes.addFlashAttribute("success", "Job post deleted successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting job post: " + e.getMessage());
        }
        return "redirect:/Admin/AllJobPosts";
    }

    @PostMapping("/ChangeJobPostStatus/{id}")
    public String changeJobPostStatus(@PathVariable("id") Integer jobPostId,
                                      @RequestParam("status") String status,
                                      RedirectAttributes redirectAttributes) {
        try {
            JobPost.ApprovalStatus newStatus = JobPost.ApprovalStatus.valueOf(status.toUpperCase());
            adminJobPostService.changeJobPostStatus(jobPostId, newStatus);

            String message = String.format("Job post status changed to %s successfully", newStatus);
            redirectAttributes.addFlashAttribute("success", message);

        } catch (Exception e) {
            logger.error("Error changing job post status: ", e);
            redirectAttributes.addFlashAttribute("error", "Error changing job post status: " + e.getMessage());
        }
        return "redirect:/Admin/AllJobPosts";
    }
}
