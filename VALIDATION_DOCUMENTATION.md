# 🔐 Validation System Documentation - Register Student Page

## 📋 Tổng quan

Hệ thống validation cho trang đăng ký sinh viên đã được implement với cả **client-side** và **server-side validation** để đảm bảo tính bảo mật và trải nghiệm người dùng tốt nhất.

---

## 🎯 Các loại Validation được áp dụng

### 1. **Client-side Validation (Frontend)**
- **Real-time validation**: Kiểm tra ngay khi user nhập liệu
- **Visual feedback**: Hiển thị icon ✅/❌ và thông báo lỗi trực quan
- **JavaScript validation**: Logic validation phức tạp
- **HTML5 validation**: Attributes như `required`, `type`, `pattern`

### 2. **Server-side Validation (Backend)**
- **Bean Validation (JSR-303)**: Annotations như `@NotBlank`, `@Email`, `@Size`, `@Pattern`
- **Custom Validator**: Logic validation phức tạp trong `AccountDTO`
- **Business Logic Validation**: Kiểm tra email trùng lặp với database

---

## 📝 Chi tiết Validation Rules

### **Họ và tên (`fullName`)**
```java
@NotBlank(message = "Họ và tên không được để trống")
@Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự")
@Pattern(regexp = "^[a-zA-ZÀ-ỹĐđ\\s]+$", message = "Họ và tên chỉ được chứa chữ cái và khoảng trắng")
```

**Validation Rules:**
- ✅ Không được để trống
- ✅ Độ dài 2-100 ký tự
- ✅ Chỉ chứa chữ cái tiếng Việt có dấu và khoảng trắng
- ✅ Không chứa số hoặc ký tự đặc biệt
- ✅ Không có nhiều khoảng trắng liên tiếp
- ✅ Tự động trim và chuẩn hóa khoảng trắng

### **Email**
```java
@NotBlank(message = "Email không được để trống")
@Email(message = "Email không đúng định dạng")
@Size(max = 100, message = "Email không được vượt quá 100 ký tự")
```

**Validation Rules:**
- ✅ Không được để trống
- ✅ Format email hợp lệ theo RFC standards
- ✅ Độ dài tối đa 100 ký tự
- ✅ Kiểm tra email đã tồn tại trong hệ thống
- ✅ Tự động chuyển thành chữ thường
- ✅ Domain validation nâng cao

### **Số điện thoại (`phone`)**
```java
@NotBlank(message = "Số điện thoại không được để trống")
@Pattern(regexp = "^(0|\\+84)[1-9][0-9]{8,9}$", message = "Số điện thoại không đúng định dạng")
```

**Validation Rules:**
- ✅ Không được để trống
- ✅ Format số điện thoại Việt Nam (10-11 số)
- ✅ Bắt đầu bằng `0` hoặc `+84`
- ✅ Kiểm tra đầu số hợp lệ của Việt Nam (03x, 05x, 07x, 08x, 09x)
- ✅ Tự động loại bỏ khoảng trắng và ký tự không phải số
- ✅ Tự động chuyển đổi từ `+84` thành `0`

### **Mật khẩu (`password`)**
```java
@NotBlank(message = "Mật khẩu không được để trống")
@Size(min = 8, max = 50, message = "Mật khẩu phải từ 8 đến 50 ký tự")
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Mật khẩu phải chứa ít nhất 1 chữ thường, 1 chữ hoa và 1 số")
```

**Validation Rules:**
- ✅ Không được để trống
- ✅ Độ dài 8-50 ký tự
- ✅ Chứa ít nhất 1 chữ thường
- ✅ Chứa ít nhất 1 chữ hoa
- ✅ Chứa ít nhất 1 số
- ✅ Không chứa khoảng trắng
- ✅ **Bonus**: Kiểm tra ký tự đặc biệt để đánh giá độ mạnh
- ✅ Real-time password strength indicator

### **Xác nhận mật khẩu (`confirmPassword`)**
**Validation Rules:**
- ✅ Không được để trống
- ✅ Phải khớp với mật khẩu chính
- ✅ Real-time validation khi thay đổi mật khẩu

### **Điều khoản sử dụng (`agreeTerms`)**
**Validation Rules:**
- ✅ Bắt buộc phải check box để đồng ý
- ✅ Validation trước khi submit form

---

## 🎨 UX Enhancements

### **Visual Feedback**
- **Valid state**: Border xanh + icon ✅ + background color nhẹ
- **Invalid state**: Border đỏ + icon ❌ + background color nhẹ
- **Focus state**: Box shadow màu xanh/đỏ tùy trạng thái
- **Smooth transitions**: All animations với `cubic-bezier` curves

### **Password Strength Indicator**
```javascript
function checkPasswordStrength(password) {
    // Kiểm tra 5 tiêu chí:
    // 1. Độ dài >= 8
    // 2. Chữ thường
    // 3. Chữ hoa  
    // 4. Số
    // 5. Ký tự đặc biệt
    
    // Hiển thị:
    // - Yếu (< 3 tiêu chí): màu đỏ
    // - Trung bình (3-4 tiêu chí): màu vàng
    // - Mạnh (5 tiêu chí): màu xanh
    // + Gợi ý những gì cần thêm
}
```

### **Error Handling**
- **Smooth error appearance**: Fade in/out với transform animations
- **Error persistence**: Lỗi server-side được giữ nguyên sau khi reload
- **Auto-scroll**: Tự động scroll đến field lỗi đầu tiên
- **Auto-focus**: Focus vào field lỗi để user sửa ngay

### **Loading States**
- **Submit button**: Disable + spinner animation khi đang xử lý
- **Form overlay**: Prevent multiple submissions
- **Progress indication**: Thông báo "Đang xử lý..."

---

