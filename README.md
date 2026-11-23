# Expense Tracker Android - Personal Finance & Loan Analytics Engine

An offline-first, privacy-centric mobile financial management system built for Android. This application provides real-time income and expense tracking, custom canvas-driven financial visualizations, an integrated mathematical evaluator, and an Equated Monthly Installment (EMI) loan amortisation calculator.

---

## Academic & Engineering Overview

Modern mobile personal finance utilities frequently suffer from excessive telemetry, network latency, forced cloud synchronization, and subscription-gated features. This project addresses these challenges by implementing an offline-first architecture powered by SQLite, ensuring zero latency, complete data sovereignty, and deterministic on-device financial computations.

### Core Architecture & Design Patterns
- **Architectural Paradigm**: Model-View-Controller (MVC) with decoupled Data Access Objects (DAO).
- **Persistence Layer**: Native SQLite database engine using `SQLiteOpenHelper` with structured relational schemas and aggregate analytical queries.
- **Custom Graphics Pipeline**: Native 2D canvas drawing (`Canvas`, `Paint`, `RectF`) implementing dynamic donut/arc geometry without third-party graphics overhead.
- **Theme Engine**: Runtime dynamic theme resolution (`ThemeManager`) with persistent user preferences stored via Android `SharedPreferences`.
- **System Integration**: Edge-to-Edge UI compliance leveraging AndroidX `WindowInsetsCompat`.

---

## Technical Specifications

| Parameter | Specification |
| :--- | :--- |
| **Language** | Java (JDK 11) |
| **Minimum SDK** | API Level 23 (Android 6.0 Marshmallow) |
| **Target / Compile SDK** | API Level 34 / 37 (Android 14 / Upside Down Cake) |
| **Local Database** | SQLite (`expense_tracker.db`) |
| **UI Framework** | Material 3, ConstraintLayout, DrawerLayout |
| **Vector & Canvas Engine** | Custom `PieChartView` extending `android.view.View` |
| **Build System** | Gradle (Kotlin DSL: `build.gradle.kts`) |

---

## System Modules & Feature Breakdown

### 1. Financial Dashboard (`MainActivity`)
- Aggregate computation of Available Balance, Total Inflow (Income), and Total Outflow (Expenses).
- Real-time recalculation triggered on transaction insertions, deletions, and updates.
- Navigation Drawer allowing rapid transitions between analytical modules.

### 2. Transaction Management (`AddIncomeActivity`, `AddExpenseActivity`, `DatabaseHelper`)
- Categorization engine with dedicated tagging (Food, Shopping, Bills, Transport, Income).
- Contextual deletion workflows with interactive confirmation dialogs to prevent accidental data loss.
- High-precision timestamp recording for chronological indexing.

### 3. Custom Canvas Data Visualization (`PieChartView`, `AnalyticsActivity`)
- Implements custom 2D vector drawing using trigonometric angle distribution.
- Renders an interactive donut chart comparing Income vs. Expense ratios.
- Yearly summaries coupled with dynamic monthly breakdown expansions.

### 4. Integrated Financial Computation Engine (`CalculatorActivity`)
- **Dual-Mode Interface**: Rapid toggle between an arithmetic keypad and an EMI projection system.
- **Arithmetic Infix Evaluator**: Parses and computes tokenized arithmetic expressions.
- **Loan Amortization (EMI Formula)**:
  Computes the monthly payment using the standard reducing-balance loan formula:
  $$E = P \cdot r \cdot \frac{(1 + r)^n}{(1 + r)^n - 1}$$
  where:
  - $E$ = Equated Monthly Installment
  - $P$ = Principal Loan Amount
  - $r$ = Monthly interest rate ($Annual Rate / (12 \times 100)$)
  - $n$ = Loan duration in months ($Tenure \times 12$)
- Computes Total Interest Payable and Gross Repayment metrics dynamically.

