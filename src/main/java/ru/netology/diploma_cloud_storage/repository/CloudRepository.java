package ru.netology.diploma_cloud_storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.diploma_cloud_storage.db.entities.FileEntity;

@Repository
public interface CloudRepository extends JpaRepository<FileEntity, String> {

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("UPDATE FileEntity f " +
            "SET f.filename = :new_name, f.updated = current_timestamp " +
            "WHERE f.filename = :old_name")
    void renameFile(@Param("old_name") String oldFilename, @Param("new_name") String newFilename);
}
