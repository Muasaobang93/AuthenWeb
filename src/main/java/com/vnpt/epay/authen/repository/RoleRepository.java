package com.vnpt.epay.authen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vnpt.epay.authen.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String name);

}