### 5. Adaptive Theme System (`ThemeManager`, `SettingsActivity`)
- Full dual-palette design system:
  - Soft Cream Minimalist Palette (Light Theme)
  - Midnight Navy Palette (Dark Theme)
- Instant activity recreations and persistent state recall across application lifecycles.

---

## Project Hierarchy

```
Expense-Tracker-Android/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/expensetracker/
│   │       │   ├── AddExpenseActivity.java   # Expense recording controller
│   │       │   ├── AddIncomeActivity.java    # Income recording controller
│   │       │   ├── AnalyticsActivity.java    # Visual reporting & monthly summaries
│   │       │   ├── CalculatorActivity.java   # Arithmetic & EMI calculation engine
│   │       │   ├── DatabaseHelper.java       # SQLite schema & CRUD operations
│   │       │   ├── MainActivity.java         # Primary dashboard & drawer container
│   │       │   ├── MonthSummary.java         # Data transfer object for aggregated metrics
│   │       │   ├── PieChartView.java         # Custom canvas 2D visualization view
│   │       │   ├── SettingsActivity.java     # User preferences & database reset controls
│   │       │   ├── SplashActivity.java       # Animated initial launch transition
│   │       │   ├── ThemeManager.java         # Dynamic theme resolution utility
│   │       │   └── Transaction.java          # Core financial entity model
│   │       ├── res/                          # Vector assets, themes, color palettes, layouts
│   │       └── AndroidManifest.xml           # Application manifests and activity registry
│   └── build.gradle.kts                      # Module build specifications and dependencies
├── gradle/                                   # Gradle wrapper binaries and configurations
├── build.gradle.kts                          # Root build script
├── settings.gradle.kts                       # Repository & dependency resolution settings
└── README.md                                 # Technical documentation
```

---

## Database Schema

The persistence layer defines the `transactions` table structured as follows:

| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Unique record identifier |
| `type` | `TEXT` | `NOT NULL` | Transaction category (`INCOME` or `EXPENSE`) |
| `amount` | `REAL` | `NOT NULL` | Numerical monetary value |
| `title` | `TEXT` | `NOT NULL` | Transaction source or description |
| `note` | `TEXT` | `NULLABLE` | Supplementary user notes |
| `timestamp` | `INTEGER` | `NOT NULL` | Epoch millisecond representation |

---

## Getting Started & Build Instructions

### Prerequisites
- **Android Studio**: Iguana (2023.2.1) or later recommended.
- **Java Development Kit (JDK)**: Version 11 or higher.
- **Android SDK**: Build Tools with API Level 34+ installed.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Priyangshu-04/Expense-Tracker-Android.git
   ```
2. Open Android Studio and choose **Open an Existing Project**, selecting the cloned directory.
3. Allow Gradle to synchronize dependencies.
4. Connect an Android physical device with USB Debugging enabled or initialize an Android Virtual Device (AVD).
5. Build and deploy by clicking **Run** (`Shift + F10`).

---

## Security & Data Privacy
- Offline-first SQLite storage (`expense_tracker.db`) ensuring zero external telemetry.
- Parameterized SQL queries (`?` bindings) in `DatabaseHelper` to prevent local SQL injection.
- Android internal storage sandboxing to protect raw transaction records.

---

## Testing & Validation
- Input validation for empty titles, negative values, and decimal formatting across transaction forms.
- Boundary value testing in `CalculatorActivity` for 0% interest rates and multi-year EMI scaling.
- State and theme persistence verification across device rotations via `ThemeManager`.

---

## Future Enhancements
- Biometric verification (AndroidX Biometric API) for restricted transaction viewing.
- Tabular data export to CSV / PDF for personal accounting records.
- Multi-currency conversion via localized symbol configurations.
- Category budget thresholds with proactive visual warnings.

---

## Author
- **Priyangshu-04** - [GitHub Profile](https://github.com/Priyangshu-04)
- **shawonmridha-sm** - [GitHub Profile](https://github.com/shawonmridha-sm)
