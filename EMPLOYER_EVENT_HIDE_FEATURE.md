# Chức năng Ẩn/Hiện Event cho Employer

## Tổng quan
Chức năng này cho phép Employer ẩn hoặc hiện lại các event đã được admin duyệt mà không xóa chúng khỏi database (soft delete).

## Các thay đổi đã thực hiện

### 1. Model (Event.java)
- **Thêm status `INACTIVE`** vào enum `EventStatus`:
  ```java
  public enum EventStatus {
      ACTIVE,     // Đang mở đăng ký
      FULL,       // Đã đủ người
      CANCELLED,  // Đã hủy
      INACTIVE    // Đã ẩn (soft delete)
  }
  ```

### 2. Controller (EmployerEventController.java)
- **Thêm method `hideEvent()`**: Ẩn event bằng cách đổi status thành `INACTIVE`
- **Thêm method `unhideEvent()`**: Hiện event bằng cách đổi status thành `ACTIVE`
- **Kiểm tra quyền**: Chỉ employer sở hữu event mới có thể ẩn/hiện

### 3. Repository (IEventRepository.java)
- **Thêm method `findActiveApprovedEvents()`**: Tìm events đã approve và active
- **Thêm method `countByApprovalStatusAndEventStatus()`**: Đếm events theo cả approval và event status
- **Thêm method `countEventsByJobFieldAndEventStatus()`**: Đếm events theo job field và status
- **Thêm method `findRelatedActiveEvents()`**: Tìm events liên quan (chỉ active)
- **Thêm method search với filter active**: Tìm kiếm events với filter active

### 4. Service (EventServiceImpl.java)
- **Cập nhật `getUpcomingEvents()`**: Chỉ lấy active events
- **Cập nhật `searchEvents()`**: Chỉ tìm active events
- **Cập nhật `countApprovedEvents()`**: Chỉ đếm active events
- **Cập nhật `countEventsByJobField()`**: Chỉ đếm active events
- **Cập nhật `getRelatedEvents()`**: Chỉ lấy active events
- **Thêm method `findActiveApprovedEvents()`**: Implement method mới

### 5. Controller (EventController.java)
- **Cập nhật `eventsPage()`**: Sử dụng method mới để chỉ hiển thị active events ở trang chủ

### 6. Template (eventList.html)
- **Thêm nút "Ẩn"**: Hiển thị khi event có status `ACTIVE`
- **Thêm nút "Hiện"**: Hiển thị khi event có status `INACTIVE`
- **Cập nhật nút "Hủy"**: Chỉ hiển thị khi event chưa bị hủy

## Cách hoạt động

### Đối với Employer:
1. **Trong eventList.html**: Employer thấy tất cả events của mình (bất kể status)
2. **Nút "Ẩn"**: Chỉ hiển thị với events có status `ACTIVE`
3. **Nút "Hiện"**: Chỉ hiển thị với events có status `INACTIVE`
4. **Sau khi ẩn**: Event vẫn hiển thị trong eventList nhưng có status `INACTIVE`

### Đối với Public (trang chủ):
1. **Chỉ hiển thị**: Events có `ApprovalStatus.APPROVED` VÀ `EventStatus.ACTIVE`
2. **Events bị ẩn**: Không hiển thị ở trang chủ
3. **Events bị hủy**: Không hiển thị ở trang chủ

## URL Endpoints
- `POST /Employer/Events/Hide/{eventId}`: Ẩn event
- `POST /Employer/Events/Unhide/{eventId}`: Hiện event

## Bảo mật
- Chỉ employer sở hữu event mới có thể ẩn/hiện
- Kiểm tra quyền trước khi thực hiện thao tác
- Flash messages thông báo kết quả

## Lợi ích
1. **Soft delete**: Không mất dữ liệu
2. **Linh hoạt**: Có thể ẩn/hiện bất cứ lúc nào
3. **An toàn**: Events bị ẩn không hiển thị công khai
4. **Dễ quản lý**: Employer vẫn thấy tất cả events trong danh sách

## Lưu ý
- Events có status `INACTIVE` không thể đăng ký
- Method `canRegister()` đã có sẵn kiểm tra `EventStatus.ACTIVE`
- Không ảnh hưởng đến logic hiện tại của eventList (vẫn hiển thị tất cả events của employer) 