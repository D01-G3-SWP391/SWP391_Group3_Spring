    package com.example.swp391_d01_g3.controller.homePage;

    import com.example.swp391_d01_g3.model.JobPost;
    import com.example.swp391_d01_g3.service.jobpost.IJobpostService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;

    import java.util.List;

    @Controller
    @RequestMapping("/JobDescription")
    public class JobsDescription {
        @Autowired
        private IJobpostService iJobpostService;

        @GetMapping("")
        public String showDescription(@RequestParam("id") Long id, Model model){
            List<JobPost> jobPosts = iJobpostService.findAllWithEmployer(id);
    //        String formatDiscription = jobPosts.get(0).getJobDescription().replace("\n","<br>");
    //        jobPosts.setJobDescription(formatDiscription);
            model.addAttribute("jobPosts", jobPosts);
            return "homePage/descriptionJob";
        }
    }
