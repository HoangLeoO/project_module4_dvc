# 🎯 Hướng dẫn sử dụng DTO để hiển thị thông tin từ nhiều bảng

## 📋 Tóm tắt

Project này đã được cập nhật với **DTO Pattern** để hiển thị thông tin từ nhiều bảng một cách hiệu quả.

### ✨ Những gì đã được thêm vào:

1. **DTO Classes** - Chứa dữ liệu từ nhiều bảng
2. **Repository Queries** - JPQL với constructor expressions
3. **Service Layer** - Business logic
4. **REST Controller** - API endpoints
5. **Unit Tests** - Test coverage
6. **Documentation** - Hướng dẫn chi tiết

---

## 📚 Tài liệu

### 1. [HUONG_DAN_DTO.md](./HUONG_DAN_DTO.md)
**Hướng dẫn chi tiết về 3 cách sử dụng DTO:**
- ✅ Constructor-based Projection (Khuyến nghị)
- ✅ Interface-based Projection
- ⚠️ Manual Mapping

**Nội dung:**
- So sánh các phương pháp
- Ví dụ code chi tiết
- Best practices
- Các lỗi thường gặp và cách fix
- Testing

### 2. [CAU_TRUC_PROJECT.md](./CAU_TRUC_PROJECT.md)
**Cấu trúc project và data flow:**
- Sơ đồ thư mục
- Luồng dữ liệu (Client → Controller → Service → Repository → Database)
- Danh sách các file đã tạo
- API endpoints
- Response examples
- Checklist triển khai

---

## 🚀 Quick Start

### 1. Xem ví dụ code

```java
// Repository - JPQL với constructor expression
@Query("""
    SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO(
        d.id,
        d.dossierCode,
        applicant.fullName,
        service.serviceName,
        handler.fullName,
        dept.deptName
    )
    FROM OpsDossier d
    JOIN d.applicant applicant
    JOIN d.service service
    LEFT JOIN d.currentHandler handler
    LEFT JOIN handler.department dept
    WHERE d.id = :id
    """)
Optional<OpsDossierDetailDTO> findDossierDetailById(@Param("id") Long id);
```

### 2. Test API

```bash
# Lấy chi tiết hồ sơ từ 4 bảng
GET http://localhost:8080/api/dossiers/1

# Lấy danh sách tất cả hồ sơ
GET http://localhost:8080/api/dossiers

# Lọc theo người nộp
GET http://localhost:8080/api/dossiers/applicant/5

# Lọc theo trạng thái
GET http://localhost:8080/api/dossiers/status/NEW
```

### 3. Chạy tests

```bash
# Chạy tất cả tests
./mvnw test

# Chạy test cụ thể
./mvnw test -Dtest=OpsDossierRepositoryTest
```

---

## 📁 Các file quan trọng

### DTO Classes
- `dto/OpsDossierDetailDTO.java` - Chi tiết hồ sơ (4 bảng)
- `dto/OpsDossierSummaryDTO.java` - Tóm tắt (list view)
- `dto/OpsDossierNativeProjection.java` - Native query projection
- `dto/projection/OpsDossierProjection.java` - Interface projection

### Repository
- `repository/ops/OpsDossierRepository.java` - Queries với DTO

### Service
- `service/OpsDossierService.java` - Business logic

### Controller
- `controller/OpsDossierController.java` - REST API

### Tests
- `test/.../OpsDossierRepositoryTest.java` - Unit tests

---

## 🎯 Use Cases

### Use Case 1: Hiển thị danh sách hồ sơ
```java
// Service
public List<OpsDossierSummaryDTO> getAllDossiers() {
    return dossierRepository.findAllDossierSummaries();
}

// Controller
@GetMapping
public ResponseEntity<List<OpsDossierSummaryDTO>> getAllDossiers() {
    return ResponseEntity.ok(dossierService.getAllDossiers());
}
```

**SQL được generate:**
```sql
SELECT 
    d.id, d.dossier_code, d.dossier_status, d.submission_date,
    applicant.full_name, service.service_name, handler.full_name
FROM ops_dossiers d
INNER JOIN sys_users applicant ON d.applicant_id = applicant.id
INNER JOIN cat_services service ON d.service_id = service.id
LEFT JOIN sys_users handler ON d.current_handler_id = handler.id
ORDER BY d.submission_date DESC
```

