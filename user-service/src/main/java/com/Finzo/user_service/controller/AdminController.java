package com.Finzo.user_service.controller;

import com.Finzo.user_service.model.User;
import com.Finzo.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // VULNERABILITY 1: SQL Injection - Direct string concatenation
    @GetMapping("/users/search")
    public String searchUsers(@RequestParam String username) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finzo", "root", "password123");
            Statement stmt = conn.createStatement();
            // SQL Injection vulnerability
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append(rs.getString("username")).append(",");
            }
            return result.toString();
        } catch (Exception e) {
            // VULNERABILITY 2: Exposing sensitive error information
            return "Error: " + e.getMessage() + " - " + e.getStackTrace()[0];
        }
    }

    // VULNERABILITY 3: Missing authentication/authorization check
    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "User deleted successfully";
    }

    // VULNERABILITY 4: Command Injection
    @PostMapping("/backup")
    public String backupDatabase(@RequestParam String filename) {
        try {
            // Command injection vulnerability
            Runtime.getRuntime().exec("mysqldump -u root -ppassword123 finzo > " + filename);
            return "Backup created: " + filename;
        } catch (Exception e) {
            return "Backup failed: " + e.getMessage();
        }
    }

    // VULNERABILITY 5: Path Traversal
    @GetMapping("/logs")
    public String getLogs(@RequestParam String logfile) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("/var/logs/" + logfile);
            return new String(java.nio.file.Files.readAllBytes(path));
        } catch (Exception e) {
            return "Error reading log: " + e.getMessage();
        }
    }

    // VULNERABILITY 6: Hardcoded credentials
    private static final String ADMIN_PASSWORD = "Admin@123456";
    private static final String DB_PASSWORD = "password123";
    private static final String API_KEY = "sk_live_51HxYzAbcdefghijklmnop";

    @PostMapping("/admin-login")
    public String adminLogin(@RequestParam String password) {
        if (password.equals(ADMIN_PASSWORD)) {
            return "Admin access granted. API Key: " + API_KEY;
        }
        return "Access denied";
    }

    // VULNERABILITY 7: Insecure deserialization
    @PostMapping("/import-data")
    public String importData(@RequestBody String serializedData) {
        try {
            byte[] data = java.util.Base64.getDecoder().decode(serializedData);
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                new java.io.ByteArrayInputStream(data)
            );
            Object obj = ois.readObject();
            return "Data imported: " + obj.toString();
        } catch (Exception e) {
            return "Import failed: " + e.getMessage();
        }
    }

    // VULNERABILITY 8: XXE (XML External Entity) vulnerability
    @PostMapping("/process-xml")
    public String processXML(@RequestBody String xmlData) {
        try {
            javax.xml.parsers.DocumentBuilderFactory factory = 
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
            // XXE vulnerability - external entities not disabled
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(
                new java.io.ByteArrayInputStream(xmlData.getBytes())
            );
            return "XML processed successfully";
        } catch (Exception e) {
            return "XML processing failed: " + e.getMessage();
        }
    }

    // VULNERABILITY 9: Weak cryptography
    @PostMapping("/encrypt")
    public String encryptData(@RequestParam String data) {
        try {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES");
            javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("DES");
            javax.crypto.SecretKey key = keyGen.generateKey();
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return java.util.Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return "Encryption failed: " + e.getMessage();
        }
    }

    // VULNERABILITY 10: LDAP Injection
    @GetMapping("/ldap-search")
    public String ldapSearch(@RequestParam String username) {
        try {
            javax.naming.directory.DirContext ctx = new javax.naming.directory.InitialDirContext();
            String filter = "(uid=" + username + ")";
            javax.naming.directory.SearchControls controls = new javax.naming.directory.SearchControls();
            javax.naming.NamingEnumeration<?> results = ctx.search("ou=users,dc=finzo,dc=com", filter, controls);
            return "LDAP search completed";
        } catch (Exception e) {
            return "LDAP search failed: " + e.getMessage();
        }
    }

    // VULNERABILITY 11: Insecure random number generation
    @GetMapping("/generate-token")
    public String generateToken() {
        java.util.Random random = new java.util.Random();
        long token = random.nextLong();
        return "Token: " + token;
    }

    // VULNERABILITY 12: Mass assignment vulnerability
    @PutMapping("/users/update")
    public String updateUser(@RequestBody User user) {
        // No validation - allows setting any field including isAdmin, role, etc.
        userService.updateUser(user);
        return "User updated";
    }
}
