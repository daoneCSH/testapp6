package com.springboot.testapp6.repository;

import com.springboot.testapp6.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT id, userid, password, testtext FROM tb_user", nativeQuery = true)
    Iterable<User> selectAll();

    Optional<User> findByUserid(String uid);

    // uid 필드에 대한 존재 여부 확인 메소드 추가
    boolean existsByUserid(String uid);

    @Query(value = "SELECT id, userid, db_decrypt_full(password) as password, db_decrypt(testtext) as testtext FROM tb_user WHERE id = :id", nativeQuery = true)
    Optional<User> findUserById(@Param("id") Long paramLong);

    @Modifying
    @Query(value = "INSERT INTO tb_user (userid, password, testtext) VALUES (:userid, db_encrypt_full(:password), db_encrypt(:testtext))", nativeQuery = true)
    void saveUserWithEncryption(@Param("userid") String userid, @Param("password") String password, @Param("testtext") String testtext);
}
