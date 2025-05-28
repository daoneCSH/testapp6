package com.springboot.testapp6.dao;

import com.springboot.testapp6.domain.User;
import com.springboot.testapp6.dto.ResultCheckUser;
import com.springboot.testapp6.dto.ResultCheckUsers;

public interface UserDao {
    ResultCheckUsers selectAll();
    ResultCheckUser insertUser(User user) throws Exception;
    User selectUser(Long id);
    User findUserByKey(String key);
    ResultCheckUser checkUser(String username, String password) throws Exception;
    void deleteUser(Long id) throws Exception;

}
