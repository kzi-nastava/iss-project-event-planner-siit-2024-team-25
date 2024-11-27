package com.team25.event.planner.user.repository;

import com.team25.event.planner.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository  extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}
