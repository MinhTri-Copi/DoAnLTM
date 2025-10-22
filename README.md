# Hệ Thống Quản Lý Lịch Làm Việc (Work Schedule Management System)

## 📋 Project Overview
**DoAnLTM** is a JavaFX-based desktop application for managing employee work schedules. The system enables administrators to create and manage shift schedules, while employees can register and track their shifts.

**Project Name:** DoAnLTM  
**Version:** 1.0-SNAPSHOT  
**Language:** Java 21  
**Framework:** JavaFX 21.0.6  
**Build Tool:** Maven  
**Database:** MySQL 8.2.0

---

## 🎯 Key Features
- **User Authentication:** Login and registration system
- **Role-Based Access Control:** Admin and employee roles
- **Shift Management:** Create, update, and delete work shifts
- **Registration Management:** Employees can register for shifts
- **Dashboard:** Role-specific dashboards for admins and users
- **Reporting:** Admin reports and statistics on shift assignments
- **Status Tracking:** Track shift registration status (pending, accepted, rejected)

---

## 🏗️ Project Structure

```
DoAnLTM/
├── src/main/java/com/example/doanltm/
│   ├── Controller/              # JavaFX Controllers for UI logic
│   │   ├── LoginController.java
│   │   ├── RegisterController.java
│   │   ├── AdminDashboardController.java
│   │   ├── UserDashboardController.java
│   │   ├── EnhancedUserDashboardController.java
│   │   ├── ShiftManagementController.java
│   │   ├── ScheduleManagementController.java
│   │   └── UserManagementController.java
│   ├── Model/                   # Data Models
│   │   ├── User.java
│   │   ├── CaLam.java          # Shift Model
│   │   ├── DangKy.java         # Registration Model
│   │   ├── ShiftStatusMonth.java
│   │   └── LoginResponse.java
│   ├── DAO/                     # Database Access Objects
│   │   ├── UserDAO.java
│   │   ├── CaLamDAO.java
│   │   ├── DangKyDAO.java
│   │   ├── RegistrationDAO.java
│   │   └── AdminReportDAO.java
│   ├── Database/                # Database Configuration
│   │   ├── BDConnection.java   # MySQL Connection
│   │   └── TestDatabase.java
│   ├── Request/                 # Request DTOs (Data Transfer Objects)
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── CreateCaLamRequest.java
│   │   ├── UpdateCaLamRequest.java
│   │   ├── DeleteCaLamRequest.java
│   │   ├── DangKyRequest.java
│   │   ├── HuyDangKyRequest.java
│   │   ├── GetDangKyRequest.java
│   │   ├── GetCaLamRequest.java
│   │   ├── GetAllCaLamAdminRequest.java
│   │   ├── DanhSachDangKyAdminRequest.java
│   │   ├── ThongKeAdminRequest.java
│   │   └── CapNhatTrangThaiRequest.java
│   ├── Response/                # Response DTOs
│   │   ├── LoginResponse.java (not found, likely used elsewhere)
│   │   ├── RegisterResponse.java
│   │   ├── CreateCaLamResponse.java
│   │   ├── DeleteCaLamResponse.java
│   │   ├── DangKyResponse.java
│   │   ├── HuyDangKyResponse.java
│   │   ├── GetCaLamResponse.java
│   │   ├── GetAllCaLamAdminResponse.java
│   │   ├── DanhSachDangKyAdminResponse.java
│   │   ├── CapNhatTrangThaiResponse.java
│   │   └── ThongKeAdminResponse.java
│   ├── HelloApplication.java    # Main Application Entry Point
│   ├── AdminApplication.java
│   ├── RegisterApplication.java
│   ├── Launcher.java
│   └── MainApp.java
├── src/main/resources/          # FXML Views and Resources
├── pom.xml                       # Maven Configuration
├── mvnw / mvnw.cmd             # Maven Wrapper
└── .gitignore                   # Git Configuration

```

---

## 🔑 Core Components

### 1. **Authentication System**
- **LoginController:** Handles user login
- **RegisterController:** Manages user registration
- **LoginRequest/LoginResponse:** Authentication data transfer
- **RegisterRequest/RegisterResponse:** Registration data transfer

### 2. **Shift Management (Ca Lam)**
- **CaLam Model:** Represents a work shift
- **CaLamDAO:** CRUD operations for shifts
- **ShiftManagementController:** UI for managing shifts
- **Request/Response Classes:** API communication for shifts

### 3. **Registration Management (Dang Ky)**
- **DangKy Model:** Represents shift registration/assignment
- **DangKyDAO:** Database operations for registrations
- **RegistrationDAO:** Alternative registration DAO
- **UserDashboardController:** UI for employees to register for shifts

