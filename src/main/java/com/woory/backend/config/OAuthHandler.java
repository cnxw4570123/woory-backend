package com.woory.backend.config;

import java.util.Map;

public interface OAuthHandler {
    String getEmail(Map<String, Object> attributes);
}
