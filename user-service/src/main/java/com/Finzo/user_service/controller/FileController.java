package com.Finzo.user_service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

    // VULNERABILITY 13: Unrestricted file upload
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // No file type validation, size check, or virus scanning
            String uploadDir = "C:/uploads/";
            String filename = file.getOriginalFilename();
            File dest = new File(uploadDir + filename);
            file.transferTo(dest);
            return "File uploaded: " + filename;
        } catch (Exception e) {
            return "Upload failed: " + e.getMessage();
        }
    }

    // VULNERABILITY 14: Directory traversal in file download
    @GetMapping("/download")
    public void downloadFile(@RequestParam String filename, HttpServletResponse response) {
        try {
            // Path traversal vulnerability
            File file = new File("C:/uploads/" + filename);
            FileInputStream fis = new FileInputStream(file);
            
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            fis.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // VULNERABILITY 15: SSRF (Server-Side Request Forgery)
    @GetMapping("/fetch-url")
    public String fetchURL(@RequestParam String url) {
        try {
            URL urlObj = new URL(url);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(urlObj.openStream())
            );
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            return "Failed to fetch URL: " + e.getMessage();
        }
    }

    // VULNERABILITY 16: Open redirect
    @GetMapping("/redirect")
    public void redirect(@RequestParam String url, HttpServletResponse response) {
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // VULNERABILITY 17: Arbitrary file deletion
    @DeleteMapping("/delete")
    public String deleteFile(@RequestParam String path) {
        try {
            File file = new File(path);
            if (file.delete()) {
                return "File deleted: " + path;
            }
            return "Failed to delete file";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // VULNERABILITY 18: ZIP slip vulnerability
    @PostMapping("/extract-zip")
    public String extractZip(@RequestParam("file") MultipartFile zipFile) {
        try {
            java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(
                zipFile.getInputStream()
            );
            java.util.zip.ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                // No path validation - allows writing outside target directory
                String filePath = "C:/extracted/" + entry.getName();
                FileOutputStream fos = new FileOutputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zis.close();
            return "ZIP extracted successfully";
        } catch (Exception e) {
            return "Extraction failed: " + e.getMessage();
        }
    }
}
