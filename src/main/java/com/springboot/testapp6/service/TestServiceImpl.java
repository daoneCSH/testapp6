package com.springboot.testapp6.service;

import com.springboot.testapp6.dao.UserDao;
import com.springboot.testapp6.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class TestServiceImpl implements TestService {
    /** Repository: 인젝션 */
    @Autowired
    UserDao dao;

    @Override
    public Iterable<User> selectAll() {
        return dao.selectAll();
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
    public boolean checkAccountPassword(String myAccount, String myPassword) throws Exception{
        return dao.checkUser(myAccount, myPassword);
    }

    @Override
    public void insert(User user) throws Exception{
        log.info("insert data:{}", user);
        dao.insertUser(user);
    }

    @Override
    public void delete(long id) throws Exception {
        dao.deleteUser(id);
    }

    @Override
    public void setDB(String dbKey) {
        com.springboot.testapp6.config.filter.DataSourceFilter.setDB(dbKey);
    }
}
