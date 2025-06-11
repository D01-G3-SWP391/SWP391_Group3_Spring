# 🌍 Hướng dẫn Internationalization (i18n) cho dự án Job4You

## 📋 Tổng quan
Dự án đã được cấu hình đầy đủ để hỗ trợ đa ngôn ngữ (Tiếng Việt và Tiếng Anh).

## 🔧 Cấu hình đã hoàn thành

### 1. Spring Boot Configuration
- ✅ `ConfigI18N.java` - Cấu hình LocaleResolver và LocaleChangeInterceptor
- ✅ `MessageSourceConfig.java` - Cấu hình MessageSource
- ✅ `application.properties` - Cấu hình encoding và basename

### 2. Properties Files
- ✅ `messages.properties` - Ngôn ngữ mặc định (English)
- ✅ `messages_en.properties` - Tiếng Anh
- ✅ `messages_vi.properties` - Tiếng Việt
- ✅ 200+ keys được tổ chức theo chức năng

### 3. Navbar Language Switcher
- ✅ Dropdown chuyển đổi ngôn ngữ với cờ quốc gia
- ✅ Hiển thị ngôn ngữ hiện tại
- ✅ Lưu lựa chọn trong cookie

## 🚀 Cách sử dụng trong Template

### Thay thế hardcoded text:
```html
<!-- TRƯỚC -->
<h1>Tìm việc làm mơ ước của bạn</h1>

<!-- SAU -->
<h1 th:text="#{home.search.title}">Tìm việc làm mơ ước của bạn</h1>
```

### Sử dụng trong placeholders:
```html
<!-- TRƯỚC -->
<input type="text" placeholder="Nhập họ và tên *" />

<!-- SAU -->
<input type="text" th:placeholder="#{register.fullName.placeholder}" />
```

### Sử dụng trong attributes:
```html
<!-- TRƯỚC -->
<title>Đăng ký Sinh viên - JobPortal</title>

<!-- SAU -->
<title th:text="#{register.student.title} + ' - JobPortal'">Đăng ký Sinh viên - JobPortal</title>
```

## 📝 Ví dụ cụ thể với một số keys quan trọng:

### Navigation
```properties
nav.home=Trang chủ
nav.jobs=Việc làm
nav.login=Đăng nhập
nav.register=Đăng ký
nav.logout=Đăng xuất
```

### Homepage
```properties
home.search.title=Tìm việc làm mơ ước của bạn
home.featuredJobs.title=Việc làm nổi bật
home.viewDetails=Xem chi tiết
home.apply=Ứng tuyển
```

### Forms
```properties
form.save=Lưu
form.cancel=Hủy
form.edit=Chỉnh sửa
form.delete=Xóa
```

### Profile
```properties
profile.phone=Phone number
profile.email=Email
profile.address=Address
profile.editInformation=Edit information
```

## 🔄 Test chuyển đổi ngôn ngữ

1. Mở website
2. Click vào dropdown "NGÔN NGỮ" ở navbar
3. Chọn "🇺🇸 English" hoặc "🇻🇳 Tiếng Việt"
4. Trang sẽ reload với ngôn ngữ đã chọn

## 📂 Cấu trúc Properties Files

```
src/main/resources/
├── messages.properties (Default - English)
├── messages_en.properties (English)
├── messages_vi.properties (Vietnamese)
├── messages_fr.properties (French - Empty, ready for future)
└── messages_ja.properties (Japanese - Empty, ready for future)
```

## 🎯 Bước tiếp theo để hoàn thiện:

1. **Thay thế từng template**:
   - `homePage.html` - Trang chủ
   - `loginPage.html` - Đăng nhập  
   - `registerStudentPage.html` - Đăng ký sinh viên
   - `profileStudent.html` - Hồ sơ sinh viên
   - Và các template khác...

2. **Quy trình thay thế**:
   ```bash
   # Tìm hardcoded text
   grep -r "Tìm việc làm" src/main/resources/templates/
   
   # Thay thế bằng
   th:text="#{home.search.title}"
   ```

3. **Test kỹ lưỡng**:
   - Kiểm tra tất cả trang với cả 2 ngôn ngữ
   - Đảm bảo không có text bị thiếu
   - Kiểm tra responsive với text dài/ngắn

## ⚠️ Lưu ý quan trọng:

- Luôn giữ fallback text trong template
- Sử dụng format `#{key}` cho Thymeleaf
- Test với cả 2 ngôn ngữ trước khi commit
- Thêm keys mới vào cả 3 files properties

## 🎨 Styling cho Language Switcher

CSS đã được thêm vào navbar để hiển thị dropdown đẹp với:
- Hiệu ứng hover
- Animation smooth
- Hiển thị cờ quốc gia
- Highlight ngôn ngữ hiện tại 