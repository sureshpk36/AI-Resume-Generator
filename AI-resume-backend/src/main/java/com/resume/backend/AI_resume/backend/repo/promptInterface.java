package com.resume.backend.AI_resume.backend.repo;

import org.json.JSONObject;

import java.io.IOException;

public interface promptInterface {
    JSONObject generateResumeResponse(String prompt) throws IOException;

}
