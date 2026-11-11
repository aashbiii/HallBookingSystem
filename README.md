# Rajagiri College Hall Booking & Management System

> **Campus Venue Reservation & Administrative Approval Platform**  
> **Institution**: Rajagiri College of Social Sciences (Autonomous), Kalamassery  
> **Platform**: Native Android Application (Java) • **Min SDK**: 24 (Android 7.0) • **Target SDK**: 34 (Android 14)

---

## 📌 Project Overview

The **RCSS Hall Booking & Management System** is a dedicated enterprise mobile platform engineered for **Rajagiri College of Social Sciences (Autonomous), Kalamassery**. It automates and digitizes the entire campus venue requisition and administrative approval lifecycle across academic auditoriums, seminar halls, and student activity centers.

By replacing manual paper-based requisition letters, physical signature gathering, and manual registry books, the system guarantees **zero double-booking collisions**, transparent administrative communication, and instantaneous allocation tracking for all academic departments and student bodies.

---

## 🏛️ Campus Venues Configured

The application comes pre-configured with the 5 central halls of the RCSS Kalamassery campus:

| Venue Name | Location | Seating Capacity | Primary Amenities |
| :--- | :--- | :--- | :--- |
| **Golden Auerole** | Main Academic Block | 600 Seats | Central AC, Surround Sound, Stage Projector, VIP Green Room, Stage Lighting, Podium Mic |
| **Carmel Hall** | Carmel Block, First Floor | 250 Seats | Central AC, Stage Projector, Podium Mic, Acoustic Paneling, Discussion Stage |
| **Chavera Hall** | Academic Block, Ground Floor | 180 Seats | Central AC, Digital Projector, Wireless Audio, Executive Seating, Video Conferencing |
| **Assumption Hall** | Valley Campus Block | 300 Seats | Air Conditioned, Hi-Fi Sound System, Dual Projector, Elevated Dais, Green Rooms |
| **Esperansa Hall** | Student Activity Centre | 120 Seats | Smart Display, Flexible Seating, Acoustic Wall, Stage Audio, High-Speed Wi-Fi |

---

## 🎓 Academic Departments Integrated

Students and faculty coordinators can select from all **11 official RCSS academic schools and departments**:
1. Department of Computer Science (MCA / MSc)
2. Department of Social Work (MSW / BSW)
3. Department of Commerce & Professional Studies
4. Department of Management Studies (MBA)
5. Department of Psychology (MSc / BSc)
6. Department of Library & Information Science
7. Department of Personnel Management (MHRM)
8. Department of Languages & General Studies
9. RCSS IQAC & Academic Affairs
10. RCSS Student Council & Cultural Club
11. General / Administrative Cell

---

## 🔑 Key Features & Role-Based Workflows

### 1. Student / Department Coordinator Portal
* **Venue Discovery**: Browse campus venues with multi-image swipeable photo carousels (2–3 high-resolution photos per hall) and amenity specification chips.
* **Real-Time Live Search**: Instantly filter halls by venue name, location, or seating capacity as you type.
* **Structured Requisition Form**:
  * Event title input.
  * Department dropdown selector.
  * Interactive Date Picker (restricts past-date selection).
  * Time slot chips (`09:00 AM - 01:00 PM`, `02:00 PM - 06:00 PM`, `06:00 PM - 10:00 PM`).
  * Expected attendee count validation against venue capacity.
  * Automatic collision checking against existing approved or pending bookings.
* **Live "My Bookings" Tracker**: Real-time status badges (`PENDING`, `APPROVED`, `REJECTED`).
* **Transparent Rejection Reasons**: If a booking is rejected by administration, the specific reason/remarks entered by the manager are prominently displayed to the student on their booking card.
* **In-App Digital Allocation Slip**: View the official allocation slip with institutional verification reference (`RCSS-2026-XXXX`), applicant info, event details, and allocation timestamp.
* **Profile & Display Picture (DP)**: Upload/change profile picture from device gallery with automatic circular clipping, update contact phone number, department affiliation, and password.
* **Notch & Status Bar Clearance**: Generous top gap (54dp) in the sidebar drawer to prevent clipping against device cameras, status bars, and notches.

### 2. Administrator / Manager Portal
* **Executive KPI Dashboard**: At-a-glance counters for *Pending Requests*, *Total Halls*, and *Total Bookings*.
* **Direct Quick-Access Cards**: Prominent exterior cards for **"Manage Halls"** and **"All Bookings"** for direct one-tap access.
* **One-Tap Review Workflow**:
  * **Approve**: Instantly approves the booking, officially reserves the slot, and generates a unique institutional receipt number.
  * **Reject with Feedback**: Prompts an administrative dialog to enter an official rejection reason, immediately visible to the applicant.
* **Venue & Photo Management (`EditHallActivity`)**:
  * Add brand-new halls or edit existing hall specifications (name, capacity, location, description, amenities).
  * **Multi-Image Photo Picker**: Select multiple photos from device storage with interactive photo grid preview and photo deletion.
* **Master Audit Ledger (`ManagerAllBookingsActivity`)**:
  * Complete audit trail across all historical university venue allocations.
  * Live search by event name, student name, department, or hall.
  * Uniform pill-shaped filter chips (`ALL`, `PENDING`, `APPROVED`, `REJECTED`) maintaining cohesive geometry across selection states.
* **Admin Profile Management**: Update administrative credentials, contact details, and display picture.

---

