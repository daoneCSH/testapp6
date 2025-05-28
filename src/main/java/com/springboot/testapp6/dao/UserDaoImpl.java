package com.springboot.testapp6.dao;

import com.springboot.testapp6.commons.crypo.PacketCrypto;
import com.springboot.testapp6.domain.User;
import com.springboot.testapp6.dto.ResultCheckUser;
import com.springboot.testapp6.dto.ResultCheckUsers;
import com.springboot.testapp6.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserDaoImpl implements UserDao {
    static final String keyString = "04a759baaa5e66df";

    private final UserRepository repository;

    @Autowired
    public UserDaoImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResultCheckUsers selectAll() {
        Iterable<User> list = repository.findAll();
        List<User> users = new ArrayList<>();
        for (User user : list) {
            users.add(user);
        }
        return ResultCheckUsers.builder()
                        .users(users)
                        .build();
    }

    @Transactional
    @Override
    public ResultCheckUser insertUser(User user) throws Exception {
        if (repository.existsByUserid(user.getUserid())) {
            log.error("이미 존재하는 값입니다: " + user.getUserid());
//            throw new IllegalArgumentException("이미 존재하는 값입니다: " + user.getUid());
            return ResultCheckUser.builder()
                    .check(false)
                    .message("이미 존재하는 값입니다: " + user.getUserid())
                    .build();
        }

        try {
            String txt = createTestCryptText(user.getUserid(), user.getPassword());
            log.info("insertUser userid:{} pw:{} txt:{}", user.getUserid(), user.getPassword(), txt);
            repository.saveUserWithEncryption(user.getUserid(), user.getPassword(), txt);
            return ResultCheckUser.builder()
                    .check(true)
                    .user(repository.findByUserid(user.getUserid()).orElse(null))
                    .build();
        } catch (Exception e) {
            log.error("🚨 insertUser 중 오류 발생: {}", e.getMessage());
            return ResultCheckUser.builder()
                    .check(false)
                    .message(String.format("🚨 insertUser 중 오류 발생: {}", e.getMessage()))
                    .build();
        }

    }

    String createTestCryptText(String s1,  String s2){
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 20) {
            sb.append(s1);
            sb.append(s2);
        }
        return sb.toString();
    }


    @Override
    public User selectUser(Long id) {
        Optional<User> op = repository.findById(id);
        return op.orElse(null);
    }

    @Override
    public User findUserByKey(String key) {
        Optional<User> op = repository.findByUserid(key);
        return op.orElse(null);
    }

    @Override
    public ResultCheckUser checkUser(String username, String myPassword) throws Exception {
        ResultCheckUser result = new ResultCheckUser();

//        Optional<User> u = repository.findByUid(username);

        Optional<User> u1 = repository.findByUserid(username);
        if (!u1.isPresent()) {
            log.info("checkUser [{}]의 유저를 찾을 수 없다.", username);
            result.setCheck(false);
            return result;
        }
        Optional<User> u = repository.findUserById(u1.get().getId());

        if (!u.isPresent()) {
            log.info("checkUser [{}]의 유저의 정보를 찾을 수 없다. (검색된 ID는 [{}])", username, u1.get().getId());
            result.setCheck(false);
            return result;
        }

        String dep;
        dep = u.get().getPassword();
        result.setUser(u.get());
        log.info("checkAccountPassword name:{} input:{} Decode:{} Decode2:{}", username, myPassword, dep, u.get().getTesttext());
        result.setCheck(dep.equals(myPassword));
        return result;
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        Optional<User> op = repository.findById(id);
        if (op.isPresent()) {
            User p = op.get();
            repository.delete(p);
        } else {
            throw new Exception();
        }
    }
}
