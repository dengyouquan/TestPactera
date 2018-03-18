package com.dyq.demo.service;

import com.dyq.demo.domain.User;
import com.dyq.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService,UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAll(Sort sort) {
        return userRepository.findAll(sort);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void removeUsersInBatch(List<User> users) {
        userRepository.deleteInBatch(users);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getUsersByNameLike(String name, Pageable pageable) {
        // 模糊查询
        name = "%" + name + "%";
        Page<User> users = userRepository.findByNameLike(name, pageable);
        return users;
    }

    @Override
    public List<User> getUsersByUsernames(Collection<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByTel(String tel) {
        return userRepository.findByTel(tel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByTel(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            //用邮箱尝试
            User emailUser = userRepository.findByEmail(username).orElse(null);//user.orElse(null);  //而不是 user.isPresent() ? user.get() : null;
            if (emailUser == null) {
                //用手机号尝试
                User phoneUser = userRepository.findByTel(username).orElse(null);
                if(phoneUser==null){
                    throw new UsernameNotFoundException("用户不存在");
                }else{
                    System.out.println("手机号密码登录》phoneUser:"+phoneUser);
                    return new org.springframework.security.core.userdetails.User(phoneUser.getTel(), phoneUser.getPassword(), phoneUser.isEnabled(), phoneUser.isAccountNonExpired(), phoneUser.isCredentialsNonExpired(), phoneUser.isAccountNonLocked()
                            ,phoneUser.getAuthorities());
                }
            } else {
                System.out.println("邮箱号密码登录》emailUser:"+emailUser);
                return new org.springframework.security.core.userdetails.User(emailUser.getEmail(), emailUser.getPassword(), emailUser.isEnabled(), emailUser.isAccountNonExpired(), emailUser.isCredentialsNonExpired(), emailUser.isAccountNonLocked()
                        ,emailUser.getAuthorities());
            }
        } else {
            System.out.println("用户名密码登录》user:"+user);
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked()
                    ,user.getAuthorities());
        }
        //return user;
    }
}
