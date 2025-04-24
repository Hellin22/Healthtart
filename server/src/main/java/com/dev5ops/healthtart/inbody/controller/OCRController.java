package com.dev5ops.healthtart.inbody.controller;

import com.dev5ops.healthtart.inbody.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocr")
public class OCRController {

    @Autowired
    private VisionService visionService;

    @GetMapping("/extract-text")
    public String extractText(@RequestParam("fileName") String fileName) {
        try {
            return visionService.extractTextFromGCSImage(fileName);
        } catch (Exception e) {
            return "Failed to extract text: " + e.getMessage();
        }
    }
}

