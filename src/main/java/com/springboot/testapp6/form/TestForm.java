package com.springboot.testapp6.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestForm {
    /** 식별 ID */
    public long id;

    /** 계정명 (암호화 되지 않음) */
    @NotBlank
    private String uid;

    /** 암호 (암호화 처리되어 저장) */
    @NotBlank
    private String password;
    
    /** 확인인가 (f:생성 t:확인) */
    private Boolean isLogin;
}
