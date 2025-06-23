package com.example.swp391_d01_g3.validation;

import com.example.swp391_d01_g3.dto.JobApplicationDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Disabled;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobApplicationDTOValidationTest {

    private Validator validator;

    @BeforeAll
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidJobApplicationDTO() {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setFullname("Nguyễn Văn An");
        dto.setEmail("nguyenvanan@gmail.com");
        dto.setPhoneNumber("0905123456");
        dto.setDescription("Tôi muốn ứng tuyển vị trí này vì tôi có kinh nghiệm và đam mê với công việc.");
        dto.setStudentId(1L);
        dto.setJobPostId(1);

        Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Should not have validation errors for valid DTO");
    }

    @Test
    void testInvalidFullName() {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setFullname(""); // Empty name
        dto.setEmail("test@email.com");
        dto.setPhoneNumber("0905123456");
        dto.setDescription("Valid description");
        dto.setStudentId(1L);
        dto.setJobPostId(1);

        Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors for empty fullname");
        
        boolean hasFullNameError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fullname"));
        assertTrue(hasFullNameError, "Should have fullname validation error");
    }

    @Test
    void testInvalidEmail() {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setFullname("Nguyễn Văn An");
        dto.setEmail("invalid-email"); // Invalid email
        dto.setPhoneNumber("0905123456");
        dto.setDescription("Valid description");
        dto.setStudentId(1L);
        dto.setJobPostId(1);

        Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors for invalid email");
        
        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertTrue(hasEmailError, "Should have email validation error");
    }

    @Test
    void testInvalidPhoneNumber() {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setFullname("Nguyễn Văn An");
        dto.setEmail("test@email.com");
        dto.setPhoneNumber("123"); // Invalid phone
        dto.setDescription("Valid description");
        dto.setStudentId(1L);
        dto.setJobPostId(1);

        Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors for invalid phone");
        
        boolean hasPhoneError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber"));
        assertTrue(hasPhoneError, "Should have phone validation error");
    }

    @Test
    void testInvalidDescription() {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setFullname("Nguyễn Văn An");
        dto.setEmail("test@email.com");
        dto.setPhoneNumber("0905123456");
        dto.setDescription("Short"); // Too short
        dto.setStudentId(1L);
        dto.setJobPostId(1);

        Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors for short description");
        
        boolean hasDescriptionError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
        assertTrue(hasDescriptionError, "Should have description validation error");
    }

    @Test
    void testInvalidStudentId() {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setFullname("Nguyễn Văn An");
        dto.setEmail("test@email.com");
        dto.setPhoneNumber("0905123456");
        dto.setDescription("Valid description");
        dto.setStudentId(0L); // Invalid ID
        dto.setJobPostId(1);

        Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors for invalid studentId");
        
        boolean hasStudentIdError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("studentId"));
        assertTrue(hasStudentIdError, "Should have studentId validation error");
    }

    @Test
    void testInvalidJobPostId() {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setFullname("Nguyễn Văn An");
        dto.setEmail("test@email.com");
        dto.setPhoneNumber("0905123456");
        dto.setDescription("Valid description");
        dto.setStudentId(1L);
        dto.setJobPostId(0); // Invalid ID

        Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors for invalid jobPostId");
        
        boolean hasJobPostIdError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("jobPostId"));
        assertTrue(hasJobPostIdError, "Should have jobPostId validation error");
    }

    @Test
    void testFullNameWithNumbers() {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setFullname("Nguyễn Văn An123"); // Contains numbers
        dto.setEmail("test@email.com");
        dto.setPhoneNumber("0905123456");
        dto.setDescription("Valid description");
        dto.setStudentId(1L);
        dto.setJobPostId(1);

        Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors for fullname with numbers");
        
        boolean hasFullNameError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fullname"));
        assertTrue(hasFullNameError, "Should have fullname validation error");
    }

    @Test
    void testValidPhoneFormats() {
        String[] validPhones = {
            "0905123456",
            "0915123456", 
            "0925123456",
            "0935123456",
            "0945123456",
            "0955123456",
            "0965123456",
            "0975123456",
            "0985123456",
            "0995123456",
            "+84905123456",
            "+84915123456"
        };

        for (String phone : validPhones) {
            JobApplicationDTO dto = new JobApplicationDTO();
            dto.setFullname("Nguyễn Văn An");
            dto.setEmail("test@email.com");
            dto.setPhoneNumber(phone);
            dto.setDescription("Valid description");
            dto.setStudentId(1L);
            dto.setJobPostId(1);

            Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Phone " + phone + " should be valid");
        }
    }

    @Test
    void testInvalidPhoneFormats() {
        String[] invalidPhones = {
            "1234567890", // Invalid prefix
            "0123456789", // Invalid prefix
            "090512345",  // Too short
            "09051234567", // Too long
            "abc1234567",  // Contains letters
            "+84123456789" // Invalid country code
        };

        for (String phone : invalidPhones) {
            JobApplicationDTO dto = new JobApplicationDTO();
            dto.setFullname("Nguyễn Văn An");
            dto.setEmail("test@email.com");
            dto.setPhoneNumber(phone);
            dto.setDescription("Valid description");
            dto.setStudentId(1L);
            dto.setJobPostId(1);

            Set<ConstraintViolation<JobApplicationDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty(), "Phone " + phone + " should be invalid");
        }
    }
} 