package com.resume.backend.AI_resume.backend.controller;

import com.resume.backend.AI_resume.backend.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/getResponse")
@CrossOrigin
public class prompt {
    @Autowired
    PromptService promptService;



    @PostMapping("/prompt")
    public Map<String, Object> generateResumeResponse(@RequestBody Map<String, String> request) throws IOException {
        String prompt = request.get("userDescription");
        return promptService.generateResumeResponse(prompt).toMap();
    }



}
