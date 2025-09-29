package com.jupiter.chatweb.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelRequest {
    private String usertoken;
    private String gender;
    private String layers;
}