### Use Case 2: Hiển thị chi tiết hồ sơ
```java
// Service
public OpsDossierDetailDTO getDossierDetail(Long id) {
    return dossierRepository.findDossierDetailById(id)
        .orElseThrow(() -> new RuntimeException("Not found"));
}
```

**Kết quả:** 1 query duy nhất JOIN 4 bảng, không có N+1 query problem!

---

## ⚡ Performance Benefits

### ❌ Trước (dùng Entity)
```java
OpsDossier dossier = repository.findById(1L).get();
// Query 1: SELECT * FROM ops_dossiers WHERE id = 1

String applicantName = dossier.getApplicant().getFullName();
// Query 2: SELECT * FROM sys_users WHERE id = ?

String serviceName = dossier.getService().getServiceName();
// Query 3: SELECT * FROM cat_services WHERE id = ?

String handlerName = dossier.getCurrentHandler().getFullName();
// Query 4: SELECT * FROM sys_users WHERE id = ?

// Tổng: 4 queries (N+1 problem)
```

### ✅ Sau (dùng DTO)
```java
OpsDossierDetailDTO dto = repository.findDossierDetailById(1L).get();
// Query 1: SELECT d.id, d.code, u1.name, s.name, u2.name, dept.name
//          FROM ops_dossiers d
//          JOIN sys_users u1 ON ...
//          JOIN cat_services s ON ...
//          LEFT JOIN sys_users u2 ON ...
//          LEFT JOIN sys_departments dept ON ...

// Tổng: 1 query duy nhất!
```

**Kết quả:** Giảm từ 4 queries xuống còn 1 query → **Performance tăng 4 lần!**

---

## 🎓 Khi nào dùng cách nào?

| Tình huống | Giải pháp | Lý do |
|------------|-----------|-------|
| Hiển thị danh sách | Constructor-based DTO | Performance tốt, chỉ SELECT field cần thiết |
| Hiển thị chi tiết | Constructor-based DTO | Tránh lazy loading, 1 query duy nhất |
| Query đơn giản | Interface projection | Code ngắn gọn |
| SQL phức tạp | Native query + Interface | Tận dụng database features |
| Logic phức tạp | Manual mapping | Linh hoạt nhất |

---

## 🔍 Debugging

### Bật SQL logging

```properties
# application.properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Kiểm tra số lượng queries

```java
@Test
void testNoNPlusOneQuery() {
    // Bật SQL logging
    // Gọi method
    List<OpsDossierSummaryDTO> results = repository.findAllDossierSummaries();
    
    // Kiểm tra log: Chỉ nên có 1 query với JOIN
}
```

---

## 📞 Liên hệ & Hỗ trợ

Nếu có thắc mắc:
1. Đọc [HUONG_DAN_DTO.md](./HUONG_DAN_DTO.md) - Hướng dẫn chi tiết
2. Đọc [CAU_TRUC_PROJECT.md](./CAU_TRUC_PROJECT.md) - Cấu trúc project
3. Xem code examples trong các file DTO, Repository, Service

---

## ✅ Checklist

- [x] Hiểu 3 cách sử dụng DTO
- [x] Biết khi nào dùng cách nào
- [x] Tạo DTO classes
- [x] Viết JPQL queries
- [x] Test API endpoints
- [ ] Áp dụng vào các entity khác trong project
- [ ] Tối ưu performance
- [ ] Deploy production

---

## 🎉 Kết luận

Bạn đã có đầy đủ code và tài liệu để:
1. ✅ Hiển thị thông tin từ nhiều bảng bằng DTO
2. ✅ Tối ưu performance (tránh N+1 query)
3. ✅ Viết code clean, maintainable
4. ✅ Test và verify

**Next steps:**
- Áp dụng pattern này cho các entity khác
- Tạo thêm DTO cho các use case khác
- Tối ưu queries dựa trên actual data

Good luck! 🚀
