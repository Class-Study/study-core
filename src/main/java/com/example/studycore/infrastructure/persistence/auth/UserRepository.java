package com.example.studycore.infrastructure.persistence.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmailAndRoleIgnoreCase(String email, String role);

    Optional<UserEntity> findByEmailIgnoreCaseAndRole(String email, String role);

    List<UserEntity> findByRole(String role, Sort sort);

    @Query("""
            select u from UserEntity u
                         where u.role = :role and
                         (lower(u.name) 
                          like lower(concat('%', :q, '%')) or lower(u.email) 
                          like lower(concat('%', :q, '%'))
                                      and u.role = :role)
            """)
    List<UserEntity> searchByRoleAndNameOrEmail(
            @Param("role") String role,
            @Param("q") String q,
            Sort sort
    );
}

