package com.lr.platform.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GoogleResponse {
    private Boolean success;

    private Float score;

    private String action;

    private String challenge_ts;

    private String hostname;
    
    @JsonProperty("error-codes")
    private List<String> errorCodes;

}
