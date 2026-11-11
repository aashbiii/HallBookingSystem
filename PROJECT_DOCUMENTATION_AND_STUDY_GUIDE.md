# Rajagiri College Hall Booking & Management System
## Master Codebase Study Guide, Architecture Manual & Viva Presentation Handbook

> **Institution**: Rajagiri College of Social Sciences (Autonomous), Kalamassery, Kochi, Kerala  
> **Department**: Department of Computer Science (MCA)  
> **Course / Purpose**: Academic Mini Project / Enterprise Mobile Application Development  
> **Target Platform**: Android (Java 8, SDK 24–34, Material Design 3, SQLite v9)  
> **Document Purpose**: Complete technical study manual, line-by-line file explanations, architectural analysis, and viva defense guide.

---

# Table of Contents
1. [Executive Summary & Real-World Problem Solved](#1-executive-summary--real-world-problem-solved)
2. [Institutional Configuration: Halls & Departments](#2-institutional-configuration-halls--departments)
3. [System Architecture & Software Engineering Patterns](#3-system-architecture--software-engineering-patterns)
4. [Relational Database Architecture (SQLite Schema v9)](#4-relational-database-architecture-sqlite-schema-v9)
5. [In-Depth Explanation of All 30 Java Files](#5-in-depth-explanation-of-all-30-java-files)
   - [5.1 Core Application & Entry Layer](#51-core-application--entry-layer)
   - [5.2 Authentication & Onboarding Layer](#52-authentication--onboarding-layer)
   - [5.3 Student Portal Activities](#53-student-portal-activities)
   - [5.4 Administrative Management Activities](#54-administrative-management-activities)
   - [5.5 Shared Activities](#55-shared-activities)
   - [5.6 RecyclerView Adapters & ViewHolders](#56-recyclerview-adapters--viewholders)
   - [5.7 Data Access Objects (DAOs)](#57-data-access-objects-daos)
   - [5.8 Database Engine & Migrations](#58-database-engine--migrations)
   - [5.9 Domain Data Models](#59-domain-data-models)
   - [5.10 Utility Classes & Storage Sandboxing](#510-utility-classes--storage-sandboxing)
6. [In-Depth Explanation of All XML Layouts & Resource Files](#6-in-depth-explanation-of-all-xml-layouts--resource-files)
   - [6.1 Activity Layouts](#61-activity-layouts)
   - [6.2 Component & RecyclerView Item Layouts](#62-component--recyclerview-item-layouts)
   - [6.3 Navigation Drawer Headers](#63-navigation-drawer-headers)
   - [6.4 Menus](#64-menus)
   - [6.5 Design Tokens: Colors, Dimensions & Themes](#65-design-tokens-colors-dimensions--themes)
7. [In-Depth Explanation of Configuration & Build Files](#7-in-depth-explanation-of-configuration--build-files)
8. [End-to-End Execution Flowcharts & Algorithms](#8-end-to-end-execution-flowcharts--algorithms)
   - [8.1 Booking Requisition & Collision Detection Algorithm](#81-booking-requisition--collision-detection-algorithm)
   - [8.2 Administrative Approval & Digital Slip Generation](#82-administrative-approval--digital-slip-generation)
   - [8.3 Administrative Rejection with Feedback Justification](#83-administrative-rejection-with-feedback-justification)
   - [8.4 Image Ingestion & Offline Sandboxing Pipeline](#84-image-ingestion--offline-sandboxing-pipeline)
9. [Presentation & Slide-by-Slide Delivery Script](#9-presentation--slide-by-slide-delivery-script)
10. [Comprehensive Viva Voce Q&A (Top 25 Questions)](#10-comprehensive-viva-voce-qa-top-25-questions)

---

# 1. Executive Summary & Real-World Problem Solved

### The Traditional Problem at Academic Campuses
In large autonomous academic institutions like **Rajagiri College of Social Sciences (Autonomous)**, scheduling campus halls, seminar auditoriums, and smart rooms for departmental fests, guest symposiums, viva sessions, placement drives, and club meetings has traditionally suffered from:
1. **Double-Booking Clashes**: Two academic departments unknowingly reserving the same auditorium for the same date and time slot.
2. **Paper-Based Bureaucracy**: Physical requisition letters signed by faculty coordinators, HODs, and campus managers, causing approval delays of several days.
3. **Lack of Transparency**: Students and faculty left in the dark about whether a hall was approved or rejected, and why it was rejected.
4. **Physical Receipt Hassles**: Reliance on physical rubber-stamped slips or cumbersome file downloads that get lost.
5. **No Centralized Audit Trail**: Administrators having no single ledger to view booking statistics, hall occupancy rates, or past event records.

### The Solution: RCSS Hall Booking & Management System
The **RCSS Hall Booking & Management System** is a native Android application built using Java and Android SDK. It delivers a modern, role-based, paperless platform:
- **Zero Collision Guarantee**: An automated database validation engine ensures no venue can be double-booked for any overlapping slot if already pending or approved.
- **Role-Based Portals**:
  - **Student / Department Coordinator Portal**: Discover venues with multi-photo swipeable carousels, view amenities, live-check time slot availability, submit requisitions, track approval status in real-time, view administrative rejection reasons, and view verified digital allocation slips.
  - **Administrator / Campus Manager Portal**: Executive dashboard with KPI summary counters, quick-action cards, one-tap approval with automatic receipt reference generation, rejection with mandatory feedback remarks, comprehensive venue management (add/edit halls with custom photo uploads), and a master audit ledger with live search and status filter chips.
- **100% Offline Resilience**: Employs an embedded SQLite relational database (Schema v9) and an internal private storage sandbox (`profile_avatars/`, `hall_images/`) that makes user avatars and hall photos permanent and immune to Android URI permission expiration.

---

# 2. Institutional Configuration: Halls & Departments

### Campus Venues Configured
The database comes pre-seeded with the five central venues of the RCSS Kalamassery campus:

| Venue Name | Campus Location | Capacity | Amenities & Specifications |
| :--- | :--- | :--- | :--- |
| **Golden Auerole** | Main Academic Block | 600 Seats | Central AC, Surround Sound, Stage Projector, VIP Green Room, Stage Lighting, Podium Mic |
| **Carmel Hall** | Carmel Block, First Floor | 250 Seats | Central AC, HD Laser Projection, Acoustic Sound System, Campus WiFi, Smart Podium, Lavalier Mics |
| **Chavera Hall** | Chavara Academic Complex, 2nd Floor | 200 Seats | AC, Dual Laser Projectors, Surround Audio, Smart Podium, High-speed WiFi |
| **Assumption Hall** | Management Block, Ground Floor | 120 Seats | AC, Smart Video Conferencing, Interactive Display, Executive Seating, WiFi |
| **Esperansa Hall** | Student Amenities Block, 3rd Floor | 150 Seats | AC, Interactive Smart Screen, Sound System, Flexible Seating, High-speed WiFi |

### Integrated Academic Departments
All 11 official academic schools and administrative departments are pre-configured in `Constants.java`:
1. Department of Computer Science (MCA / MSc)
2. Department of Social Work (MSW / BSW)
3. Department of Commerce & Professional Studies
4. Department of Management Studies (MBA)
5. Department of Psychology (MSc / BSc)
6. Department of Library & Information Science
7. Department of Personnel Management (MHRM)
8. College Cultural Committee & Fine Arts
9. IQAC & College Administration
10. Placement & Corporate Relations Cell
11. Student Council & University Club

---

# 3. System Architecture & Software Engineering Patterns

### Architectural Paradigm: MVC with DAO Abstraction
The application adheres strictly to the **Model-View-Controller (MVC)** architectural pattern combined with the **Data Access Object (DAO)** enterprise pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                      VIEW LAYER (XML)                       │
│  Activities Layouts (Material 3), Recycler Items, Menus     │
└──────────────────────────────▲──────────────────────────────┘
                               │ User Interactions / View Binding
┌──────────────────────────────▼──────────────────────────────┐
│                  CONTROLLER LAYER (Java)                    │
│  Activities (Splash, Login, UserDashboard, ManagerDashboard) │
│  Adapters (HallAdapter, BookingAdapter, RequestAdapter)      │
└──────────────────────────────▲──────────────────────────────┘
                               │ Invokes CRUD Operations
┌──────────────────────────────▼──────────────────────────────┐
│                     DATA ACCESS OBJECTS                     │
│         UserDAO    •    HallDAO    •    BookingDAO          │
└──────────────────────────────▲──────────────────────────────┘
                               │ SQL Execution & Cursor Mapping
┌──────────────────────────────▼──────────────────────────────┐
│                    PERSISTENCE / MODEL                      │
│   DBHelper (SQLite v9)   •   Models (User, Hall, Booking)   │
│   SessionManager (SharedPreferences) • Internal File Storage │
└─────────────────────────────────────────────────────────────┘
```

### Core Design Patterns Implemented
1. **Data Access Object (DAO) Pattern** (`UserDAO`, `HallDAO`, `BookingDAO`): Separates low-level SQLite database operations from high-level UI controllers. Activities never execute raw SQL queries; they call domain methods like `bookingDAO.isSlotTaken()`.
2. **Singleton Pattern** (`DBHelper.getInstance(context)`): Ensures only one database connection pool exists throughout the application lifecycle, avoiding database lock errors.
3. **Adapter & ViewHolder Pattern** (`HallAdapter`, `BookingAdapter`, `RequestAdapter`, `ImageCarouselAdapter`, `EditImageAdapter`): Recycles UI view instances in `RecyclerView` and `ViewPager2` to ensure 60 FPS scrolling and low memory usage.
4. **Session Store Pattern** (`SessionManager`): Encapsulates Android's `SharedPreferences` to persist user login state, token keys, user name, role, email, and display picture path across application restarts.
5. **Private Storage Sandboxing Pattern** (`ImageUtil`): Downsamples incoming camera/gallery Bitmaps to target dimensions (e.g., 600x600) and saves them into the app's private files directory (`context.getFilesDir()`), preventing memory crashes (`OutOfMemoryError`) and eliminating URI permission revocation bugs.

---

# 4. Relational Database Architecture (SQLite Schema v9)

The application uses an embedded SQLite database named `hall_booking.db` managed by `DBHelper.java`. Foreign key constraints are explicitly enforced via `db.setForeignKeyConstraintsEnabled(true)`.

### Entity-Relationship Diagram

```
       ┌────────────────────────┐
       │         USERS          │
       ├────────────────────────┤
       │ id (PK, AUTOINCREMENT) │
       │ name (TEXT NOT NULL)   │
       │ email (TEXT UNIQUE)    │
       │ phone (TEXT)           │
       │ password (TEXT NOT NULL│
       │ role (TEXT: USER/MGR)  │
       │ profile_image (TEXT)   │
       │ department (TEXT)      │
       └───────────┬────────────┘
                   │ 1
                   │
                   │ has many
                   │
                   │ M
       ┌───────────▼────────────┐                   ┌────────────────────────┐
       │        BOOKINGS        │                   │         HALLS          │
       ├────────────────────────┤                   ├────────────────────────┤
       │ id (PK, AUTOINCREMENT) │         M         │ id (PK, AUTOINCREMENT) │
       │ user_id (FK -> users)  ├───────────────────┤ name (TEXT NOT NULL)   │
       │ hall_id (FK -> halls)  │  belongs to hall  │ location (TEXT)        │
       │ event_name (TEXT)      │                   │ capacity (INTEGER)     │
       │ department (TEXT)      │                   │ amenities (TEXT)       │
       │ booking_date (TEXT)    │                   │ price (REAL)           │
       │ slot (TEXT)            │                   │ description (TEXT)     │
       │ attendees (INTEGER)    │                   │ image_uris (TEXT)      │
       │ status (PENDING/APP/REJ│                   └────────────────────────┘
       │ remarks (TEXT)         │
       │ receipt_no (TEXT)      │
       │ created_at (TEXT)      │
       │ updated_at (TEXT)      │
       └────────────────────────┘
```

### Self-Healing Migration Architecture
The database version is currently **9**. To prevent crashes during app updates and ensure that data is never lost:
- `onOpen(SQLiteDatabase db)` inspects table schemas dynamically using `PRAGMA table_info(...)`.
- If newly added columns (`profile_image`, `department`, `remarks`, `receipt_no`) are absent in an existing database, it runs `ALTER TABLE ... ADD COLUMN` non-destructively.
- It validates the existence of the default administrator (`admin@rajagiri.edu`) and default student (`student@rajagiri.edu`), inserting them automatically if missing.

---

# 5. In-Depth Explanation of All 30 Java Files

---

### 5.1 Core Application & Entry Layer

#### 1. `HallBookingApp.java`
- **Location**: `com.mca.hallbooking.HallBookingApp`
- **Extends**: `android.app.Application`
- **Role**: Base application class initialized by Android runtime before any activity launches.
- **Key Methods**:
  - `onCreate()`: Initializes global resources and triggers `DBHelper.getInstance(this).getWritableDatabase()` on startup to run schema checks, migrations, and database seeding before the UI opens.
- **Interaction**: Registered in `AndroidManifest.xml` under `<application android:name=".HallBookingApp" ...>`.

#### 2. `SplashActivity.java`
- **Location**: `com.mca.hallbooking.SplashActivity`
- **Extends**: `AppCompatActivity`
- **Role**: First visual screen shown to the user upon opening the app. Displays the animated RCSS crest, app title, and tag line, then routes the user based on session state.
- **Key Variables**:
  - `SessionManager sessionManager`: Checks whether a user is currently logged in.
- **Key Methods**:
  - `onCreate()`: Sets the splash layout, starts fade-in and scale animations on the logo, and triggers a 1200ms `Handler.postDelayed(...)`.
  - Session Routing:
    - If `sessionManager.isLoggedIn()` is `false` → Launches `LoginActivity.java`.
    - If `isLoggedIn()` is `true` and role is `MANAGER` → Launches `ManagerDashboardActivity.java`.
    - If `isLoggedIn()` is `true` and role is `USER` → Launches `UserDashboardActivity.java`.
  - Calls `finish()` so the splash screen cannot be returned to via the device back button.

---

### 5.2 Authentication & Onboarding Layer

#### 3. `LoginActivity.java`
- **Location**: `com.mca.hallbooking.LoginActivity`
- **Role**: Handles user authentication for both Students and Administrators.
- **Key UI Elements**: `TextInputEditText` for email and password, Material login button, link to registration.
- **Key Logic**:
  - Validates non-empty email and password.
  - Calls `userDAO.login(email, password)`.
  - If valid: Saves user ID, name, email, role, department, and profile image into `SessionManager`.
  - Routes to `ManagerDashboardActivity` (if `ROLE_MANAGER`) or `UserDashboardActivity` (if `ROLE_USER`).
  - If invalid: Displays an error Toast: *"Invalid email or password"*.

#### 4. `RegisterActivity.java`
- **Location**: `com.mca.hallbooking.RegisterActivity`
- **Role**: Handles registration for new student accounts.
- **Key Validations**:
  - Full Name: Cannot be empty.
  - Email: Validated with `Patterns.EMAIL_ADDRESS.matcher(email).matches()`.
  - Phone: Validated for 10-digit format.
  - Password: Enforces minimum length of 6 characters.
  - Email Uniqueness: Calls `userDAO.emailExists(email)` before inserting.
- **Result**: Inserts student into the `users` table with role `USER`, shows success message, and redirects to `LoginActivity`.

---

### 5.3 Student Portal Activities

#### 5. `UserDashboardActivity.java`
- **Location**: `com.mca.hallbooking.UserDashboardActivity`
- **Role**: Main landing hub for students. Displays campus halls, live search, quick-action shortcuts, and navigation drawer.
- **Key Features & Methods**:
  - `setupToolbarAndDrawer()`: Wires `ActionBarDrawerToggle` with the `DrawerLayout`.
  - **Status Bar & Notch Clearance**: Configures top spacing so the drawer header is not cut off by front-camera notches or status bars.
  - `updateNavHeader()`: Dynamically loads the student's name, email, department, and circular avatar using `ImageUtil.loadAvatar()`.
  - `loadHalls()`: Queries `hallDAO.getAllHalls()` and sets up `HallAdapter` on the `RecyclerView`.
  - **Live Search (`etSearchHalls`)**: Uses a `TextWatcher` to filter halls by name, location, or seating capacity in real-time as the user types.
  - Navigation Handling: Drawer links to `MyBookingsActivity`, `ProfileActivity`, `AboutActivity`, and `Logout` (with confirmation dialog).

#### 6. `HallDetailActivity.java`
- **Location**: `com.mca.hallbooking.HallDetailActivity`
- **Role**: Displays detailed specifications for a selected hall and presents the booking requisition form.
- **Key Components**:
  - `setupImageCarousel()`: Horizontal multi-photo carousel using `RecyclerView` and `PagerSnapHelper` bound to `ImageCarouselAdapter`.
  - `bindHallInfo()`: Displays capacity badge, location, full description, and dynamically creates amenity chips.
  - `buildSlotOptions()`: Dynamically generates `RadioButton` items for morning, afternoon, and evening slots.
  - `btnPickDate`: Opens `DatePickerDialog` configured with `setMinDate(System.currentTimeMillis())` to prevent booking past dates.
  - `btnCheckAvailability`: Calls `bookingDAO.isSlotTaken(hallId, date, slot)`. If available, displays a green success message and reveals the booking form. If booked, displays a red warning.
  - `btnBookNow`: Validates event name, department dropdown, and attendees count (must be > 0 and <= hall capacity). Re-verifies slot availability, creates the booking with status `PENDING`, and navigates to `MyBookingsActivity`.

#### 7. `MyBookingsActivity.java`
- **Location**: `com.mca.hallbooking.MyBookingsActivity`
- **Role**: Dedicated status tracking portal for the logged-in student.
- **Key Logic**:
  - Queries `bookingDAO.getBookingsByUser(userId)`.
  - If no bookings exist: Displays empty state illustration and message (*"You haven't requested any halls yet"*).
  - If bookings exist: Populates `RecyclerView` via `BookingAdapter`.
  - Listens for `onReceiptClick`: When an approved booking's slip button is tapped, launches `ReceiptActivity` passing the booking ID.

#### 8. `ReceiptActivity.java`
- **Location**: `com.mca.hallbooking.ReceiptActivity`
- **Role**: Renders the in-app digital allocation slip for approved bookings.
- **Key Logic**:
  - Receives `booking_id` from Intent extras and retrieves full joined details via `bookingDAO.getBookingById(id)`.
  - Binds official approval reference: `booking.getReceiptNo()` (e.g., `RCSS-2026-0042`).
  - Populates structured key-value rows using helper `setRow(rowId, label, value)`:
    - Applicant Name, Email, Department, Event Name, Hall Name, Booking Date, Time Slot, Expected Attendees, Approval Timestamp, and Approving Authority (*"RCSS Hall Administration (Autonomous)"*).
  - Clean toolbar with back navigation.

---

### 5.4 Administrative Management Activities

#### 9. `ManagerDashboardActivity.java`
- **Location**: `com.mca.hallbooking.ManagerDashboardActivity`
- **Role**: Executive command center for the campus hall administrator.
- **Key Features & Methods**:
  - **KPI Summary Cards**: Queries `bookingDAO.getBookingCounts()` to populate three metric badges:
    1. *Pending Requests* (highlighted in amber)
    2. *Total Configured Halls*
    3. *Total Lifetime Bookings*
  - **Quick Action Exterior Cards**: Prominent clickable cards on the dashboard for **"Manage Halls"** and **"All Bookings"**.
  - **Review Queue**: `RecyclerView` populated by `RequestAdapter` showing only `PENDING` bookings awaiting administrative action.
  - **One-Tap Approval**:
    - Generates unique reference code via `bookingDAO.generateReceiptNumber(bookingId)`.
    - Updates status to `APPROVED`.
    - Refreshes KPI counters and review queue with feedback Toast.
  - **Rejection with Mandatory Justification**:
    - Displays an `AlertDialog` containing an `EditText` prompting the administrator for rejection remarks.
    - Updates status to `REJECTED` and saves remarks in the database.
    - Immediately visible to the student in their booking card.

#### 10. `ManagerHallsActivity.java`
- **Location**: `com.mca.hallbooking.ManagerHallsActivity`
- **Role**: Administrative venue catalog where halls can be inspected, edited, or added.
- **Key Methods**:
  - `loadHalls()`: Queries `hallDAO.getAllHalls()` and binds `HallAdapter` in admin mode.
  - Floating Action Button (FAB): Launches `EditHallActivity` with `hall_id = -1` to add a new hall.
  - Item Click: Launches `EditHallActivity` with existing `hall_id` to edit venue details.

#### 11. `EditHallActivity.java`
- **Location**: `com.mca.hallbooking.EditHallActivity`
- **Role**: Comprehensive hall editor supporting multi-image upload.
- **Key Logic & Methods**:
  - Detects mode: New Hall (`hallId == -1`) or Edit Hall (`hallId != -1`).
  - Multi-Photo Management:
    - Uses `ActivityResultContracts.GetContent()` via `imagePickerLauncher.launch("image/*")`.
    - Calls `copyUriToInternalStorage()` to safely store picked photos into `files/hall_images/`.
    - Binds photo list to `EditImageAdapter` with instant deletion capability.
  - `saveChanges()`: Validates name, capacity, location, and serializes photo list into comma-delimited string `image_uris`. Calls `hallDAO.insertHall()` or `hallDAO.updateHall()`.

#### 12. `ManagerAllBookingsActivity.java`
- **Location**: `com.mca.hallbooking.ManagerAllBookingsActivity`
- **Role**: Complete institutional audit ledger of all booking records across the college.
- **Key Features**:
  - **Uniform Filter Chips**: Cohesive pill-shaped filters for `ALL`, `PENDING`, `APPROVED`, and `REJECTED`. Selecting a chip dynamically highlights it while maintaining identical pill geometry.
  - **Live Search**: `TextWatcher` on `etSearchBookings` filters bookings by event name, student name, department, or hall name.
  - Dynamic KPI Header: Displays count of records matching the current filter (e.g., *"Showing 14 records"*).

---

### 5.5 Shared Activities

#### 13. `ProfileActivity.java`
- **Location**: `com.mca.hallbooking.ProfileActivity`
- **Role**: Profile management screen accessible to both Students and Administrators.
- **Key Features**:
  - **Display Picture (DP) Upload**: Uses `ActivityResultContracts.GetContent()` to let the user select a photo from gallery.
  - Calls `ImageUtil.saveProfileImage()`: Downsamples image to 600x600 and writes to `files/profile_avatars/avatar_{userId}_{timestamp}.jpg`.
  - Editable Fields: Full Name, Contact Phone, Academic Department (dropdown selection).
  - Password Update Section: Requires current password verification, enforces 6-character minimum for new password, and validates confirmation.
  - Updates SQLite `users` table via `userDAO.updateProfile()` and `userDAO.updatePassword()`, and synchronizes `SessionManager`.

#### 14. `AboutActivity.java`
- **Location**: `com.mca.hallbooking.AboutActivity`
- **Role**: Institutional information screen displaying college details, vision, accreditation, and app development credits.
- **Key Features**: Material card layout showing RCSS Autonomous accreditation, development by Department of Computer Science (MCA), and support contact info.

---

### 5.6 RecyclerView Adapters & ViewHolders

#### 15. `HallAdapter.java`
- **Location**: `com.mca.hallbooking.adapter.HallAdapter`
- **Role**: Binds list of `Hall` objects to `item_hall.xml` cards for both student discovery and admin management.
- **Key Logic**:
  - Parses comma-delimited `image_uris`. Extracts primary image and loads it into the hall thumbnail.
  - Formats seating capacity chip (e.g., `"600 Seats"`).
  - Sets location and truncated description.
  - Dispatches click event to listener to open detail or edit view.

#### 16. `BookingAdapter.java`
- **Location**: `com.mca.hallbooking.adapter.BookingAdapter`
- **Role**: Binds student booking history to `item_booking.xml`.
- **Key UI Logic**:
  - **Status Badge Styling**: Sets background drawable and color dynamically:
    - `APPROVED` → Green badge (`bg_status_approved`, `@color/status_approved`).
    - `PENDING` → Amber badge (`bg_status_pending`, `@color/status_pending`).
    - `REJECTED` → Red badge (`bg_status_rejected`, `@color/status_rejected`).
  - **Rejection Reason Binder**: If status is `REJECTED`, sets `layoutRejectReason` to `VISIBLE` and populates `tvRejectReason` with `booking.getRemarks()`. Otherwise hides the view.
  - **Approval Slip Access**: If status is `APPROVED`, shows `tvReceiptNo` and `btnViewReceipt`. Clicking the button invokes `listener.onReceiptClick(booking)`.

#### 17. `RequestAdapter.java`
- **Location**: `com.mca.hallbooking.adapter.RequestAdapter`
- **Role**: Binds booking requests to `item_request.xml` for administrative review on the manager dashboard and audit ledger.
- **Key Logic**:
  - Displays event name, student name, department, date, slot, attendees, and hall.
  - In review mode (Manager Dashboard): Renders **"Approve"** and **"Reject"** action buttons.
  - In ledger mode (All Bookings): Hides action buttons and displays status badge and remarks.

#### 18. `ImageCarouselAdapter.java`
- **Location**: `com.mca.hallbooking.adapter.ImageCarouselAdapter`
- **Role**: Powers the horizontal swipeable photo carousel in `HallDetailActivity`.
- **Key Logic**:
  - Takes a `List<String>` of image URIs (supporting `res:img_name`, `file://`, and `content://`).
  - Binds each image into `item_carousel_image.xml` with center-crop scaling.

#### 19. `EditImageAdapter.java`
- **Location**: `com.mca.hallbooking.adapter.EditImageAdapter`
- **Role**: Powers the photo preview grid in `EditHallActivity`.
- **Key Logic**:
  - Renders thumbnails of all photos currently attached to a hall.
  - Displays a delete icon button on each photo thumbnail allowing administrators to remove individual photos.

---

### 5.7 Data Access Objects (DAOs)

#### 20. `BookingDAO.java`
- **Location**: `com.mca.hallbooking.dao.BookingDAO`
- **Role**: Central database query engine for all booking operations.
- **Key Methods**:
  - `isSlotTaken(long hallId, String date, String slot)`:
    ```sql
    SELECT id FROM bookings 
    WHERE hall_id=? AND booking_date=? AND slot=? AND status IN ('PENDING', 'APPROVED')
    ```
    Returns `true` if the slot is occupied, preventing double-bookings.
  - `createBooking(userId, hallId, eventName, department, date, slot, attendees)`: Validates foreign key constraints, sets initial status to `PENDING`, and inserts the record.
  - `updateStatus(long bookingId, String newStatus, String remarks, String receiptNo)`: Updates booking status, manager feedback remarks, and receipt reference.
  - `generateReceiptNumber(long bookingId)`: Returns formatted institutional slip code: `"RCSS-" + Calendar.getInstance().get(Calendar.YEAR) + "-" + String.format(Locale.US, "%04d", bookingId)`.
  - `getBookingsByUser(long userId)`: Returns all bookings for a student sorted by newest first with joined hall and user names.
  - `getPendingRequests()`: Returns all pending bookings for manager review.
  - `getAllBookings(String statusFilter, String searchQuery)`: Supports filtered queries across the entire audit ledger.
  - `getBookingCounts()`: Executes count queries returning `int[]{pendingCount, totalHalls, totalBookings}` for the dashboard KPI widgets.

#### 21. `HallDAO.java`
- **Location**: `com.mca.hallbooking.dao.HallDAO`
- **Role**: Manages CRUD operations for campus venues.
- **Key Methods**:
  - `getAllHalls()`: Returns all halls ordered alphabetically by name.
  - `getHallById(long id)`: Retrieves a specific hall by primary key.
  - `insertHall(Hall hall)`: Inserts a new hall record.
  - `updateHall(Hall hall)`: Updates existing venue details and photo URIs.
  - `deleteHall(long id)`: Deletes a hall by ID.

#### 22. `UserDAO.java`
- **Location**: `com.mca.hallbooking.dao.UserDAO`
- **Role**: Handles credential validation, registration, and user profiles.
- **Key Methods**:
  - `emailExists(String email)`: Checks whether an email is already registered.
  - `registerUser(name, email, phone, password)`: Inserts a new student account.
  - `login(email, password)`: Validates credentials and returns mapped `User` object, or `null`.
  - `getUserById(long id)` & `getUserByEmail(String email)`: Entity lookup queries.
  - `updateProfile(userId, name, phone, department, profileImage)`: Updates profile information.
  - `updatePassword(userId, newPassword)`: Updates user account password.

---

### 5.8 Database Engine & Migrations

#### 23. `DBHelper.java`
- **Location**: `com.mca.hallbooking.db.DBHelper`
- **Extends**: `SQLiteOpenHelper`
- **Role**: Core SQLite database helper (`hall_booking.db`, Version 9).
- **Key Features**:
  - Enforces foreign keys in `onConfigure()`.
  - Creates tables `users`, `halls`, and `bookings` in `onCreate()`.
  - Seeds default administrator, demo student, and 5 RCSS halls in `seedData()`.
  - **Self-Healing Schema**: In `onOpen()`, calls `ensureUserProfileColumns()`, `ensureDepartmentColumn()`, and `ensureDefaultUsers()` to verify schema health and insert missing columns dynamically.

---

### 5.9 Domain Data Models

#### 24. `Booking.java`
- **Location**: `com.mca.hallbooking.model.Booking`
- **Role**: POJO model representing a venue requisition.
- **Fields**: `id`, `userId`, `hallId`, `eventName`, `department`, `bookingDate`, `slot`, `attendees`, `status`, `remarks`, `receiptNo`, `createdAt`, `updatedAt`, `hallName`, `userName`, `userEmail`.
- **Constants**: `STATUS_PENDING = "PENDING"`, `STATUS_APPROVED = "APPROVED"`, `STATUS_REJECTED = "REJECTED"`.

#### 25. `Hall.java`
- **Location**: `com.mca.hallbooking.model.Hall`
- **Role**: POJO model representing an institutional venue.
- **Fields**: `id`, `name`, `location`, `capacity`, `amenities`, `price`, `description`, `imageUris`.
- **Key Method**: `getImageUriList()` parses the comma-separated `imageUris` string into a `List<String>`.

#### 26. `User.java`
- **Location**: `com.mca.hallbooking.model.User`
- **Role**: POJO model representing an authenticated user.
- **Fields**: `id`, `name`, `email`, `phone`, `password`, `role`, `profileImage`, `department`.
- **Constants**: `ROLE_USER = "USER"`, `ROLE_MANAGER = "MANAGER"`.

---

### 5.10 Utility Classes & Storage Sandboxing

#### 27. `Constants.java`
- **Location**: `com.mca.hallbooking.util.Constants`
- **Role**: Global application constants.
- **Contents**:
  - Time slot definitions:
    - `"09:00 AM - 01:00 PM (Morning)"`
    - `"02:00 PM - 06:00 PM (Afternoon)"`
    - `"06:00 PM - 10:00 PM (Evening)"`
  - Array of all 11 official RCSS Academic Departments.
  - Intent extra keys and shared preferences identifiers.

#### 28. `DateTimeUtils.java`
- **Location**: `com.mca.hallbooking.util.DateTimeUtils`
- **Role**: Date/time conversion and calendar parsing helpers.
- **Methods**: Formats dates (`"yyyy-MM-dd"`), parses timestamps, and validates date constraints.

#### 29. `ImageUtil.java`
- **Location**: `com.mca.hallbooking.util.ImageUtil`
- **Role**: High-performance image loading, decoding, downsampling, and local storage sandboxing.
- **Key Methods**:
  - `calculateInSampleSize(options, reqWidth, reqHeight)`: Calculates power-of-2 scaling factor to prevent OutOfMemory crashes when decoding high-resolution smartphone camera photos.
  - `loadAvatar(context, uriStr, imageView, fallbackResId)`: Safely loads avatar from file path, content URI, or resource URI into an `ImageView`.
  - `saveProfileImage(context, userId, sourceUri)`: Copies and downsamples picked photo into `files/profile_avatars/` for permanent offline access.

#### 30. `SessionManager.java`
- **Location**: `com.mca.hallbooking.util.SessionManager`
- **Role**: Encapsulated session store built on `SharedPreferences`.
- **Methods**:
  - `createLoginSession(id, name, email, role, phone, department, profileImage)`: Saves authenticated state.
  - `isLoggedIn()`, `getUserRole()`, `getUserId()`, `getName()`, `getEmail()`: Fast accessors.
  - `updateProfileDetails(...)`: Keeps session in sync when user edits their profile.
  - `logout()`: Clears all stored session preferences.

---

# 6. In-Depth Explanation of All XML Layouts & Resource Files

---

### 6.1 Activity Layouts

1. **`activity_splash.xml`**:
   - **Root**: `RelativeLayout` with deep institutional background.
   - **Elements**: Centered `ImageView` with the RCSS emblem, bold application title `TextView`, tagline, and bottom progress indicator.
2. **`activity_login.xml`**:
   - **Root**: `ScrollView` wrapping a `LinearLayout` with Material cards.
   - **Elements**: College header badge, `TextInputLayout` for Email and Password with outlined styling, Material Login button, and registration redirection link.
3. **`activity_register.xml`**:
   - **Root**: `ScrollView` wrapping registration input fields.
   - **Elements**: Inputs for Full Name, Email, Phone Number, Password, and Confirm Password with input validation hints.
4. **`activity_user_dashboard.xml`**:
   - **Root**: `DrawerLayout` containing a `CoordinatorLayout` and `NavigationView`.
   - **Elements**: `Toolbar` with navigation menu toggle, search bar `TextInputEditText` (`etSearchHalls`), quick shortcut button to "My Bookings", and `RecyclerView` (`recyclerHalls`) with empty-state view.
5. **`activity_hall_detail.xml`**:
   - **Root**: `CoordinatorLayout` with collapsible layout and `NestedScrollView`.
   - **Elements**: Multi-image carousel (`RecyclerView`), capacity and location badges, amenities chip group, date picker button, slot radio group, availability check button, dynamic booking requisition form, and confirmation button.
6. **`activity_my_bookings.xml`**:
   - **Root**: `LinearLayout` with `Toolbar`.
   - **Elements**: `RecyclerView` (`recyclerMyBookings`) and an empty state layout with calendar icon and descriptive text.
7. **`activity_receipt.xml`**:
   - **Root**: `CoordinatorLayout` with `NestedScrollView`.
   - **Elements**: Material card styled as an institutional allocation slip, verification reference header, 10 structured detail rows (`row_receipt_field.xml`), official seal indicator, and return button.
8. **`activity_manager_dashboard.xml`**:
   - **Root**: `DrawerLayout` with `CoordinatorLayout`.
   - **Elements**: Executive KPI header card showing 3 counter badges (*Pending, Halls, Total Bookings*), quick action exterior cards for *Manage Halls* and *All Bookings*, and a pending review `RecyclerView`.
9. **`activity_manager_halls.xml`**:
   - **Root**: `CoordinatorLayout`.
   - **Elements**: `RecyclerView` listing all halls in administrative mode, and a Floating Action Button (FAB) to add a new venue.
10. **`activity_edit_hall.xml`**:
    - **Root**: `ScrollView` with `LinearLayout`.
    - **Elements**: Text inputs for hall name, campus location, seating capacity, amenities, and full description; horizontal `RecyclerView` showing photo thumbnails with delete icons; and an "Add Photos" button.
11. **`activity_manager_all_bookings.xml`**:
    - **Root**: `LinearLayout` with `Toolbar`.
    - **Elements**: Live search input, horizontal scroll container with pill-shaped filter chips (`ALL`, `PENDING`, `APPROVED`, `REJECTED`), record counter `TextView`, and `RecyclerView`.
12. **`activity_profile.xml`**:
    - **Root**: `ScrollView` with Material card containers.
    - **Elements**: Circular avatar `ImageView` with camera overlay icon, display name, role badge, editable profile fields, department dropdown selector, password change card, and logout button.
13. **`activity_about.xml`**:
    - **Root**: `ScrollView` with institutional branding cards.
    - **Elements**: RCSS Autonomous crest, accreditation details, department attribution (MCA), and developer credits.

---

### 6.2 Component & RecyclerView Item Layouts

1. **`item_hall.xml`**: Card layout for venue discovery. Contains primary photo thumbnail, venue title, capacity chip, location text, amenities summary, and "View & Book" button.
2. **`item_booking.xml`**: Card layout for student booking history. Displays event title, hall name, department, date and slot, dynamic status badge (`PENDING`, `APPROVED`, `REJECTED`), rejection reason remarks container, and "View Approval Slip" button.
3. **`item_request.xml`**: Card layout for administrative review. Contains applicant details, event information, expected attendees, and action buttons for "Approve" (green) and "Reject" (red).
4. **`item_carousel_image.xml`**: Full-width image item for `ViewPager2` / carousel in `HallDetailActivity`.
5. **`item_edit_image.xml`**: Thumbnail card with a top-right circular delete button for photo management in `EditHallActivity`.
6. **`row_receipt_field.xml`**: Reusable two-column key-value row (`tvLabel` and `tvValue`) with subtle divider used in `ReceiptActivity`.

---

### 6.3 Navigation Drawer Headers

1. **`nav_header_user.xml`**: Header layout for student drawer. Includes a 54dp top padding clearance for device notches, circular avatar with click feedback, student name, email, and department badge.
2. **`nav_header_manager.xml`**: Header layout for administrator drawer. Includes status bar clearance, admin avatar, name (*"RCSS Hall Administrator"*), official email, and "Administration Portal" badge.

---

### 6.4 Menus

1. **`menu_user_drawer.xml`**: Student drawer menu items: *Explore Halls*, *My Bookings*, *My Profile*, *About RCSS*, *Logout*.
2. **`menu_user_dashboard.xml`**: Student action bar options: Quick refresh and profile shortcut.
3. **`menu_manager_drawer.xml`**: Administrator drawer menu items: *Review Dashboard*, *Manage Halls*, *All Bookings Ledger*, *Admin Profile*, *Logout*.
4. **`menu_manager_dashboard.xml`**: Admin action bar options: Refresh queue and profile shortcut.

---

### 6.5 Design Tokens: Colors, Dimensions & Themes

- **`colors.xml`**:
  - Primary Brand: Deep Purple (`#6200EE` / `#7C4DFF`)
  - Accent / Secondary: Teal (`#03DAC5`)
  - Status Approved: Forest Green (`#2E7D32` with light tinted background `#E8F5E9`)
  - Status Pending: Amber Gold (`#EF6C00` with light tinted background `#FFF3E0`)
  - Status Rejected: Crimson Red (`#C62828` with light tinted background `#FFEBEE`)
  - Card & Surface: Pure White (`#FFFFFF`) in light mode; Rich Charcoal (`#1E1E1E`) in dark mode.
- **`themes.xml` & `values-night/themes.xml`**: Full Material 3 DayNight compliance ensuring clean, high-contrast readability in both light and dark display modes.
- **`dimens.xml`**: Standardized 8dp grid spacing (4dp, 8dp, 16dp, 24dp, 54dp drawer top gap).

---

# 7. In-Depth Explanation of Configuration & Build Files

### 1. `AndroidManifest.xml`
- **Application Declaration**:
  - `android:name=".HallBookingApp"`: Specifies custom application subclass.
  - `android:allowBackup="true"`: Enables standard backup.
  - `android:icon="@drawable/ic_launcher"`: Application launcher icon.
  - `android:theme="@style/Theme.HallBookingSystem"`: Global Material 3 theme.
- **Launcher Activity**: `SplashActivity` configured with:
  ```xml
  <intent-filter>
      <action android:name="android.intent.action.MAIN" />
      <category android:name="android.intent.category.LAUNCHER" />
  </intent-filter>
  ```
- **Activity Declarations**: All 13 secondary activities explicitly declared with `android:exported="false"` for strict Android security compliance.
- **No Unwanted Permissions**: Cleaned of obsolete `WRITE_EXTERNAL_STORAGE` and `FileProvider` tags since photo management is handled natively via Android internal app sandboxing.

### 2. `app/build.gradle`
- **Compile SDK & Target SDK**: 34 (Android 14).
- **Min SDK**: 24 (Android 7.0 Nougat) — covers over 98% of active Android devices.
- **Java Compatibility**: `JavaVersion.VERSION_1_8` for source and target.
- **Key Dependencies**:
  - `com.google.android.material:material:1.11.0`: Material Design 3 UI components (buttons, text fields, cards, chips, navigation drawer).
  - `androidx.appcompat:appcompat:1.6.1`: Backward-compatible activity and widget base.
  - `androidx.constraintlayout:constraintlayout:2.1.4`: Responsive layout engine.
  - `androidx.viewpager2:viewpager2:1.0.0`: Swipeable carousel view.
  - `androidx.cardview:cardview:1.0.0`: Elevated card containers.

### 3. `build.gradle` (Project Root) & `settings.gradle`
- Configures Android Gradle Plugin (AGP 8.3+) and standard repositories (`google()` and `mavenCentral()`).

---

# 8. End-to-End Execution Flowcharts & Algorithms

---

### 8.1 Booking Requisition & Collision Detection Algorithm

```
User selects Hall, Date, and Time Slot
                  │
                  ▼
Tap "Check Availability" (HallDetailActivity)
                  │
                  ▼
BookingDAO.isSlotTaken(hallId, date, slot)
                  │
                  ▼
SQL Query:
SELECT id FROM bookings 
WHERE hall_id = ? AND booking_date = ? AND slot = ? 
  AND status IN ('PENDING', 'APPROVED')
                  │
       ┌──────────┴──────────┐
       │                     │
Cursor Count > 0       Cursor Count == 0
       │                     │
       ▼                     ▼
[SLOT OCCUPIED]       [SLOT AVAILABLE]
Display Red Warning   Display Green Confirmation
Hide Booking Form     Reveal Booking Form
                             │
                             ▼
                      Enter Event Details & Attendees
                             │
                             ▼
                      Tap "Confirm & Submit Request"
                             │
                             ▼
                      Validate Input (Attendees <= Capacity)
                             │
                             ▼
                      Double-Check isSlotTaken()
                             │
                             ▼
                      BookingDAO.createBooking(...)
                             │
                             ▼
                      Status: PENDING
                      Redirect to MyBookingsActivity
```

---

### 8.2 Administrative Approval & Digital Slip Generation

```
Admin opens Manager Dashboard
                  │
                  ▼
Pending Requests Loaded via BookingDAO.getPendingRequests()
                  │
                  ▼
Admin taps "Approve" on Request Card
                  │
                  ▼
Generate Institutional Slip Reference:
receiptNo = "RCSS-2026-" + String.format("%04d", bookingId)
                  │
                  ▼
BookingDAO.updateStatus(id, "APPROVED", remarks = null, receiptNo)
                  │
                  ▼
Database Row Updated (status = 'APPROVED', receipt_no = receiptNo)
                  │
                  ▼
Review card dismissed from Manager Dashboard
KPI badges updated
                  │
                  ▼
Student opens "My Bookings"
Badge shows green "APPROVED"
"View Approval Slip" button appears
Tapping button displays official in-app slip in ReceiptActivity
```

---

### 8.3 Administrative Rejection with Feedback Justification

```
Admin taps "Reject" on Request Card
                  │
                  ▼
System prompts AlertDialog with EditText:
"Enter Rejection Reason / Remarks"
                  │
                  ▼
Admin types reason (e.g., "Hall reserved for NAAC Peer Team visit")
                  │
                  ▼
Admin confirms dialog
                  │
                  ▼
BookingDAO.updateStatus(id, "REJECTED", remarks = reason, receiptNo = null)
                  │
                  ▼
Review card dismissed from Manager Dashboard
                  │
                  ▼
Student opens "My Bookings"
Badge shows red "REJECTED"
Rejection remarks container becomes VISIBLE
Shows: "Reason: Hall reserved for NAAC Peer Team visit"
```

---

### 8.4 Image Ingestion & Offline Sandboxing Pipeline

```
User selects Photo from Device Gallery (Profile DP or Hall Photo)
                           │
                           ▼
ActivityResultContracts.GetContent() returns Content URI
(e.g., content://media/external/images/media/10000042)
                           │
                           ▼
ImageUtil.saveProfileImage() / copyUriToInternalStorage()
                           │
                           ▼
1. Calculate inSampleSize to downsample Bitmap safely to 600x600
2. Decode Stream into downsampled Bitmap (prevents OOM crashes)
3. Open FileOutputStream in context.getFilesDir() ("profile_avatars/")
4. Compress to JPEG (Quality 90)
                           │
                           ▼
Returns permanent internal file path:
file:///data/user/0/com.mca.hallbooking/files/profile_avatars/avatar_1_1727230000.jpg
                           │
                           ▼
Path saved in SQLite database
(Permanent offline access immune to Android URI permission expiration)
```

---

# 9. Presentation & Slide-by-Slide Delivery Script

Use this structured script to present the project with the accompanying 5-slide presentation deck ([RCSS_Hall_Booking_System_Presentation.pptx](file:///c:/Users/ashbi/Documents/Android/HallBookingSystem/RCSS_Hall_Booking_System_Presentation.pptx)):

### Slide 1: Title & Institutional Overview
- **Visual**: Slide 1 — Title, Rajagiri College of Social Sciences (Autonomous), Kalamassery.
- **What to Say**:
  > *"Respected evaluators, today I present the **RCSS Hall Booking & Management System**, an enterprise native Android application built specifically for Rajagiri College of Social Sciences (Autonomous), Kalamassery. The system is engineered to digitize and automate the entire campus venue requisition and administrative approval process across all academic auditoriums, seminar halls, and activity centers."*

### Slide 2: Problem Statement & Proposed Solution
- **Visual**: Slide 2 — Problem Statement vs. Modern Solution.
- **What to Say**:
  > *"In our campus, scheduling halls across 11 academic departments traditionally required manual paper forms, physical signatures from faculty coordinators and administrators, and paper registers. This caused booking conflicts, zero transparency, and delays. Our solution replaces this with a role-based digital system that guarantees zero double-booking collisions, instant slot verification, and transparent real-time approval tracking."*

### Slide 3: Key Features & Role-Based Workflows
- **Visual**: Slide 3 — Student Portal vs. Administrator Portal.
- **What to Say**:
  > *"The system provides two tailored portals:*
  > *1. **For Students and Faculty Coordinators**: Browse venues with swipeable photo carousels, live search, select time slots, verify availability in real-time, submit requisitions, track approval status, read administrative rejection feedback, and access an official in-app digital allocation slip.*
  > *2. **For Administrators**: An executive KPI dashboard, quick-action cards, one-tap approvals with automated verification code generation, rejections with mandatory feedback remarks, comprehensive hall and photo management, and a complete institutional audit ledger with status filter chips."*

### Slide 4: System Architecture & Technical Highlights
- **Visual**: Slide 4 — Architecture, Tech Stack & Database.
- **What to Say**:
  > *"Architecturally, the application is built on Java and Android SDK (API 24 to 34), adhering to the Model-View-Controller (MVC) pattern and Data Access Object (DAO) pattern. We use an embedded SQLite database with schema version 9 and self-healing migrations. To ensure total offline stability, all user avatars and hall photos are downsampled and stored in internal private app storage, eliminating Android URI permission expiration issues."*

### Slide 5: Campus Venues & Academic Impact
- **Visual**: Slide 5 — Pre-configured Venues & Conclusion.
- **What to Say**:
  > *"The platform comes pre-configured with the 5 central halls of the Kalamassery campus: Golden Auerole (600 seats), Carmel Hall (250 seats), Chavera Hall (200 seats), Assumption Hall (120 seats), and Esperansa Hall (150 seats), integrated across all 11 RCSS academic schools. In conclusion, the system delivers complete administrative efficiency, eliminates paper waste, and establishes complete accountability for campus event management. Thank you."*

---

# 10. Comprehensive Viva Voce Q&A (Top 25 Questions)

### Q1: Why did you choose SQLite over Room or Firebase for this project?
**Answer**: SQLite is Android's native, lightweight, zero-configuration embedded relational database. For this application, direct SQLite via `SQLiteOpenHelper` and custom DAOs was chosen to demonstrate a deep understanding of low-level SQL queries, database cursor management, relational schema migrations, and transaction integrity without relying on high-level third-party abstraction libraries. Furthermore, it operates 100% offline with zero external cloud dependencies.

### Q2: How does the system guarantee zero double-booking collisions?
**Answer**: In `BookingDAO.java`, the `isSlotTaken(hallId, date, slot)` method queries the database:
```sql
SELECT id FROM bookings 
WHERE hall_id = ? AND booking_date = ? AND slot = ? 
  AND status IN ('PENDING', 'APPROVED')
```
If a record exists with status `PENDING` or `APPROVED`, the slot is flagged as occupied. This check is executed twice: first when the user taps "Check Availability", and again as an atomic validation before `createBooking()` executes.

### Q3: What happens when a booking is rejected?
**Answer**: When an administrator taps "Reject" on the dashboard, an `AlertDialog` prompts for mandatory feedback remarks. The administrator enters the reason (e.g., *"Reserved for NAAC peer review"*). The database updates status to `REJECTED` and stores the remarks. When the student opens "My Bookings", `BookingAdapter` detects `STATUS_REJECTED`, sets the rejection container to `VISIBLE`, and displays the exact justification to the student.

### Q4: Why did you migrate from PDF generation to native in-app allocation slips?
**Answer**: Traditional PDF generation on mobile devices introduces external storage permission complexities (`WRITE_EXTERNAL_STORAGE`), device-specific PDF viewer dependencies, and file-sharing security risks. By replacing it with `ReceiptActivity`, students view an official, tamper-proof, branded allocation slip with verification code `RCSS-2026-XXXX` natively inside the app with zero file downloads or external permissions required.

### Q5: How do you handle Android URI permission expiration for images?
**Answer**: When an image is picked from the system gallery, the returned `content://` URI is temporary and expires when the app or device restarts. In `ImageUtil.java` and `EditHallActivity.java`, we solve this by reading the image stream, downsampling it safely, and copying the bytes into the app's internal private storage sandbox (`context.getFilesDir()`, `profile_avatars/` or `hall_images/`). We then save the permanent `file://` URI to SQLite, ensuring the image is always accessible.

### Q6: How do you prevent OutOfMemory (OOM) errors when loading user photos?
**Answer**: In `ImageUtil.java`, we use `BitmapFactory.Options` with `inJustDecodeBounds = true` to decode the image dimensions without allocating memory for pixels. We then compute `inSampleSize` to downsample the image (e.g., scaling a 12MP photo down to 600x600) before setting `inJustDecodeBounds = false` and loading the scaled bitmap.

### Q7: What architectural pattern is implemented in this application?
**Answer**: Model-View-Controller (MVC) combined with the Data Access Object (DAO) pattern:
- **Model**: `User.java`, `Hall.java`, `Booking.java`.
- **View**: XML layouts and Material 3 components.
- **Controller**: Activities and RecyclerView Adapters.
- **DAO**: `UserDAO`, `HallDAO`, `BookingDAO` abstracting all database operations.

### Q8: What is the purpose of `HallBookingApp.java`?
**Answer**: It extends `android.app.Application` and represents the global application state. It executes `DBHelper.getInstance(this).getWritableDatabase()` on launch to ensure the database schema, tables, default users, and pre-seeded halls are initialized before any UI activity opens.

### Q9: How is user session maintained across app restarts?
**Answer**: Using `SessionManager.java`, which encapsulates Android `SharedPreferences`. When a user logs in, their ID, full name, email, role (`USER` or `MANAGER`), department, and profile image URI are stored as key-value pairs. `SplashActivity` checks `sessionManager.isLoggedIn()` to route returning users directly to their dashboard.

### Q10: What are "Self-Healing Migrations" in your database?
**Answer**: In `DBHelper.java`, within `onOpen()`, the helper queries `PRAGMA table_info(...)` to inspect active table columns. If newer columns like `profile_image` or `department` are missing, it executes `ALTER TABLE ... ADD COLUMN` non-destructively without wiping existing user booking data. It also verifies default accounts and pre-seeded halls, adding them if absent.

### Q11: Explain the ViewHolder pattern used in `BookingAdapter`.
**Answer**: The ViewHolder pattern caches references to layout view elements (`findViewById`) within an inner class `BookingViewHolder`. When a user scrolls a `RecyclerView`, Android reuses existing item views and calls `onBindViewHolder()` rather than inflating new XML layouts and calling `findViewById()` repeatedly, ensuring smooth 60 FPS scrolling.

### Q12: How does `ImageCarouselAdapter` work?
**Answer**: It binds to a horizontal `RecyclerView` equipped with `PagerSnapHelper` in `HallDetailActivity`. It accepts a list of image URI strings (pre-seeded drawable references or local file paths), inflates `item_carousel_image.xml`, and displays each photo full-width with snapping pagination.

### Q13: How does the live search filter work in `UserDashboardActivity`?
**Answer**: A `TextWatcher` is attached to `etSearchHalls`. In `onTextChanged()`, the input string is matched case-insensitively against hall name, location, and capacity. The filtered list is submitted to `HallAdapter.filterList()`, which notifies the adapter to re-render matching halls immediately.

### Q14: How does the administrative filter chip bar work in `ManagerAllBookingsActivity`?
**Answer**: The screen has four pill-shaped `TextView` chips: `ALL`, `PENDING`, `APPROVED`, and `REJECTED`. Clicking a chip sets `selectedStatus`, updates the chip's background color while preserving identical pill geometry, and invokes `bookingDAO.getAllBookings(selectedStatus, searchQuery)` to update the list.

### Q15: Why are foreign key constraints explicitly enabled in `DBHelper`?
**Answer**: In SQLite, foreign key enforcement is disabled by default for backward compatibility. In `DBHelper.onConfigure()`, we execute `db.setForeignKeyConstraintsEnabled(true)` to ensure referential integrity between `bookings.user_id` -> `users.id` and `bookings.hall_id` -> `halls.id`.

### Q16: How do you prevent past dates from being booked?
**Answer**: In `HallDetailActivity`, when launching the `DatePickerDialog`, we call `datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis())`, which disables all past calendar dates in the picker.

### Q17: What security measures are implemented for user authentication?
**Answer**:
1. Duplicate email prevention using unique constraints in SQLite and pre-validation in `UserDAO.emailExists()`.
2. Password length enforcement (minimum 6 characters).
3. Current password verification before allowing a password update in `ProfileActivity`.
4. Role-based routing preventing students from accessing manager screens.

### Q18: What is the purpose of `PagerSnapHelper`?
**Answer**: It is an Android support class attached to a `RecyclerView` that mimics the paging behavior of a `ViewPager`, snapping carousel photos cleanly to the center of the screen when swiped.

### Q19: How are the 5 RCSS halls pre-seeded?
**Answer**: In `DBHelper.seedHalls()`, structured `ContentValues` containing venue names, locations, capacities, comma-separated amenities, and drawable resource paths (`res:img_auditorium_1,...`) are inserted into the `halls` table during database creation.

### Q20: How does the app support Dark Mode?
**Answer**: The app utilizes Material 3 DayNight themes. In `values/themes.xml` and `values-night/themes.xml`, semantic color tokens (`colorPrimary`, `colorSurface`, `android:colorBackground`) are defined, allowing Android to automatically adjust colors when the system switches to Dark Theme.

### Q21: What is the role of `row_receipt_field.xml`?
**Answer**: It is a modular XML component used repeatedly in `ReceiptActivity` to render clean, two-column labeled metadata (`tvLabel` and `tvValue`) with consistent typography and dividers.

### Q22: Why did you provide a 54dp top gap in the navigation drawer?
**Answer**: Modern smartphones feature front-facing camera cutouts (punch-holes, notches) and status bars. A 54dp top padding in `nav_header_user.xml` and `nav_header_manager.xml` ensures the user's profile avatar and name are never obscured or clipped by the status bar or hardware notch.

### Q23: How are booking receipt numbers structured?
**Answer**: In `BookingDAO.generateReceiptNumber(bookingId)`, the receipt reference is constructed using the institution code, the current year, and a 4-digit zero-padded booking ID:
`"RCSS-" + year + "-" + String.format(Locale.US, "%04d", bookingId)` (e.g., `RCSS-2026-0005`).

### Q24: What are the demo credentials pre-seeded in the database?
**Answer**:
- **Administrator**: `admin@rajagiri.edu` / `Admin@123`
- **Student**: `student@rajagiri.edu` / `Student@123`
- Additional student accounts can be registered at any time via `RegisterActivity`.

### Q25: What future enhancements could be added to this project?
**Answer**:
1. Integration with Firebase Cloud Messaging (FCM) for real-time push notifications on booking approvals/rejections.
2. Integration with college biometric or LDAP / Single Sign-On (SSO) systems.
3. Exportable audit reports in Excel/CSV format for administrative archival.
4. QR code embedding on the in-app allocation slip for gatekeeper security scanning.
