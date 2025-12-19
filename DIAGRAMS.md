# Sơ đồ minh họa DTO Pattern

## 1. Sơ đồ quan hệ giữa các bảng

```mermaid
erDiagram
    ops_dossiers ||--|| sys_users : "applicant_id"
    ops_dossiers ||--o| sys_users : "current_handler_id"
    ops_dossiers ||--|| cat_services : "service_id"
    sys_users ||--o| sys_departments : "dept_id"
    
    ops_dossiers {
        bigint id PK
        varchar dossier_code
        varchar dossier_status
        datetime submission_date
        bigint applicant_id FK
        bigint current_handler_id FK
        bigint service_id FK
    }
    
    sys_users {
        bigint id PK
        varchar username
        varchar full_name
        varchar user_type
        bigint dept_id FK
    }
    
    cat_services {
        bigint id PK
        varchar service_name
        varchar service_code
        int processing_days
    }
    
    sys_departments {
        bigint id PK
        varchar dept_name
    }
```

## 2. Luồng xử lý DTO

```mermaid
sequenceDiagram
    participant C as Client
    participant Ctrl as Controller
    participant Svc as Service
    participant Repo as Repository
    participant DB as Database
    
    C->>Ctrl: GET /api/dossiers/1
    Ctrl->>Svc: getDossierDetail(1)
    Svc->>Repo: findDossierDetailById(1)
    
    Note over Repo: JPQL với constructor<br/>SELECT new DTO(...)
    
    Repo->>DB: SQL JOIN 4 bảng
    
    Note over DB: SELECT d.id, d.code,<br/>u1.name, s.name,<br/>u2.name, dept.name<br/>FROM ... JOIN ...
    
    DB-->>Repo: ResultSet
    
    Note over Repo: Spring tự động<br/>gọi constructor<br/>tạo DTO object
    
    Repo-->>Svc: OpsDossierDetailDTO
    Svc-->>Ctrl: OpsDossierDetailDTO
    Ctrl-->>C: JSON Response
```

## 3. So sánh Entity vs DTO

```mermaid
graph TB
    subgraph "Cách 1: Dùng Entity (❌ Không tốt)"
        A1[Client Request] --> B1[Controller]
        B1 --> C1[Repository.findById]
        C1 --> D1[Query 1: SELECT ops_dossiers]
        D1 --> E1[OpsDossier Entity]
        E1 --> F1[getApplicant]
        F1 --> G1[Query 2: SELECT sys_users]
        G1 --> H1[getService]
        H1 --> I1[Query 3: SELECT cat_services]
        I1 --> J1[getCurrentHandler]
        J1 --> K1[Query 4: SELECT sys_users]
        K1 --> L1[getDepartment]
        L1 --> M1[Query 5: SELECT sys_departments]
        M1 --> N1[❌ 5 queries - N+1 problem]
    end
    
    subgraph "Cách 2: Dùng DTO (✅ Tốt)"
        A2[Client Request] --> B2[Controller]
        B2 --> C2[Repository.findDossierDetailById]
        C2 --> D2[Query 1: SELECT với JOIN]
        D2 --> E2[OpsDossierDetailDTO]
        E2 --> F2[✅ 1 query duy nhất]
    end
```

## 4. Constructor-based Projection Flow

```mermaid
flowchart LR
    A[JPQL Query] --> B[SELECT new DTO<br/>d.id, d.code,<br/>applicant.name,<br/>service.name]
    B --> C[SQL Generation]
    C --> D[SELECT d.id, d.code,<br/>u.full_name, s.service_name<br/>FROM ops_dossiers d<br/>JOIN sys_users u<br/>JOIN cat_services s]
    D --> E[Execute Query]
    E --> F[ResultSet]
    F --> G[Spring calls<br/>DTO constructor]
    G --> H[OpsDossierDetailDTO<br/>object created]
```

## 5. Kiến trúc 3-layer

