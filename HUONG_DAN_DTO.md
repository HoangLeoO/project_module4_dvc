# Hướng dẫn sử dụng DTO để hiển thị thông tin từ nhiều bảng

## 📚 Tổng quan

DTO (Data Transfer Object) là pattern dùng để truyền dữ liệu giữa các layer trong ứng dụng. Khi cần hiển thị thông tin từ nhiều bảng, DTO giúp:

1. **Tối ưu hiệu suất**: Chỉ SELECT các field cần thiết
2. **Tránh vấn đề Lazy Loading**: Không bị lỗi khi truy cập quan hệ
3. **Kiểm soát dữ liệu**: Chỉ trả về những gì cần thiết
4. **Bảo mật**: Không expose toàn bộ entity ra ngoài

---

## 🎯 3 Cách xử lý DTO

### **Cách 1: Constructor-based Projection** ⭐ (Khuyến nghị)

#### Ưu điểm:
- ✅ Hiệu suất tốt nhất (chỉ SELECT field cần thiết)
- ✅ Type-safe
- ✅ Dễ debug
- ✅ Linh hoạt

#### Cách sử dụng:

```java
// 1. Tạo DTO class với constructor
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpsDossierDetailDTO {
    private Long dossierId;
    private String dossierCode;
    private String applicantFullName;
    private String serviceName;
    // ... các field khác
    
    // Constructor này sẽ được dùng trong JPQL
    public OpsDossierDetailDTO(Long dossierId, String dossierCode, 
                                String applicantFullName, String serviceName) {
        this.dossierId = dossierId;
        this.dossierCode = dossierCode;
        this.applicantFullName = applicantFullName;
        this.serviceName = serviceName;
    }
}

// 2. Tạo query trong Repository
@Query("""
    SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO(
        d.id,
        d.dossierCode,
        applicant.fullName,
        service.serviceName
    )
    FROM OpsDossier d
    JOIN d.applicant applicant
    JOIN d.service service
    WHERE d.id = :id
    """)
Optional<OpsDossierDetailDTO> findDossierDetailById(@Param("id") Long id);
```

#### SQL được generate:
```sql
SELECT 
    d.id,
    d.dossier_code,
    u.full_name,
    s.service_name
FROM ops_dossiers d
INNER JOIN sys_users u ON d.applicant_id = u.id
INNER JOIN cat_services s ON d.service_id = s.id
WHERE d.id = ?
```

---

### **Cách 2: Interface-based Projection**

#### Ưu điểm:
- ✅ Code ngắn gọn
- ✅ Không cần constructor
- ✅ Spring tự động implement

#### Nhược điểm:
- ❌ Ít linh hoạt hơn
- ❌ Khó debug
- ❌ Có thể gây N+1 query nếu dùng nested projection

#### Cách sử dụng:

```java
// 1. Tạo interface projection
public interface OpsDossierProjection {
    Long getId();
    String getDossierCode();
    
    // Nested projection
    ApplicantProjection getApplicant();
    
    interface ApplicantProjection {
        String getFullName();
    }
}

// 2. Sử dụng trong Repository
// Không cần @Query, Spring tự động generate
List<OpsDossierProjection> findAllBy();

// Hoặc với @Query
@Query("SELECT d FROM OpsDossier d JOIN FETCH d.applicant JOIN FETCH d.service")
List<OpsDossierProjection> findAllDossiers();
```

---

### **Cách 3: Manual Mapping**

#### Ưu điểm:
- ✅ Linh hoạt nhất
- ✅ Có thể xử lý logic phức tạp

#### Nhược điểm:
- ❌ Code dài
- ❌ Dễ gây N+1 query nếu không cẩn thận
- ❌ Tốn performance

#### Cách sử dụng:

```java
// Service layer
public OpsDossierDetailDTO getDossierDetail(Long id) {
    OpsDossier dossier = dossierRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Not found"));
    
    // Manual mapping
    return OpsDossierDetailDTO.builder()
        .dossierId(dossier.getId())
        .dossierCode(dossier.getDossierCode())
        .applicantFullName(dossier.getApplicant().getFullName())
        .serviceName(dossier.getService().getServiceName())
        .build();
}
```

⚠️ **Lưu ý**: Cách này dễ gây lỗi LazyInitializationException nếu không dùng `@Transactional` hoặc `JOIN FETCH`.

---

## 🔧 Ví dụ thực tế trong project

### Ví dụ 1: Hiển thị danh sách hồ sơ