## 🛠️ Technology Stack & Architecture

* **Language & SDK**: Java 8 • Android SDK (compileSdk 34, minSdk 24, targetSdk 34)
* **Design System**: Material Design 3 (Material3 DayNight with Light/Dark mode compatibility)
* **Architecture Pattern**: Model-View-Controller (MVC) with Data Access Object (DAO) pattern
* **Database & Persistence**:
  * Embedded SQLite database (`hall_booking.db`) via `DBHelper.java`
  * Relational schema across 3 core tables: `users`, `halls`, `bookings`
  * Self-healing migration (`ensureUserProfileColumns`, `ensureDepartmentColumn`) with `DATABASE_VERSION = 9`
* **Local Storage Sandbox**:
  * Custom user avatars and hall photos are safely downsampled and copied to internal app storage (`profile_avatars/` and `hall_images/`)
  * Guarantees 100% permanent offline access immune to Android URI permission expiration across app restarts
* **Session Management**: Lightweight session store backed by `SharedPreferences` (`SessionManager.java`)

---

## 🔐 Demo Credentials

The database is pre-seeded with two default accounts:

| Role | Email | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Administrator / Manager** | `admin@rajagiri.edu` | `Admin@123` | Full administrative control, approval/rejection, hall management, master ledger |
| **Student / Applicant** | `student@rajagiri.edu` | `Student@123` | Venue browsing, booking requisition, request tracking, profile management |

> You can also tap **"New here? Create an account"** on the login screen to register a new student account.

---

## 🚀 How to Open & Run the Project

1. Open **Android Studio** (Hedgehog, Iguana, Jellyfish, Koala or newer).
2. Click **File** → **Open...** and select the `HallBookingSystem` root directory.
3. Allow Gradle to sync dependencies automatically (requires internet connection on initial sync).
4. Connect an Android device with USB Debugging enabled or start an Android Virtual Device (AVD) emulator (**API 24 to 34**).
5. Click **Run** (`Shift + F10`) to build and launch the application.

---

## 📂 Project Structure

```
HallBookingSystem/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/mca/hallbooking/
│   │       │   ├── HallBookingApp.java             # Application initialization
│   │       │   ├── SplashActivity.java             # Animated splash & session routing
│   │       │   ├── LoginActivity.java              # Institutional authentication
│   │       │   ├── RegisterActivity.java           # Student registration
│   │       │   ├── UserDashboardActivity.java      # Student hall discovery & drawer
│   │       │   ├── HallDetailActivity.java         # Hall specifications & booking form
│   │       │   ├── MyBookingsActivity.java         # Student request history & status
│   │       │   ├── ReceiptActivity.java            # In-app digital allocation slip
│   │       │   ├── ManagerDashboardActivity.java   # Admin KPI counters & pending reviews
│   │       │   ├── ManagerHallsActivity.java       # Venue directory for admin
│   │       │   ├── EditHallActivity.java           # Hall editor & multi-photo upload
│   │       │   ├── ManagerAllBookingsActivity.java # Master audit ledger & filter chips
│   │       │   ├── ProfileActivity.java            # Profile & DP management (Student & Admin)
│   │       │   ├── AboutActivity.java              # RCSS institutional information
│   │       │   ├── adapter/
│   │       │   │   ├── HallAdapter.java            # Hall feed adapter
│   │       │   │   ├── BookingAdapter.java         # Student bookings list & reject reason binder
│   │       │   │   ├── RequestAdapter.java         # Admin pending requests adapter
│   │       │   │   ├── ImageCarouselAdapter.java   # Multi-photo swipeable carousel adapter
│   │       │   │   └── EditImageAdapter.java       # Hall photo management grid adapter
│   │       │   ├── dao/
│   │       │   │   ├── UserDAO.java                # User credentials & profile queries
│   │       │   │   ├── HallDAO.java                # Venue catalog queries
│   │       │   │   └── BookingDAO.java             # Requisition, approval & audit queries
│   │       │   ├── db/
│   │       │   │   └── DBHelper.java               # SQLite schema, migrations & seed data
│   │       │   ├── model/
│   │       │   │   ├── User.java                   # User entity with role & DP
│   │       │   │   ├── Hall.java                   # Venue entity with photo URIs
│   │       │   │   └── Booking.java                # Booking entity with department & remarks
│   │       │   └── util/
│   │       │       ├── Constants.java              # Departments, time slots, shared keys
│   │       │       ├── DateTimeUtils.java          # Date formatting & calendar utilities
│   │       │       ├── ImageUtil.java              # Safe avatar downsampling & file storage
│   │       │       └── SessionManager.java         # User session & profile persistence
│   │       └── res/
│   │           ├── drawable/                       # Vector icons, chips, and hall assets
│   │           ├── layout/                         # Modern Material 3 activity & item layouts
│   │           ├── menu/                           # Sidebar navigation drawer menus
│   │           └── values/                         # Colors, dimens, strings & themes (DayNight)
│   └── build.gradle
├── gradle/
├── build.gradle
├── settings.gradle
├── RCSS_Hall_Booking_System_Presentation.pptx     # 5-Slide 16:9 widescreen presentation deck
└── README.md
```

---

## 📄 License & Academic Attribution
* **Institution**: Rajagiri College of Social Sciences (Autonomous), Kalamassery, Kerala, India
* **Department**: Department of Computer Science (MCA)
* **Purpose**: Academic Mini Project / Enterprise Application Development
