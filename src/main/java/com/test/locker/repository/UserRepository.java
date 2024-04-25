package com.test.locker.repository;

import com.test.locker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    User getById(Integer userId);
}
