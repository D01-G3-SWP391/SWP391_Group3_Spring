# UC-04: Edit Company Profile
## Functionalities

| **UCD ID and Name:** | **UC-04: Edit Company Profile** |
|----------------------|----------------------------------|
| **Created By:** | VuVN | **Date Created:** | 30/Sep/2024 |
| **Primary Actor:** | Employer | **Secondary Actors:** | System |
| **Trigger:** | Employer clicks on the "Edit Profile" button or "Chỉnh sửa hồ sơ" link |
| **Description:** | As an employer I want to edit my company profile successfully so that I can keep my company information up-to-date and attract better candidates. |
| **Preconditions:** | Employer must be logged in to the system and have an active employer account. |
| **Postconditions:** | Company profile is updated successfully with new information. |

## **Normal Flow:**

**1.0 Edit Company Profile system**
1. An employer navigates to their dashboard and clicks on the "Edit Profile" or "Chỉnh sửa hồ sơ" button.
2. The system displays the company profile edit form with current information pre-filled (company name, description, website, phone, address, logo, etc.).
3. The employer modifies the desired information in the form fields (company description, contact information, website URL, company address, industry field).
4. The employer optionally uploads a newxcompany logo image.
5. The employer clicks the "Save Changes" or "Lưu thay đổi" button to submit the updated information.
6. System validates all input data and file uploads.
7. System updates the company profile information in the database.
8. System displays a success message confirming the profile has been updated.
9. System redirects the employer back to their profile view page with updated information.

| **Alternative Flows:** | None |
|------------------------|------|

## **Exceptions:**

**1.0.E1 Employer entered invalid information**
1. The Error Message screen is shown to the user.
2. Employer did not fill in required fields.
3. Employer fills in invalid format information (invalid email, phone, website URL).

**1.0.E2 File upload error**
1. The Error Message screen is shown to the user.
2. Uploaded logo file exceeds size limit (>2MB).
3. Uploaded file is not in supported format (not JPG, PNG, GIF).

**1.0.E3 System error during update**
1. The Error Message screen is shown to the user.
2. Database connection error occurs.
3. System displays "Unable to save changes, please try again" message.

| **Priority:** | Should have |
|---------------|-------------|
| **Frequency of Use:** | Medium - employers update profiles periodically |
| **Business Rules:** | BR-04, BR-05, BR-06, BR-09 |
| **Other Information:** | - Company logo must be in JPG, PNG, or GIF format<br>- Maximum file size for logo is 2MB<br>- Company name cannot be changed once set<br>- Website URL must be valid format |
| **Assumptions:** | - Employer has stable internet connection<br>- Browser supports file upload functionality<br>- Company has necessary information to update profile | 