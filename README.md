# Bulawayo Student Accommodation Center (BSAC) Portal

The BSAC Portal is a comprehensive, responsive web application built to streamline the room booking, storage management, and repair logging process for students at the Bulawayo Student Accommodation Center. By transitioning away from manual paperwork, the portal mitigates issues like double-booking and poor room condition allocations at the start of a semester.

## 🚀 Features

### 1. Secure Authentication
- Students log in using their **Student Number** as the username and their **National ID** as the password.
- Secure session management and route protection provided by Spring Security.

### 2. Smart Room Booking
- **Gender-Specific Filtering**: Rooms are segregated by gender to enforce accommodation policies automatically.
  - **Block 1 (Girls Only)**: Rooms 1 to 299
  - **Block 2 (Boys Only)**: Rooms 300 to 401
  - **Block 3 (Boys Only)**: Rooms 402 to 600
- **Real-Time Availability**: Students can only view and secure rooms that are strictly unoccupied.
- **Transparent Billing**: The interface explicitly shows the $400/semester rent fee required to secure the chosen room.

### 3. Storage Unit Management
- Students can log the items they plan to leave in the storage unit during the break (e.g., mattress, fridge).
- The system assesses a flat storage fee of **$20**.

### 4. Complaints & Maintenance
- If a room requires renovation or property replacement, students can file a complaint or maintenance request directly to the admin. 
- Issues can be bound to specific rooms or logged as general property issues.

### 5. UI/UX Design
- Completely responsive layout allowing students to access the portal gracefully on mobile phones, tablets, or desktops.
- Customized color theme built around BSAC's branding: Green (primary), White (background), and Black (text).

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3
- **Data Persistence**: Spring Data JPA, H2 In-Memory Database (for easy local setup)
- **Frontend Engine**: Thymeleaf templating
- **Styling**: Custom CSS3 utilizing CSS Variables, Grid, and Flexbox for modern responsive design.
- **Security**: Spring Security

## 💻 Getting Started

### Prerequisites
- JDK 17 or higher
- Maven (or run directly from a modern IDE like IntelliJ IDEA or Eclipse)

### Running the Application

1. Clone or download the repository to your local machine.
2. Open the project in your IDE.
3. Run the `BsacApplication.java` main class.
   *(Alternatively, run `mvn spring-boot:run` from the terminal).*
4. Open your web browser and navigate to:
   ```
   http://localhost:8080
   ```

### Default Test Credentials

The database is automatically seeded upon startup with the following test accounts:

**Female Student:**
- **Student Number**: N001
- **National ID (Password)**: 12345

**Male Student:**
- **Student Number**: N002
- **National ID (Password)**: 54321

*Logging in with the Female student will only display rooms from Block 1. Logging in with the Male student will display rooms from Block 2 and 3.*

## 📖 Architecture & Design

The codebase follows the standard MVC (Model-View-Controller) structure typical of Spring Boot applications:
- `/controller`: Houses the `WebController` handling HTTP GET/POST requests and rendering Thymeleaf views.
- `/entity`: Contains the core domain models (`Student`, `Room`, `Booking`, `Complaint`, `StorageItem`).
- `/repository`: Spring Data JPA interfaces for database CRUD operations.
- `/security`: Authentication services and filter chain definitions.
- `/config`: Houses the `DataInitializer` responsible for seeding initial rooms and mock students.
- `/resources/templates`: HTML fragments and pages using Thymeleaf.
- `/resources/static/css`: Custom stylesheet implementing the Green/White/Black aesthetic.
