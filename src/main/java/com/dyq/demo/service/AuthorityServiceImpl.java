package com.dyq.demo.service;

import com.dyq.demo.domain.Authority;
import com.dyq.demo.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Optional<Authority> getAuthorityById(Long id) {
        return authorityRepository.findById(id);
    }

    @Override
    public Authority saveAuthority(Authority authority) {
        return authorityRepository.save(authority);
    }

    @Override
    public void removeAuthority(Long id) {
        authorityRepository.deleteById(id);
    }

    @Override
    public Authority updateAuthority(Authority authority) {
        return authorityRepository.save(authority);
    }
}
