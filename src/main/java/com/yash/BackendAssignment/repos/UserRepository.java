package com.yash.BackendAssignment.repos;

import com.yash.BackendAssignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
