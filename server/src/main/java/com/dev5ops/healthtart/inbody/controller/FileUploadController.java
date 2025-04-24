package com.dev5ops.healthtart.inbody.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.*;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${cloud.gcp.bucket-name}")
    private String bucketName;

    @Value("${cloud.gcp.project-id}")
    private String projectId;

    @PostMapping("/image")
    public String uploadImageToGCS(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            storage.create(blobInfo, file.getBytes());

            URL imageUrl = new URL("https://storage.googleapis.com/" + bucketName + "/" + fileName);
            return imageUrl.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload image: " + e.getMessage();
        }
    }
}
