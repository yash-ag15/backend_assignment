package com.yash.BackendAssignment.repos;

import com.yash.BackendAssignment.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository  extends JpaRepository<Post,Long> {
}
