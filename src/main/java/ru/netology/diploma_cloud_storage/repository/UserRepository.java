package ru.netology.diploma_cloud_storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.diploma_cloud_storage.db.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    boolean existsByLogin(String login);
    UserEntity findByLogin(String login);
}
