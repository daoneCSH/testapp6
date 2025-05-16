package com.springboot.testapp6.repository;

import com.springboot.testapp6.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(String uid);

    // uid 필드에 대한 존재 여부 확인 메소드 추가
    boolean existsByUid(String uid);

    @Query(value = "SELECT id, uid, db_decrypt_full(password) as password, encode FROM tb_user WHERE id = :id", nativeQuery = true)
    Optional<User> findUserById(@Param("id") Long paramLong);

    @Modifying
    @Query(value = "INSERT INTO tb_user (uid, password) VALUES (:uid, db_encrypt_full(:password))", nativeQuery = true)
    void saveUserWithEncryption(@Param("uid") String paramString2, @Param("password") String paramString3);
}
