package com.example.movieAPI.controllers;

import com.example.movieAPI.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Value("${project.poster}")
    private String path;

    @PostMapping("/upload")
    // Request part:
    public ResponseEntity<String> fileUploadHandler(@RequestPart MultipartFile file) throws IOException {
        String fileName = fileService.uploadFile(path, file);
        return ResponseEntity.ok("File uploaded" + fileName);
    }

    @GetMapping("/{fileName}")
    // Path variable
    public void serveFileHandler(@PathVariable String fileName, HttpServletResponse httpServletResponse) throws IOException {
        InputStream resourceFile = fileService.getResourceFile(path, fileName);
        httpServletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile, httpServletResponse.getOutputStream());
    }
}