### 4. **User Management**
- **User Model:** Represents system users
- **UserDAO:** CRUD operations for users
- **UserManagementController:** Admin interface for managing users

### 5. **Admin Features**
- **AdminDashboardController:** Admin dashboard UI
- **AdminReportDAO:** Generate reports and statistics
- **ThongKeAdminRequest/Response:** Reporting data transfer

### 6. **Database Layer**
- **BDConnection:** MySQL database connection management
- **TestDatabase:** Database testing utilities

---

## 👥 User Roles

### Admin (maVaitro = 1)
- Create, update, and delete shifts
- View all employee registrations
- Generate reports and statistics
- Manage user accounts
- Track registration statuses

### Employee (maVaitro = 2)
- View available shifts
- Register for shifts
- Cancel registrations
- View personal shift history
- Check registration status

---

## 📊 Database Schema Overview

### Users Table
- `maNguoidung` (ID)
- `email` (unique)
- `matKhau` (password)
- `hoTen` (full name)
- `maVaitro` (role ID)

### Shifts Table (Ca Lam)
- Shift details and timing
- Created/managed by admins

### Registrations Table (Dang Ky)
- Links users to shifts
- Tracks registration status (pending/accepted/rejected)

---

## 🔧 Technologies & Dependencies

### Framework
- **JavaFX 21.0.6** - UI Framework
- **ControlsFX 11.2.1** - Additional JavaFX controls
- **BootstrapFX 0.4.0** - CSS styling
- **TilesFX 21.0.9** - Dashboard tiles

### Database
- **MySQL Connector/J 8.2.0** - Database driver

### UI Libraries
- **FormsFX 11.6.0** - Form handling
- **ValidatorFX 0.6.1** - Form validation
- **Ikonli 12.3.1** - Icon library
- **FXGL 17.3** - Game framework (for advanced UI elements)

### Testing
- **JUnit 5.12.1** - Unit testing framework

---

## 🚀 Building & Running

### Prerequisites
- Java 21 or higher
- MySQL server
- Maven 3.6+

### Build
```bash
mvn clean install
```

### Run
```bash
mvn javafx:run
```

Or directly run:
```bash
mvn clean javafx:run
```

### Run Tests
```bash
mvn test
```

---

## 📝 Data Models

### User Class
Represents a user in the system with authentication and profile information.
```
- maNguoidung: int (User ID)
- email: String (unique identifier)
- matKhau: String (password)
- hoTen: String (full name)
- maVaitro: int (role ID: 1=admin, 2=employee)
- tenVaitro: String (role name: "admin", "employee")
```

### CaLam Class (Shift)
Represents a work shift.
```
- Shift timing and details
- Creation/modification information
```

### DangKy Class (Registration)
Represents a user's shift registration.
```
- User ID
- Shift ID
- Registration status
- Registration date
```

---

## 🔐 Security Notes
- Passwords are stored in the database (consider hashing for production)
- Role-based access control implemented
- Input validation through ValidatorFX

---

## 📋 Request/Response Pattern

The application uses a Request-Response pattern for data communication:

**Common Requests:**
- `LoginRequest` → authenticate user
- `RegisterRequest` → create new account
- `CreateCaLamRequest` → add shift
- `UpdateCaLamRequest` → modify shift
- `DeleteCaLamRequest` → remove shift
- `DangKyRequest` → register for shift
- `HuyDangKyRequest` → cancel registration
- `GetDangKyRequest` → retrieve registrations
- `GetCaLamRequest` → retrieve shifts
- `ThongKeAdminRequest` → generate reports

---

## 🖥️ User Interface

### Views
- **login-view.fxml** - Login screen
- Dashboard views for admin and employees
- Shift management views
- User management views
- Registration/enrollment views

### Main Entry Point
- `HelloApplication.java` - Starts with login screen at 450x650 resolution

---

## 🛠️ Development Notes

1. **Controllers** communicate with **DAOs** to access database
2. **Models** represent domain objects
3. **Request/Response** classes handle data transfer between UI and logic
4. **Database connectivity** through `BDConnection` singleton
5. **Role-based UI** displayed based on `User.isAdmin()` method

---

## 📞 Support & Maintenance

For issues or enhancements:
1. Check the relevant controller for UI logic
2. Verify DAO for database operations
3. Review Request/Response classes for data flow
4. Test database connectivity using `TestDatabase`

---

## 📄 License & Attribution
Project Version: 1.0-SNAPSHOT  
Build Configuration: Java 21, Maven 3.13.0

