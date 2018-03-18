package com.dyq.demo.repository;

import com.dyq.demo.domain.Authority;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface AuthorityRepository extends JpaRepository<Authority,Long> {
}
