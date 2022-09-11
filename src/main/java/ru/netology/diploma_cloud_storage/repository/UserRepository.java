package ru.netology.diploma_cloud_storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.diploma_cloud_storage.db.entities.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);
}
