package com.dev5ops.healthtart.user.domain.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseEmailMessageVO {

    @JsonProperty("message")
    private String message;
}
