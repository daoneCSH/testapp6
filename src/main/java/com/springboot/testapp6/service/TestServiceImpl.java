package com.springboot.testapp6.service;

import com.springboot.testapp6.config.DataSourceConfig;
import com.springboot.testapp6.config.DynamicDataSource;
import com.springboot.testapp6.config.InitializerDatabase;
import com.springboot.testapp6.dao.UserDao;
import com.springboot.testapp6.domain.User;
import com.springboot.testapp6.dto.ResultCheckUser;
import com.springboot.testapp6.dto.ResultCheckUsers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.SQLException;
import java.util.Collections;

@Slf4j
@Service
@Transactional
public class TestServiceImpl implements TestService {
    /** Repository: 인젝션 */
    @Autowired
    UserDao dao;

    @Autowired
    private InitializerDatabase initializer;

    @Transactional(noRollbackFor = Exception.class)
    @Override
    public ResultCheckUsers selectAll() throws SQLException {
        try {
            ResultCheckUsers result = dao.selectAll();
            return ResultCheckUsers.builder()
                    .check(true)
                    .users(result.getUsers())
                    .build();
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String mgs = String.format("테이블이 없거나 접근 불가: %s", e.getMessage());
            log.error(mgs);
            initializer.ensureTableExistsOrRecreate();
            return ResultCheckUsers.builder()
                    .check(false)
                    .users(Collections.emptyList())
                    .message(mgs)
                    .build();
        }
    }

    @Override
    public User selectById(long id) {
        return dao.selectUser(id);
    }

    @Override
    public long selectByKey(String myAccount) {
        User u = dao.findUserByKey(myAccount);
        return u.getId();
    }

    @Override
    public ResultCheckUser checkAccountPassword(String myAccount, String myPassword) throws Exception{
        return dao.checkUser(myAccount, myPassword);
    }

    @Override
    public ResultCheckUser insert(User user) throws Exception{
        log.info("insert data:{}", user);
        return dao.insertUser(user);
    }

    @Override
    public void delete(long id) throws Exception {
        dao.deleteUser(id);
    }

    @Override
    public void setDB(String dbKey) {
        com.springboot.testapp6.config.DynamicDataSource.setDataSourceKey(dbKey);
    }
}
