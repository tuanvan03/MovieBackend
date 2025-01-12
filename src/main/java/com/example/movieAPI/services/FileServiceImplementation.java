package com.example.movieAPI.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImplementation implements FileService{
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // get file's name
        String fileName = file.getOriginalFilename();

        // get path
        String filePath = path + File.separator + fileName;

        // create file object
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

        // copy file or upload file to the path
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String name) throws FileNotFoundException {
        String filePath = path + File.separator + name;
        return new FileInputStream(filePath);
    }
}
