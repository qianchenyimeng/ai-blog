package com.blog.repository;

import com.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名或邮箱查找用户
     * @param username 用户名
     * @param email 邮箱
     * @return 用户对象
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查用户名是否存在（排除指定用户ID）
     * @param username 用户名
     * @param userId 要排除的用户ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :userId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("userId") Long userId);

    /**
     * 检查邮箱是否存在（排除指定用户ID）
     * @param email 邮箱
     * @param userId 要排除的用户ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Long userId);

    /**
     * 根据启用状态查找用户
     * @param enabled 启用状态
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.enabled = :enabled ORDER BY u.createdAt DESC")
    java.util.List<User> findByEnabledOrderByCreatedAtDesc(@Param("enabled") Boolean enabled);

    /**
     * 统计用户总数
     * @return 用户总数
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countUsers();

    /**
     * 统计启用的用户数量
     * @return 启用的用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countEnabledUsers();
}