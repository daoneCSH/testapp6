package com.springboot.testapp6.service;

import com.springboot.testapp6.domain.User;
import com.springboot.testapp6.dto.ResultCheckUser;
import com.springboot.testapp6.dto.ResultCheckUsers;

import java.sql.SQLException;

/** test 서비스 : Service  */
public interface TestService {
    /** 모든 데이터 가져오기 */
    ResultCheckUsers selectAll() throws SQLException;

    /** id로 데이터 가져오기 */
    User selectById(long id);

    /** key로 id 가져오기 */
    long selectByKey(String key);

    /** 계정 정보와 암호 확인 */
    ResultCheckUser checkAccountPassword(String myAccount, String myPassword) throws Exception;

    /** 계정 등록 */
    ResultCheckUser insert(User data) throws Exception;

    /** 계정 삭제 */
    void delete(long id) throws Exception;

    void setDB(String dbKey);
}
