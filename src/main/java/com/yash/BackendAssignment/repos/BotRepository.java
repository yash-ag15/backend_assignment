package com.yash.BackendAssignment.repos;

import com.yash.BackendAssignment.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotRepository extends JpaRepository<Bot,Long> {
}
