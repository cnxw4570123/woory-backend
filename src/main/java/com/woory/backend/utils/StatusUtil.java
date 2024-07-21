package com.woory.backend.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StatusUtil {

    public static ResponseEntity<Map<String, Object>> getPhotoSaveError() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "사진 저장 중 오류 발생");
        return ResponseEntity.badRequest().body(response);
    }

    public static Map<String, Object> getStatusMessage(String contentId) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", contentId);
        return response;
    }
    public static ResponseEntity<Map<String, String>> getResponseMessage(String message){
        Map<java.lang.String, java.lang.String> response = new HashMap<>();
        response.put("message", (java.lang.String) message);
        return ResponseEntity.ok(response);
    }



}
