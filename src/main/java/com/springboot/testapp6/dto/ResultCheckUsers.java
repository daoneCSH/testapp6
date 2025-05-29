package com.springboot.testapp6.dto;

import com.springboot.testapp6.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultCheckUsers {
    boolean check;
    List<User> users;
    String message;
}