```java
// Repository
@Query("""
    SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO(
        d.id,
        d.dossierCode,
        d.dossierStatus,
        d.submissionDate,
        applicant.fullName,
        service.serviceName,
        handler.fullName
    )
    FROM OpsDossier d
    JOIN d.applicant applicant
    JOIN d.service service
    LEFT JOIN d.currentHandler handler
    ORDER BY d.submissionDate DESC
    """)
List<OpsDossierSummaryDTO> findAllDossierSummaries();

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

### Ví dụ 2: Hiển thị chi tiết hồ sơ (từ 4 bảng)

```java
// DTO
@Data
@AllArgsConstructor
public class OpsDossierDetailDTO {
    // Từ ops_dossiers
    private Long dossierId;
    private String dossierCode;
    
    // Từ sys_users (applicant)
    private String applicantFullName;
    
    // Từ cat_services
    private String serviceName;
    
    // Từ sys_users (handler)
    private String handlerFullName;
    
    // Từ sys_departments
    private String handlerDeptName;
}

// Repository
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

---

## 📊 So sánh hiệu suất

| Phương pháp | Performance | Code Length | Flexibility | Khuyến nghị |
|-------------|-------------|-------------|-------------|-------------|
| Constructor-based | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ✅ Dùng cho hầu hết trường hợp |
| Interface-based | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ✅ Dùng cho query đơn giản |
| Manual Mapping | ⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⚠️ Chỉ dùng khi cần logic phức tạp |

---

## 🚀 Best Practices

### 1. Đặt tên DTO rõ ràng
```java
✅ OpsDossierDetailDTO      // Rõ ràng, biết ngay là chi tiết
✅ OpsDossierSummaryDTO     // Rõ ràng, biết ngay là tóm tắt
❌ DossierDTO               // Không rõ mục đích
```

### 2. Tạo nhiều DTO cho các use case khác nhau
```java
// Cho list view (ít field)
OpsDossierSummaryDTO

// Cho detail view (nhiều field)
OpsDossierDetailDTO

// Cho form create/update
OpsDossierCreateDTO
OpsDossierUpdateDTO
```

### 3. Sử dụng LEFT JOIN cho quan hệ optional
```java
// currentHandler có thể null
LEFT JOIN d.currentHandler handler

// applicant luôn có (NOT NULL)
JOIN d.applicant applicant
```

### 4. Tránh N+1 query
```java
❌ BAD: Không JOIN, sẽ gây N+1 query
@Query("SELECT d FROM OpsDossier d")
List<OpsDossier> findAll();

✅ GOOD: Dùng DTO với JOIN
@Query("""
    SELECT new ...DTO(d.id, applicant.fullName, ...)
    FROM OpsDossier d
    JOIN d.applicant applicant
    """)
List<OpsDossierSummaryDTO> findAllSummaries();
```

### 5. Sử dụng @Transactional(readOnly = true)
```java
@Service
@Transactional(readOnly = true)  // Tối ưu cho read operations
public class OpsDossierService {
    // ...
}
```

---

## 🐛 Các lỗi thường gặp

### 1. LazyInitializationException
```java
❌ Lỗi:
public OpsDossierDetailDTO getDossierDetail(Long id) {
    OpsDossier dossier = repository.findById(id).get();
    // Lỗi ở đây vì applicant là LAZY
    return new OpsDossierDetailDTO(dossier.getApplicant().getFullName());
}

✅ Giải pháp: Dùng DTO với JPQL JOIN
@Query("SELECT new ...DTO(...) FROM OpsDossier d JOIN d.applicant ...")
```

### 2. Constructor không khớp
```java
❌ Lỗi:
@Query("SELECT new ...DTO(d.id, d.code) FROM ...")  // 2 tham số
// Nhưng constructor có 3 tham số

✅ Giải pháp: Đảm bảo số lượng và thứ tự tham số khớp
```

### 3. Package name sai trong JPQL
```java
❌ Lỗi:
SELECT new dto.OpsDossierDetailDTO(...)  // Thiếu full package

✅ Đúng:
SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO(...)
```

---

## 📝 Testing

```java
@SpringBootTest
class OpsDossierServiceTest {
    
    @Autowired
    private OpsDossierService dossierService;
    
    @Test
    void testGetDossierDetail() {
        // Given
        Long dossierId = 1L;
        
        // When
        OpsDossierDetailDTO dto = dossierService.getDossierDetail(dossierId);
        
        // Then
        assertNotNull(dto);
        assertNotNull(dto.getApplicantFullName());
        assertNotNull(dto.getServiceName());
    }
}
```

---

## 🎓 Kết luận

- **Dùng Constructor-based Projection** cho hầu hết các trường hợp
- **Dùng Interface-based Projection** cho query đơn giản, ít field
- **Tránh Manual Mapping** trừ khi thực sự cần thiết
- Luôn kiểm tra SQL được generate để tối ưu hiệu suất
- Tạo nhiều DTO cho các use case khác nhau

---

## 📚 Tài liệu tham khảo

- [Spring Data JPA Projections](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections)
- [JPQL Constructor Expressions](https://docs.oracle.com/javaee/7/tutorial/persistence-querylanguage004.htm)
