package com.vnpt.epay.authen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vnpt.epay.authen.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String user);

}