```mermaid
graph TD
    subgraph "Presentation Layer"
        A[OpsDossierController]
    end
    
    subgraph "Business Layer"
        B[OpsDossierService]
    end
    
    subgraph "Data Access Layer"
        C[OpsDossierRepository]
        D[OpsDossierDetailDTO]
        E[OpsDossierSummaryDTO]
    end
    
    subgraph "Database"
        F[(ops_dossiers)]
        G[(sys_users)]
        H[(cat_services)]
        I[(sys_departments)]
    end
    
    A --> B
    B --> C
    C --> D
    C --> E
    C --> F
    C --> G
    C --> H
    C --> I
    
    style D fill:#90EE90
    style E fill:#90EE90
```

## 6. Decision Tree: Chọn phương pháp DTO

```mermaid
graph TD
    Start[Cần hiển thị data<br/>từ nhiều bảng?] --> Q1{Query đơn giản?<br/>Ít field?}
    
    Q1 -->|Yes| Q2{Cần linh hoạt<br/>xử lý logic?}
    Q1 -->|No| Q3{Cần SQL phức tạp?<br/>Window functions?}
    
    Q2 -->|No| A[✅ Interface-based<br/>Projection]
    Q2 -->|Yes| B[✅ Constructor-based<br/>Projection]
    
    Q3 -->|Yes| C[✅ Native Query +<br/>Interface Projection]
    Q3 -->|No| B
    
    style A fill:#90EE90
    style B fill:#90EE90
    style C fill:#FFD700
```

## 7. Performance Comparison

```mermaid
graph LR
    subgraph "Entity (Lazy Loading)"
        A1[1 query] --> A2[+ N queries<br/>for relationships]
        A2 --> A3[Total: 1 + N queries]
    end
    
    subgraph "DTO (Constructor-based)"
        B1[1 query with JOIN] --> B2[Total: 1 query]
    end
    
    A3 -.->|"❌ Slow"| Perf1[Performance]
    B2 -.->|"✅ Fast"| Perf1
    
    style B2 fill:#90EE90
    style A3 fill:#FFB6C1
```

## 8. DTO Pattern Benefits

```mermaid
mindmap
  root((DTO Pattern))
    Performance
      Chỉ SELECT field cần thiết
      Tránh N+1 query
      1 query với JOIN
    Security
      Không expose sensitive data
      Kiểm soát dữ liệu trả về
    Maintainability
      Tách biệt Entity và Response
      Dễ test
      Clean code
    Flexibility
      Nhiều DTO cho nhiều use case
      DetailDTO cho detail view
      SummaryDTO cho list view
```

## 9. Common Pitfalls

```mermaid
flowchart TD
    A[Common Mistakes] --> B[LazyInitializationException]
    A --> C[N+1 Query Problem]
    A --> D[Constructor mismatch]
    A --> E[Wrong package name]
    
    B --> B1[✅ Solution: Use DTO with JOIN]
    C --> C1[✅ Solution: Constructor-based Projection]
    D --> D1[✅ Solution: Match parameters order]
    E --> E1[✅ Solution: Use full package name in JPQL]
    
    style B fill:#FFB6C1
    style C fill:#FFB6C1
    style D fill:#FFB6C1
    style E fill:#FFB6C1
    
    style B1 fill:#90EE90
    style C1 fill:#90EE90
    style D1 fill:#90EE90
    style E1 fill:#90EE90
```

## 10. Testing Strategy

```mermaid
graph TD
    A[Testing DTO Queries] --> B[Unit Tests]
    A --> C[Integration Tests]
    A --> D[Performance Tests]
    
    B --> B1[Test Repository methods]
    B --> B2[Verify DTO mapping]
    
    C --> C1[Test full flow]
    C --> C2[Test with real DB]
    
    D --> D1[Check SQL logs]
    D --> D2[Verify no N+1 queries]
    D --> D3[Measure query time]
    
    style A fill:#FFD700
    style B fill:#90EE90
    style C fill:#90EE90
    style D fill:#90EE90
```