## 🔧 Technical Implementation

### **File Structure**
```
src/main/java/com/example/swp391_d01_g3/
├── dto/
│   └── AccountDTO.java           # Bean Validation + Custom Validator
├── controller/register/
│   └── Register.java             # Controller với improved error handling
└── resources/
    ├── static/css/
    │   └── validation-enhanced.css   # Enhanced styling cho validation
    └── templates/register/
        └── registerStudentPage.html  # Template với real-time validation
```

### **Key Features**

#### **1. Data Normalization**
```java
// Controller tự động chuẩn hóa dữ liệu trước khi validate
if (accountDTO.getFullName() != null) {
    accountDTO.setFullName(accountDTO.getFullName().trim().replaceAll("\\s+", " "));
}
if (accountDTO.getEmail() != null) {
    accountDTO.setEmail(accountDTO.getEmail().trim().toLowerCase());
}
if (accountDTO.getPhone() != null) {
    accountDTO.setPhone(accountDTO.getPhone().trim().replaceAll("\\s", ""));
}
```

#### **2. Advanced Phone Number Validation**
```java
// Custom validation cho số điện thoại Việt Nam
if (phone.startsWith("+84")) {
    phone = "0" + phone.substring(3);
}

// Kiểm tra đầu số hợp lệ của Việt Nam
if (!phone.matches("^0(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$")) {
    errors.rejectValue("phone", "error.phone", "Số điện thoại không đúng định dạng Việt Nam");
}
```

#### **3. Password Complexity Validation**
```java
// Kiểm tra độ phức tạp nâng cao
int complexity = 0;
if (password.matches(".*[a-z].*")) complexity++;
if (password.matches(".*[A-Z].*")) complexity++;
if (password.matches(".*\\d.*")) complexity++;
if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) complexity++;

if (complexity < 3) {
    errors.rejectValue("password", "error.password", 
        "Mật khẩu cần có ít nhất 3 trong 4 yếu tố: chữ thường, chữ hoa, số, ký tự đặc biệt");
}
```

### **4. Accessibility Support**
- **ARIA labels**: `aria-describedby` cho error messages
- **Semantic HTML**: Proper form structure với labels
- **Keyboard navigation**: Tab order và focus management
- **High contrast support**: Media queries cho high contrast mode
- **Reduced motion support**: Disable animations if needed

### **5. Responsive Design**
- **Mobile-first approach**: Touch-friendly form elements
- **Prevent zoom on iOS**: `font-size: 16px` trên mobile
- **Optimized touch targets**: Larger clickable areas
- **Viewport adaptation**: Form adapts to different screen sizes

---

## 🧪 Testing Scenarios

### **Test Cases cho Họ và tên:**
- ✅ "Nguyễn Văn A" → Valid
- ❌ "Nguyễn123" → Invalid (chứa số)
- ❌ "A" → Invalid (quá ngắn)
- ❌ "Nguyễn  Văn  A" → Invalid (nhiều khoảng trắng)

### **Test Cases cho Email:**
- ✅ "user@gmail.com" → Valid
- ❌ "user@.com" → Invalid (domain không hợp lệ)
- ❌ "user@@gmail.com" → Invalid (double @)
- ❌ "user@gmail" → Invalid (thiếu TLD)

### **Test Cases cho Phone:**
- ✅ "0901234567" → Valid
- ✅ "+84901234567" → Valid (auto convert to 0901234567)
- ❌ "0801234567" → Invalid (đầu số không hợp lệ)
- ❌ "123456789" → Invalid (không bắt đầu bằng 0)

### **Test Cases cho Password:**
- ✅ "MyPass123" → Valid (có chữ hoa, thường, số)
- ❌ "mypass123" → Invalid (thiếu chữ hoa)
- ❌ "MyPass" → Invalid (thiếu số)
- ❌ "My Pass123" → Invalid (chứa khoảng trắng)

---

## 🚀 Performance & Security

### **Performance Optimizations:**
- **Debounced validation**: Tránh validate quá nhiều lần khi user đang nhập
- **Efficient regex patterns**: Optimized cho performance
- **Minimal DOM manipulations**: Chỉ update khi cần thiết
- **CSS animations**: Hardware-accelerated transforms

### **Security Features:**
- **Input sanitization**: Server-side trimming và normalization
- **SQL injection prevention**: Bean Validation + ORM
- **XSS prevention**: Thymeleaf auto-escaping
- **CSRF protection**: Spring Security tokens
- **Password encoding**: BCrypt hashing cho passwords

---

## 📈 Future Enhancements

### **Planned Improvements:**
1. **Email verification**: Real-time check email existence via API
2. **Phone OTP verification**: SMS verification for phone numbers  
3. **Password policies**: Configurable password requirements
4. **Rate limiting**: Prevent spam registration attempts
5. **Analytics**: Track validation error patterns
6. **A/B testing**: Test different validation UX approaches
7. **Internationalization**: Support multiple languages
8. **Progressive validation**: Smart validation based on user behavior

---

## 🔍 Debug & Troubleshooting

### **Common Issues:**
1. **CSS not loading**: Check file path `/css/validation-enhanced.css`
2. **JavaScript errors**: Check browser console for validation script errors
3. **Server validation not working**: Verify `@Valid` annotation in controller
4. **Thymeleaf errors**: Check template syntax và field binding

### **Debug Tools:**
- **Browser DevTools**: Network tab để check file loading
- **Console logs**: JavaScript validation logs trong controller
- **Spring Boot logs**: Server-side validation errors
- **Thymeleaf debug**: Template processing errors

---

*Hệ thống validation này đảm bảo tính bảo mật cao, trải nghiệm người dùng tốt và khả năng maintain code dễ dàng. Mọi thắc mắc vui lòng liên hệ team development.* 