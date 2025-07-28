# Swimlane Diagram - Job4You Main Flow

## Overview
Job4You system has 3 main actors:
1. **Employer** - Post jobs, manage candidates, send interview schedules, chat
2. **Admin** - Approve job posts from employers
3. **Student** - Apply for jobs, receive interview schedules, chat with employers

## Swimlane Diagram

```mermaid
graph TB
    subgraph col1[" "]
        subgraph EMPLOYER["👔 EMPLOYER"]
            E1[Post Job]
            E2[View Applications] 
            E3[Chat & Interview]
            E4{Accept/Reject?}
            E5[Accept]
            E6[Reject]
        end
    end
    
    subgraph col2[" "]
        subgraph ADMIN["🛡️ ADMIN"]
            A1[Review Job]
            A2{Approve?}
            A3[Approved]
            A4[Rejected]
        end
    end
    
    subgraph col3[" "]
        subgraph STUDENT["🎓 STUDENT"]
            S1[Search Jobs]
            S2[Apply with CV]
            S3[Chat & Interview]
            S4[Get Result]
        end
    end

    %% Flow
    E1 --> A1
    A1 --> A2
    A2 -->|YES| A3
    A2 -->|NO| A4
    A4 --> E1
    
    A3 --> S1
    S1 --> S2
    S2 --> E2
    
    E2 --> E3
    E3 <--> S3
    E3 --> E4
    S3 --> E4
    
    E4 -->|ACCEPT| E5
    E4 -->|REJECT| E6
    E5 --> S4
    E6 --> S4

    %% Hide outer borders
    style col1 fill:none,stroke:none
    style col2 fill:none,stroke:none
    style col3 fill:none,stroke:none

    %% Styling
    classDef employer fill:#E3F2FD,stroke:#1976D2,stroke-width:2px,font-size:16px,font-weight:bold
    classDef admin fill:#FFF3E0,stroke:#F57C00,stroke-width:2px,font-size:16px,font-weight:bold
    classDef student fill:#F3E5F5,stroke:#7B1FA2,stroke-width:2px,font-size:16px,font-weight:bold
    classDef decision fill:#FFF59D,stroke:#F9A825,stroke-width:2px,font-size:18px,font-weight:bold
    classDef result fill:#C8E6C9,stroke:#4CAF50,stroke-width:2px,font-size:16px,font-weight:bold

    class E1,E2,E3 employer
    class E4 decision
    class E5,E6 result
    class A1,A3,A4 admin
    class A2 decision
    class S1,S2,S3,S4 student
```

## Main Flow - Simple & Clean

| Step | Employer | Admin | Student |
|------|----------|-------|---------|
| 1 | **Post Job** | | |
| 2 | | **Review Job** → Approve/Reject | |
| 3 | | | **Search Jobs** → **Apply with CV** |
| 4 | **View Applications** | | |
| 5 | **Chat & Interview** ↔ | | **Chat & Interview** |
| 6 | **Accept/Reject Decision** | | |
| 7 | | | **Get Final Result** |

## Process Flow

1. **Job Creation**: Employer posts → Admin reviews → Approved jobs go live
2. **Application**: Student searches and applies
3. **Communication**: Employer and Student chat → Schedule interview
4. **Decision**: Employer accepts/rejects → Student gets result

## Layout: True Vertical Columns (Swimlane)

### Current Setup:
- **`graph TB`**: Top to Bottom layout 
- **3 separate columns**: Employer, Admin, Student
- **Hidden borders**: Ẩn border ngoài để chỉ hiện swimlane

### Column Order (Left to Right):
1. **👔 EMPLOYER** - Post jobs, manage candidates
2. **🛡️ ADMIN** - Review and approve jobs  
3. **🎓 STUDENT** - Search and apply for jobs

### To Reorder Columns:
Change the order of subgraphs:
```mermaid
subgraph col1[" "]          %% First column
    subgraph STUDENT["🎓 STUDENT"]
subgraph col2[" "]         %% Second column  
    subgraph EMPLOYER["👔 EMPLOYER"]
```

**Result**: Clean 3-column swimlane diagram!
