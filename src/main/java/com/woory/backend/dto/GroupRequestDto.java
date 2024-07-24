package com.woory.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupRequestDto {
    @JsonProperty("name")
    private String groupName;
    private String images;
}
