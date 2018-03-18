package com.dyq.demo.service;

import com.dyq.demo.domain.Authority;

import java.util.Optional;

public interface AuthorityService {
    /**
     * 根据id获取 Authority
     * @param Authority
     * @return
     */
    Optional<Authority> getAuthorityById(Long id);
    Authority saveAuthority(Authority authority);
    void removeAuthority(Long id);
    Authority updateAuthority(Authority authority);
}
