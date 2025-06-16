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
            logger.info("Loading job posts - page: {}, size: {}, keyword: {}, status: {}",
                    page, size, keyword, status);

            Page<JobPost> jobPostPage;
            JobPost.ApprovalStatus approvalStatus = null;

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 6;

            if (status != null && !status.isEmpty()) {
                try {
                    approvalStatus = JobPost.ApprovalStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status parameter: {}", status);
                    approvalStatus = JobPost.ApprovalStatus.PENDING;
                }
            }

            jobPostPage = adminJobPostService.searchJobPosts(keyword, approvalStatus, page, size);

            model.addAttribute("jobPostList", jobPostPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", jobPostPage.getTotalPages());
            model.addAttribute("totalItems", jobPostPage.getTotalElements());
            model.addAttribute("hasNext", jobPostPage.hasNext());
            model.addAttribute("hasPrevious", jobPostPage.hasPrevious());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword);
            model.addAttribute("approvalStatuses", JobPost.ApprovalStatus.values());

            logger.info("Successfully loaded {} job posts", jobPostPage.getContent().size());
            return "admin/viewJobPosts";

        } catch (Exception e) {
            logger.error("Error loading job posts: ", e);
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
                                  Model model) {
        try {
            logger.info("Loading all job posts - page: {}, size: {}, keyword: {}", page, size, keyword);

            Page<JobPost> jobPostPage;

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 10;

            if (keyword != null && !keyword.trim().isEmpty()) {
                jobPostPage = adminJobPostService.searchJobPosts(keyword.trim(), null, page, size);
                model.addAttribute("keyword", keyword);
            } else {
                jobPostPage = adminJobPostService.getAllJobPostsWithDetails(page, size);
            }

            model.addAttribute("jobPostList", jobPostPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", jobPostPage.getTotalPages());
            model.addAttribute("totalItems", jobPostPage.getTotalElements());
            model.addAttribute("hasNext", jobPostPage.hasNext());
            model.addAttribute("hasPrevious", jobPostPage.hasPrevious());
            model.addAttribute("keyword", keyword);
            model.addAttribute("approvalStatuses", JobPost.ApprovalStatus.values());

            logger.info("Successfully loaded {} job posts", jobPostPage.getContent().size());
            return "admin/viewAllJobPosts";

        } catch (Exception e) {
            logger.error("Error loading all job posts: ", e);
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
            logger.info("Viewing job post details for ID: {}", jobPostId);

            if (jobPostId == null || jobPostId <= 0) {
                logger.warn("Invalid job post ID: {}", jobPostId);
                return "redirect:/Admin/JobPosts";
            }

            JobPost jobPost = adminJobPostService.getJobPostById(jobPostId);
            model.addAttribute("jobPost", jobPost);
            return "admin/viewJobPostDetails";

        } catch (Exception e) {
            logger.error("Error loading job post details for ID: {}", jobPostId, e);
            return "redirect:/Admin/JobPosts";
        }
    }

    @PostMapping("/ApproveJobPost/{id}")
    public String approveJobPost(@PathVariable("id") Integer jobPostId,
                                 RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to approve job post with ID: {}", jobPostId);
            adminJobPostService.approveJobPost(jobPostId);
            redirectAttributes.addFlashAttribute("success", "Job post approved successfully");
        } catch (Exception e) {
            logger.error("Error approving job post: ", e);
            redirectAttributes.addFlashAttribute("error", "Error approving job post: " + e.getMessage());
        }
        return "redirect:/Admin/JobPosts";
    }

    @PostMapping("/RejectJobPost/{id}")
    public String rejectJobPost(@PathVariable("id") Integer jobPostId,
                                @RequestParam(required = false) String reason,
                                RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to reject job post with ID: {}", jobPostId);
            adminJobPostService.rejectJobPost(jobPostId, reason);
            redirectAttributes.addFlashAttribute("success", "Job post rejected successfully");
        } catch (Exception e) {
            logger.error("Error rejecting job post: ", e);
            redirectAttributes.addFlashAttribute("error", "Error rejecting job post: " + e.getMessage());
        }
        return "redirect:/Admin/JobPosts";
    }

    @PostMapping("/DeleteJobPost/{id}")
    public String deleteJobPost(@PathVariable("id") Integer jobPostId,
                                @RequestParam(required = false) String reason,
                                RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to delete job post with ID: {}, reason: {}", jobPostId, reason);

            if (!adminJobPostService.canDeleteJobPost(jobPostId)) {
                redirectAttributes.addFlashAttribute("error", "This job post cannot be deleted at this time.");
                return "redirect:/Admin/AllJobPosts";
            }

            adminJobPostService.deleteJobPost(jobPostId);
            redirectAttributes.addFlashAttribute("success", "Job post deleted successfully");

        } catch (Exception e) {
            logger.error("Error deleting job post: ", e);
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
