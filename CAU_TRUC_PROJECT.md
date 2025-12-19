# Cấu trúc Project - DTO Pattern

## 📁 Cấu trúc thư mục

```
src/main/java/org/example/project_module4_dvc/
│
├── dto/                                    # ✨ MỚI - Chứa các DTO classes
│   ├── OpsDossierDetailDTO.java           # DTO chi tiết (từ 4 bảng)
│   ├── OpsDossierSummaryDTO.java          # DTO tóm tắt (cho list view)
│   ├── OpsDossierNativeProjection.java    # Interface projection cho native query
│   └── projection/
│       └── OpsDossierProjection.java      # Interface-based projection
│
├── entity/                                 # Entity classes (JPA)
│   ├── ops/
│   │   └── OpsDossier.java                # Entity hồ sơ
│   ├── sys/
│   │   ├── SysUser.java                   # Entity người dùng
│   │   └── SysDepartment.java             # Entity phòng ban
│   └── cat/
│       └── CatService.java                # Entity dịch vụ
│
├── repository/                             # Repository layer
│   └── ops/
│       └── OpsDossierRepository.java      # ✨ CẬP NHẬT - Thêm DTO queries
│
├── service/                                # Service layer
│   └── OpsDossierService.java             # ✨ MỚI - Business logic
│
└── controller/                             # Controller layer
    └── OpsDossierController.java          # ✨ MỚI - REST API endpoints

src/test/java/
└── repository/ops/
    └── OpsDossierRepositoryTest.java      # ✨ MỚI - Unit tests
```

---

## 🔄 Luồng dữ liệu (Data Flow)

```
┌─────────────┐
│   Client    │
│  (Browser)  │
└──────┬──────┘
       │ HTTP Request
       ▼
┌─────────────────────────────────────────┐
│         Controller Layer                │
│  OpsDossierController.java              │
│  - Nhận request                         │
│  - Validate input                       │
│  - Trả về ResponseEntity<DTO>           │
└──────┬──────────────────────────────────┘
       │ Call service method
       ▼
┌─────────────────────────────────────────┐
│         Service Layer                   │
│  OpsDossierService.java                 │
│  - Business logic                       │
│  - Transaction management               │
│  - Gọi repository                       │
└──────┬──────────────────────────────────┘
       │ Call repository method
       ▼
┌─────────────────────────────────────────┐
│         Repository Layer                │
│  OpsDossierRepository.java              │
│  - JPQL/Native Query                    │
│  - JOIN nhiều bảng                      │
│  - Trả về DTO (không phải Entity)       │
└──────┬──────────────────────────────────┘
       │ Execute SQL
       ▼
┌─────────────────────────────────────────┐
│         Database                        │
│  - ops_dossiers                         │
│  - sys_users                            │
│  - cat_services                         │
│  - sys_departments                      │
└─────────────────────────────────────────┘
```

---

## 🎯 Các file đã tạo và mục đích

### 1. **DTO Classes**

#### `OpsDossierDetailDTO.java`
- **Mục đích**: Hiển thị thông tin chi tiết hồ sơ từ 4 bảng
- **Sử dụng**: Detail view, API endpoint `/api/dossiers/{id}`
- **Bảng liên quan**: 
  - ops_dossiers
  - sys_users (applicant + handler)
  - cat_services
  - sys_departments

#### `OpsDossierSummaryDTO.java`
- **Mục đích**: Hiển thị danh sách hồ sơ (tóm tắt)
- **Sử dụng**: List view, API endpoint `/api/dossiers`
- **Ưu điểm**: Ít field hơn → performance tốt hơn

#### `OpsDossierNativeProjection.java`
- **Mục đích**: Interface projection cho Native SQL
- **Khi nào dùng**: Cần SQL phức tạp, database-specific features

#### `OpsDossierProjection.java`
- **Mục đích**: Interface-based projection (cách 2)
- **Ưu điểm**: Code ngắn gọn, không cần constructor

---

### 2. **Repository**

#### `OpsDossierRepository.java`
Các method đã thêm:

| Method | Return Type | Mục đích |
|--------|-------------|----------|
| `findDossierDetailById()` | `Optional<OpsDossierDetailDTO>` | Lấy chi tiết 1 hồ sơ |
| `findAllDossierSummaries()` | `List<OpsDossierSummaryDTO>` | Lấy tất cả hồ sơ |
| `findDossiersByApplicantId()` | `List<OpsDossierSummaryDTO>` | Lọc theo người nộp |
| `findDossiersByHandlerId()` | `List<OpsDossierSummaryDTO>` | Lọc theo cán bộ |
| `findDossiersByStatus()` | `List<OpsDossierSummaryDTO>` | Lọc theo trạng thái |
| `findDossierByIdNative()` | `Optional<OpsDossierNativeProjection>` | Ví dụ native query |

---

### 3. **Service**

#### `OpsDossierService.java`
- **Mục đích**: Business logic layer
- **Annotation**: `@Transactional(readOnly = true)` cho performance
- **Methods**:
  - `getDossierDetail(Long id)`
  - `getAllDossiers()`
  - `getDossiersByApplicant(Long applicantId)`
  - `getDossiersByHandler(Long handlerId)`
  - `getDossiersByStatus(String status)`

