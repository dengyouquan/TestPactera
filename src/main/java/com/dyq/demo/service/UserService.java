package com.dyq.demo.service;

import com.dyq.demo.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    List<User> findAll(Sort sort);
    Page<User> findAll(Pageable pageable);
    User saveUser(User user);
    void removeUser(Long id);
    /**
     * 删除列表里面的用户
     * @param users
     * @return
     */
    void removeUsersInBatch(List<User> users);
    User updateUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getUsers();
    Page<User> getUsersByNameLike(String name, Pageable pageable);
    List<User> getUsersByUsernames(Collection<String> usernames);
    User getUserByUsername(String username);
    Optional<User> findByTel(String tel);
    Optional<User> findByEmail(String email);
    User findByUsername(String username);

}
