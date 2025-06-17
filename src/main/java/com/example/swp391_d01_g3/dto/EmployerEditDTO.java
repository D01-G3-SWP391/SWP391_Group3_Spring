package com.example.swp391_d01_g3.dto;

import jakarta.validation.constraints.*;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Employer;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EmployerEditDTO {

    // Getters and Setters
    // Account fields
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 6, max = 100, message = "Họ và tên phải từ 6 đến 100 ký tự")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹĐđ\\s]+$", message = "Họ và tên chỉ được chứa chữ cái và khoảng trắng")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$", message = "Số điện thoại không đúng định dạng Việt Nam")
    private String phone;

    // Employer fields
    @NotBlank(message = "Tên công ty không được để trống")
    @Size(min = 2, max = 255, message = "Tên công ty phải từ 2 đến 255 ký tự")
    private String companyName;

    @NotBlank(message = "Địa chỉ công ty không được để trống")
    @Size(min = 5, max = 500, message = "Địa chỉ công ty phải từ 5 đến 500 ký tự")
    private String companyAddress;

    @NotBlank(message = "Mô tả công ty không được để trống")
    @Size(min = 10, max = 100000000, message = "Mô tả công ty phải từ 10 đến 10000000 ký tự")
    private String companyDescription;

    @Setter
    private String logoUrl;

    @Setter
    @NotNull(message = "Vui lòng chọn lĩnh vực công việc")
    private Integer jobsFieldId;

    // Constructors
    public EmployerEditDTO() {
    }

    // Constructor để tạo từ Account và Employer
    public EmployerEditDTO(Account account, Employer employer) {
        if (account != null) {
            this.fullName = account.getFullName();
            this.email = account.getEmail();
            this.phone = account.getPhone();
        }
        if (employer != null) {
            this.companyName = employer.getCompanyName();
            this.companyAddress = employer.getCompanyAddress();
            this.companyDescription = employer.getCompanyDescription();
            this.logoUrl = employer.getLogoUrl();
            if (employer.getJobField() != null) {
                this.jobsFieldId = employer.getJobField().getJobFieldId();
            }
        }
    }

    public void setFullName(String fullName) {
        if (fullName != null) {
            this.fullName = fullName.trim().replaceAll("\\s+", " ");
        } else {
            this.fullName = fullName;
        }
    }

    public void setEmail(String email) {
        if (email != null) {
            this.email = email.trim().toLowerCase();
        } else {
            this.email = email;
        }
    }

    public void setPhone(String phone) {
        if (phone != null) {
            String normalizedPhone = phone.trim().replaceAll("\\s+", "");
            if (normalizedPhone.startsWith("+84")) {
                normalizedPhone = "0" + normalizedPhone.substring(3);
            } else if (normalizedPhone.startsWith("84")) {
                normalizedPhone = "0" + normalizedPhone.substring(2);
            }
            this.phone = normalizedPhone;
        } else {
            this.phone = phone;
        }
    }

    public void setCompanyName(String companyName) {
        if (companyName != null) {
            this.companyName = companyName.trim().replaceAll("\\s+", " ");
        } else {
            this.companyName = companyName;
        }
    }

    public void setCompanyAddress(String companyAddress) {
        if (companyAddress != null) {
            this.companyAddress = companyAddress.trim().replaceAll("\\s+", " ");
        } else {
            this.companyAddress = companyAddress;
        }
    }

    public void setCompanyDescription(String companyDescription) {
        if (companyDescription != null) {
            this.companyDescription = companyDescription.trim().replaceAll("\\s+", " ");
        } else {
            this.companyDescription = companyDescription;
        }
    }

    @Override
    public String toString() {
        return "EmployerEditDTO{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", companyDescription='" + companyDescription + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", jobsFieldId=" + jobsFieldId +
                '}';
    }
}