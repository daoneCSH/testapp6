package com.springboot.testapp6.dto;

import com.springboot.testapp6.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultCheckUser {
    boolean check;
    User user;
    String message;
}
