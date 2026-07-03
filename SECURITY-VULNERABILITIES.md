# Security Vulnerabilities Documentation

This document lists all intentional security vulnerabilities added to this codebase for testing and educational purposes.

## ⚠️ WARNING
**DO NOT deploy this code to production!** This code contains severe security vulnerabilities.

## Vulnerability Summary

Total Vulnerabilities: **25**

### 1. SQL Injection (AdminController.java)
- **Location**: `AdminController.searchUsers()`
- **Severity**: CRITICAL
- **Description**: Direct string concatenation in SQL query allows SQL injection attacks
- **Example**: `username=admin' OR '1'='1`

### 2. Information Disclosure (AdminController.java)
- **Location**: `AdminController.searchUsers()` catch block
- **Severity**: HIGH
- **Description**: Exposes detailed error messages and stack traces to users

### 3. Missing Authorization (AdminController.java)
- **Location**: `AdminController.deleteUser()`
- **Severity**: CRITICAL
- **Description**: No authentication or authorization checks allow anyone to delete users

### 4. Command Injection (AdminController.java)
- **Location**: `AdminController.backupDatabase()`
- **Severity**: CRITICAL
- **Description**: User input passed directly to Runtime.exec() allows arbitrary command execution
- **Example**: `filename=backup.sql; rm -rf /`

### 5. Path Traversal (AdminController.java)
- **Location**: `AdminController.getLogs()`
- **Severity**: CRITICAL
- **Description**: No path validation allows reading arbitrary files from the filesystem
- **Example**: `logfile=../../etc/passwd`

### 6. Hardcoded Credentials (AdminController.java)
- **Location**: `AdminController` class fields
- **Severity**: CRITICAL
- **Description**: Passwords, database credentials, and API keys hardcoded in source code
- **Values**: ADMIN_PASSWORD, DB_PASSWORD, API_KEY

### 7. Insecure Deserialization (AdminController.java)
- **Location**: `AdminController.importData()`
- **Severity**: CRITICAL
- **Description**: Deserializes untrusted data without validation, allowing remote code execution

### 8. XXE (XML External Entity) (AdminController.java)
- **Location**: `AdminController.processXML()`
- **Severity**: HIGH
- **Description**: XML parser doesn't disable external entities, allows file disclosure and SSRF

### 9. Weak Cryptography (AdminController.java)
- **Location**: `AdminController.encryptData()`
- **Severity**: HIGH
- **Description**: Uses obsolete DES encryption algorithm which is easily breakable

### 10. LDAP Injection (AdminController.java)
- **Location**: `AdminController.ldapSearch()`
- **Severity**: HIGH
- **Description**: LDAP query built with string concatenation allows injection attacks
- **Example**: `username=*)(uid=*))(|(uid=*`

### 11. Insecure Random (AdminController.java)
- **Location**: `AdminController.generateToken()`
- **Severity**: MEDIUM
- **Description**: Uses java.util.Random for security-sensitive operations (predictable)

### 12. Mass Assignment (AdminController.java)
- **Location**: `AdminController.updateUser()`
- **Severity**: HIGH
- **Description**: No input validation allows users to set privileged fields (isAdmin, role)

### 13. Unrestricted File Upload (FileController.java)
- **Location**: `FileController.uploadFile()`
- **Severity**: CRITICAL
- **Description**: No file type validation, size limits, or malware scanning

### 14. Path Traversal in Download (FileController.java)
- **Location**: `FileController.downloadFile()`
- **Severity**: CRITICAL
- **Description**: Can download any file from the server
- **Example**: `filename=../../etc/passwd`

### 15. SSRF (Server-Side Request Forgery) (FileController.java)
- **Location**: `FileController.fetchURL()`
- **Severity**: CRITICAL
- **Description**: Can make requests to internal services and cloud metadata endpoints
- **Example**: `url=http://169.254.169.254/latest/meta-data/`

### 16. Open Redirect (FileController.java)
- **Location**: `FileController.redirect()`
- **Severity**: MEDIUM
- **Description**: Unvalidated redirect can be used for phishing attacks
- **Example**: `url=http://evil.com/phishing`

### 17. Arbitrary File Deletion (FileController.java)
- **Location**: `FileController.deleteFile()`
- **Severity**: CRITICAL
- **Description**: Can delete any file the application has permissions for
- **Example**: `path=C:/Windows/System32/config/sam`

### 18. ZIP Slip (FileController.java)
- **Location**: `FileController.extractZip()`
- **Severity**: CRITICAL
- **Description**: No path validation during ZIP extraction allows writing files outside target directory

### 19. Hardcoded Encryption Key (SecurityUtils.java)
- **Location**: `SecurityUtils` SECRET_KEY field
- **Severity**: CRITICAL
- **Description**: Encryption key hardcoded in source code

### 20. Weak Hashing (SecurityUtils.java)
- **Location**: `SecurityUtils.hashPassword()`
- **Severity**: CRITICAL
- **Description**: Uses MD5 for password hashing (broken algorithm, no salt)

### 21. Insecure Encryption (SecurityUtils.java)
- **Location**: `SecurityUtils.encrypt()`
- **Severity**: HIGH
- **Description**: Uses ECB mode (insecure), hardcoded key, no IV

### 22. Predictable Token (SecurityUtils.java)
- **Location**: `SecurityUtils.generateSessionToken()`
- **Severity**: HIGH
- **Description**: Session tokens are predictable (username + timestamp)

### 23. Information Leakage (SecurityUtils.java)
- **Location**: `SecurityUtils.validateCredentials()`
- **Severity**: MEDIUM
- **Description**: Error messages reveal whether username or password is wrong

### 24. No Input Sanitization (SecurityUtils.java)
- **Location**: `SecurityUtils.sanitizeInput()`
- **Severity**: HIGH
- **Description**: Method claims to sanitize but does nothing

### 25. Timing Attack (SecurityUtils.java)
- **Location**: `SecurityUtils.comparePasswords()`
- **Severity**: LOW
- **Description**: String comparison vulnerable to timing attacks

## OWASP Top 10 Coverage

This codebase includes examples of:
- ✅ A01:2021 - Broken Access Control
- ✅ A02:2021 - Cryptographic Failures
- ✅ A03:2021 - Injection
- ✅ A04:2021 - Insecure Design
- ✅ A05:2021 - Security Misconfiguration
- ✅ A06:2021 - Vulnerable and Outdated Components
- ✅ A07:2021 - Identification and Authentication Failures
- ✅ A08:2021 - Software and Data Integrity Failures
- ✅ A09:2021 - Security Logging and Monitoring Failures
- ✅ A10:2021 - Server-Side Request Forgery

## Files Modified/Created

1. `AdminController.java` - 12 vulnerabilities
2. `FileController.java` - 6 vulnerabilities
3. `SecurityUtils.java` - 7 vulnerabilities
4. `openapi-spec.yaml` - Complete API documentation with vulnerability notes

## Testing Tools

Recommended security testing tools to detect these vulnerabilities:
- SAST: SonarQube, Checkmarx, Fortify
- DAST: OWASP ZAP, Burp Suite
- Dependency Check: OWASP Dependency-Check
- Container Scanning: Trivy, Clair

## Remediation Guidelines

Each vulnerability should be fixed using industry best practices:
- Use parameterized queries for database operations
- Implement proper authentication and authorization
- Validate and sanitize all user inputs
- Use secure cryptographic algorithms (AES-GCM, bcrypt)
- Never hardcode credentials or keys
- Implement proper error handling without information disclosure
- Use security libraries and frameworks
- Regular security testing and code reviews