---

### 4. **Controller**

#### `OpsDossierController.java`
REST API endpoints:

| Endpoint | Method | Return |
|----------|--------|--------|
| `/api/dossiers/{id}` | GET | Chi tiết 1 hồ sơ |
| `/api/dossiers` | GET | Danh sách tất cả |
| `/api/dossiers/applicant/{id}` | GET | Hồ sơ của người nộp |
| `/api/dossiers/handler/{id}` | GET | Hồ sơ của cán bộ |
| `/api/dossiers/status/{status}` | GET | Hồ sơ theo trạng thái |

---

## 🚀 Cách sử dụng

### 1. Test API với Postman/curl

```bash
# Lấy chi tiết hồ sơ
GET http://localhost:8080/api/dossiers/1

# Lấy tất cả hồ sơ
GET http://localhost:8080/api/dossiers

# Lấy hồ sơ của người nộp ID=5
GET http://localhost:8080/api/dossiers/applicant/5

# Lấy hồ sơ của cán bộ ID=3
GET http://localhost:8080/api/dossiers/handler/3

# Lấy hồ sơ có trạng thái NEW
GET http://localhost:8080/api/dossiers/status/NEW
```

### 2. Response ví dụ

#### GET `/api/dossiers/1`
```json
{
  "dossierId": 1,
  "dossierCode": "HS001",
  "dossierStatus": "PROCESSING",
  "submissionDate": "2024-01-15T10:30:00",
  "dueDate": "2024-01-30T17:00:00",
  "applicantId": 5,
  "applicantUsername": "nguyenvana",
  "applicantFullName": "Nguyễn Văn A",
  "applicantUserType": "CITIZEN",
  "handlerId": 3,
  "handlerUsername": "tranthib",
  "handlerFullName": "Trần Thị B",
  "handlerDeptId": 2,
  "handlerDeptName": "Phòng Đăng ký kinh doanh",
  "serviceId": 10,
  "serviceName": "Đăng ký kinh doanh hộ cá thể",
  "serviceCode": "DKD001",
  "processingDays": 15
}
```

#### GET `/api/dossiers`
```json
[
  {
    "dossierId": 1,
    "dossierCode": "HS001",
    "dossierStatus": "PROCESSING",
    "submissionDate": "2024-01-15T10:30:00",
    "applicantFullName": "Nguyễn Văn A",
    "serviceName": "Đăng ký kinh doanh hộ cá thể",
    "handlerFullName": "Trần Thị B"
  },
  {
    "dossierId": 2,
    "dossierCode": "HS002",
    "dossierStatus": "NEW",
    "submissionDate": "2024-01-16T14:20:00",
    "applicantFullName": "Lê Văn C",
    "serviceName": "Cấp giấy phép xây dựng",
    "handlerFullName": null
  }
]
```

---

## 📊 So sánh Entity vs DTO

### ❌ Trước (dùng Entity)

```java
// Controller
@GetMapping("/{id}")
public ResponseEntity<OpsDossier> getDossier(@PathVariable Long id) {
    OpsDossier dossier = repository.findById(id).get();
    return ResponseEntity.ok(dossier);
}

// Response sẽ có vấn đề:
// 1. LazyInitializationException khi access applicant, service, handler
// 2. Trả về toàn bộ entity (bao gồm cả field không cần thiết)
// 3. Có thể expose sensitive data (password_hash, ...)
// 4. Circular reference nếu có bidirectional relationship
```

### ✅ Sau (dùng DTO)

```java
// Controller
@GetMapping("/{id}")
public ResponseEntity<OpsDossierDetailDTO> getDossier(@PathVariable Long id) {
    OpsDossierDetailDTO dto = service.getDossierDetail(id);
    return ResponseEntity.ok(dto);
}

// Response:
// 1. Không có lazy loading exception
// 2. Chỉ trả về field cần thiết
// 3. Kiểm soát được dữ liệu
// 4. Performance tốt hơn (1 query duy nhất với JOIN)
```

---

## 🎓 Best Practices đã áp dụng

✅ **Separation of Concerns**: DTO tách biệt với Entity  
✅ **Performance**: Chỉ SELECT field cần thiết  
✅ **Type Safety**: Dùng constructor-based projection  
✅ **Testability**: Có unit tests  
✅ **Documentation**: Comment rõ ràng  
✅ **Naming Convention**: Tên DTO rõ ràng (Detail, Summary)  
✅ **Transaction Management**: `@Transactional(readOnly = true)`  
✅ **Error Handling**: `Optional` + `orElseThrow()`  

---

## 📝 Checklist triển khai

- [x] Tạo package `dto`
- [x] Tạo DTO classes với constructor
- [x] Cập nhật Repository với JPQL queries
- [x] Tạo Service layer
- [x] Tạo Controller với REST endpoints
- [x] Tạo unit tests
- [x] Viết documentation
- [ ] Test API với Postman
- [ ] Kiểm tra SQL log (không có N+1 query)
- [ ] Deploy và verify

---

## 🔗 Tài liệu liên quan

- [HUONG_DAN_DTO.md](./HUONG_DAN_DTO.md) - Hướng dẫn chi tiết về DTO
- [Spring Data JPA Docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
