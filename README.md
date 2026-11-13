# 💰 Expense Tracker — Personal Finance Android App

A modern, feature-rich, and offline-first Android application designed to help users track daily income and expenses, calculate monthly loan EMIs, view visual analytics with interactive pie charts, and manage personal finances effortlessly.

Built with **Java**, **Android SDK 34/37**, **SQLite**, and **Material 3 Design**, supporting seamless **Dark and Light** themes.

---

## ✨ Key Features

- 💵 **Real-Time Financial Dashboard**: Displays live **Available Balance**, **Total Income**, and **Total Expenses** with automatic instant recalculations.
- 📊 **Analytics & Reports**: Yearly summary metrics and monthly breakdowns. Tapping any month card expands an interactive **Donut/Pie Chart** comparing Income vs. Expense percentages.
- 🧮 **In-App Calculator & Monthly EMI Planner**:
  - **Standard Calculator**: Quick arithmetic solver with interactive keypad (`0-9`, `.`, `C`, `⌫`, `+`, `-`, `×`, `÷`, `%`, `=`).
  - **Monthly EMI Calculator**: Enter Loan Amount, Interest Rate (% p.a.), and Tenure (Years) to calculate **Monthly EMI**, **Total Interest**, and **Total Payment**.
- 🍔 **Side Navigation Drawer (Burger Menu)**: Smooth left side menu covering ~half of the screen with quick access to Dashboard, Analytics, Calculator & EMI, and Settings.
- 🎨 **Adaptive Light & Dark Themes**:
  - **Light Theme**: Warm soft cream/beige background (`#F6EFE9`) with crisp off-white cards.
  - **Dark Theme**: Sleek midnight blue background (`#111827`) with dark navy cards (`#1E2638`).
- 🏷️ **Gen-Z / Millennial Category Pill Badges**: Styled transaction cards featuring category pill badges (`Food`, `Shopping`, `Bills`, `Income`, `Transport`) with pastel color tinting.
- 🗑️ **Transaction Management & Deletion**: Quick tap on the trash icon or long-press on any transaction card to delete mistakenly added records with confirmation dialogs.
- 🚀 **Animated Splash Screen**: Opening scale-and-fade logo entrance animation on launch.
- 🔒 **100% Offline & Private**: All data is stored locally on the device in an SQLite database (`expense_tracker.db`) with zero network latency or cloud data exposure.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Java (JDK 11)
- **Minimum SDK**: API 23 (Android 6.0+)
- **Target SDK**: API 34 / 37 (Android 14+)
- **Database**: SQLite (`SQLiteOpenHelper`)
- **UI & Layouts**: Material 3 Components, Custom Canvas Drawing (`PieChartView`), ConstraintLayout, DrawerLayout, ScrollView
- **Preferences**: `SharedPreferences` (`app_settings` for Dark/Light mode state)
- **Architecture**: Model-View-Controller (MVC) / Repository-driven UI

---

## 📱 Screen Structure

| Screen / Component | Description |
| :--- | :--- |
| **`SplashActivity`** | Opening entrance transition with scale & fade animation. |
| **`MainActivity`** | Main dashboard with hero credit-card balance, quick action buttons, analytics preview card, and recent transactions. |
| **`AddIncomeActivity`** | Log income with amount, source, and optional notes. |
| **`AddExpenseActivity`** | Log expenses with amount, category dropdown, description, and optional notes. |
| **`AnalyticsActivity`** | Reports screen with year selector, yearly summary, and expandable monthly pie charts. |
| **`CalculatorActivity`** | Dual-mode screen for standard math calculations and loan EMI planning. |
| **`SettingsActivity`** | Theme toggle (Light/Dark mode) and safety database clear option. |

---

## 🚀 How to Build & Run

1. Clone or download this repository:
   ```bash
   git clone https://github.com/Priyangshu-04/Expense-Tracker-Android.git
