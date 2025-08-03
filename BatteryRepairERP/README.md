# Battery Repair ERP - Android Application

A comprehensive Android-based ERP application for managing battery repair shops with Firebase integration, QR code tracking, and multi-role access control.

## 🚀 Features

### 🔧 Core Functionality
- **Battery Inward Entry**: Record incoming batteries with customer and battery details
- **QR Code Generation**: Automatic unique QR codes for battery tracking
- **PDF Receipt Generation**: Professional inward receipts
- **Repair Workflow Management**: Track batteries through repair stages
- **Multi-Role Access Control**: Admin, Staff, and Technician roles
- **Invoice Generation**: Professional billing with PDF export
- **Customer Management**: Comprehensive customer database
- **Firebase Integration**: Cloud data synchronization
- **Offline Admin Login**: Initial setup without Firebase dependency

### 👥 User Roles & Permissions

#### 🔑 Admin
- Full system access
- User management (add/edit/delete users)
- Firebase configuration
- System settings management
- All staff and technician permissions

#### 👨‍💼 Staff
- Create battery entries
- Assign batteries to technicians
- Update battery status
- Generate invoices and receipts
- Customer management
- Print QR codes

#### 🔧 Technician
- View assigned batteries
- Update repair status and notes
- Input test results and final values
- Add repair comments

### 📱 App Flow

1. **Splash Screen** → Route based on app state
2. **Login** → Hardcoded admin login or Firebase authentication
3. **Setup** → Firebase configuration (admin only, first time)
4. **Main App** → Role-based navigation and features

### 🔄 Battery Repair Workflow

```
Inward → Assigned → In Progress → Completed → Quality Check → Ready for Delivery → Delivered
```

Each status change is tracked with timestamps, user information, and notes.

## 🏗️ Technical Architecture

### 📁 Project Structure
```
app/
├── src/main/
│   ├── java/com/batteryrepair/erp/
│   │   ├── data/
│   │   │   ├── model/           # Data models (User, Battery, Customer, Invoice)
│   │   │   ├── repository/      # Data repositories
│   │   │   └── firebase/        # Firebase configuration
│   │   ├── ui/
│   │   │   ├── splash/          # Splash screen
│   │   │   ├── auth/            # Login/authentication
│   │   │   ├── setup/           # Firebase setup
│   │   │   └── main/            # Main application
│   │   ├── utils/               # Utility classes
│   │   └── BatteryRepairApplication.kt
│   ├── res/
│   │   ├── layout/              # XML layouts
│   │   ├── drawable/            # Icons and graphics
│   │   ├── values/              # Strings, colors, themes
│   │   └── menu/                # Navigation menus
│   └── AndroidManifest.xml
└── build.gradle
```

### 🛠️ Tech Stack
- **Language**: Kotlin
- **UI**: Material Design 3, View Binding
- **Architecture**: MVVM with Repository pattern
- **Database**: Firebase Realtime Database + Room (offline)
- **Authentication**: Firebase Auth + Local admin
- **QR Code**: ZXing library
- **PDF Generation**: iText 7
- **Image Loading**: Glide
- **Networking**: Retrofit 2 (if needed)
- **Async**: Kotlin Coroutines + Flow

### 📊 Data Models

#### User
- ID, username, email, full name
- Role (Admin/Staff/Technician)
- Permissions and access control
- Profile information

#### Battery
- Unique ID and QR code
- Customer information
- Battery details (type, brand, serial, voltage)
- Repair information and status history
- Assigned technician
- Invoice details

#### Customer
- Contact information
- Battery history
- Loyalty points and discounts
- Business vs individual classification

