package com.team25.event.planner.user.repository;

import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Modifying
    @Query("delete from User where id in :ids")
    void deleteAllByIds(Collection<Long> ids);

    List<User> findByUserRole(UserRole userRole);

}