#### Invoice
- Items, parts, and services
- Tax calculations
- Payment status and method
- PDF generation capability

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9.20+
- Firebase project (optional, for cloud features)

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd BatteryRepairERP
```

2. **Open in Android Studio**
- Open Android Studio
- Select "Open an existing project"
- Navigate to the cloned directory

3. **Build the project**
- Let Android Studio sync and download dependencies
- Build → Make Project

4. **Run the app**
- Connect Android device or start emulator
- Run → Run 'app'

### 🔧 Configuration

#### Initial Setup
1. **Launch app** → First run shows splash screen
2. **Login** with default admin credentials:
   - Username: `admin`
   - Password: `admin123`
3. **Firebase Setup** (optional):
   - Enter Firebase Database URL
   - Add API Key
   - Input Project ID
   - Test connection and save

#### Firebase Setup (Optional)
1. Create Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Enable Realtime Database
3. Enable Authentication (if using Firebase auth)
4. Get configuration details:
   - Database URL: `https://your-project.firebaseio.com/`
   - API Key: From project settings
   - Project ID: Your Firebase project ID

## 🎯 Usage

### 🔐 Login
- Use default admin credentials for initial access
- Add users through admin panel after setup
- Role-based navigation appears after login

### 📋 Adding Battery Entry
1. Tap **Add** button (FAB)
2. Fill customer information
3. Enter battery details and complaint
4. System generates QR code automatically
5. Print receipt if needed

### 🔄 Managing Repairs
1. Navigate to **Batteries** section
2. Select battery to update
3. Change status and add notes
4. Assign to technician (staff only)
5. Track progress through workflow

### 🧾 Creating Invoices
1. Go to **Billing** section
2. Select completed battery
3. Add parts and service charges
4. Calculate tax and total
5. Generate PDF invoice

### 👥 User Management (Admin)
1. Access **Users** from navigation
2. Add new users with roles
3. Manage permissions
4. Deactivate users if needed

## 🎨 UI/UX Features

- **Material Design 3** - Modern, clean interface
- **Dark Mode Support** - User preference based
- **Role-based Navigation** - Different menus per role
- **Offline Indicators** - Show when Firebase unavailable
- **Loading States** - Progress indicators for operations
- **Error Handling** - User-friendly error messages
- **Responsive Design** - Works on phones and tablets

## 🔒 Security

- **Role-based Access Control** - Granular permissions
- **Input Validation** - Prevent malicious data
- **Firebase Rules** - Server-side security
- **Local Data Encryption** - Sensitive data protection
- **Session Management** - Automatic logout

## 📱 Permissions

### Required Permissions
- **INTERNET** - Firebase communication
- **CAMERA** - QR code scanning
- **WRITE_EXTERNAL_STORAGE** - PDF file saving
- **READ_EXTERNAL_STORAGE** - File access

### Optional Permissions
- **ACCESS_NETWORK_STATE** - Network status checking

## 🔧 Build Configuration

### Debug Build
- Includes debug tools
- Detailed logging
- Testing features enabled

### Release Build
- Code obfuscation (ProGuard)
- Optimized APK size
- Production Firebase config

## 🚀 Deployment

### APK Generation
1. Build → Generate Signed Bundle/APK
2. Select APK
3. Choose release keystore
4. Build release APK

### Play Store Publishing
1. Generate App Bundle (recommended)
2. Upload to Play Console
3. Complete store listing
4. Submit for review

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

### Development Guidelines
- Follow Kotlin coding conventions
- Use meaningful commit messages
- Add documentation for new features
- Test on multiple device sizes
- Ensure role-based access works correctly

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support or questions:
- Create an issue in the repository
- Contact the development team
- Check documentation in the `docs/` folder

## 🔮 Future Enhancements

- [ ] Push notifications for task assignments
- [ ] Barcode scanning for parts inventory
- [ ] Advanced reporting and analytics
- [ ] Multi-language support
- [ ] Tablet-optimized layouts
- [ ] Integration with accounting software
- [ ] Customer portal/app
- [ ] Parts inventory management
- [ ] Warranty tracking system
- [ ] Email/SMS notifications

## 📊 Version History

### v1.0.0 (Current)
- Initial release
- Core ERP functionality
- Firebase integration
- Multi-role support
- QR code generation
- PDF receipts and invoices
- Offline admin login

---

**Built with ❤️ for battery repair shops worldwide**