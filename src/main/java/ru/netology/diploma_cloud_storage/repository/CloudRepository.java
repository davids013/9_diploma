package ru.netology.diploma_cloud_storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.diploma_cloud_storage.db.entities.FileEntity;
import ru.netology.diploma_cloud_storage.db.entities.FileId;

@Repository
public interface CloudRepository extends JpaRepository<FileEntity, FileId> {

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("UPDATE FileEntity f " +
            "SET f.id = :new_id, f.updated = current_timestamp " +
            "WHERE f.id = :old_id")
    void renameFile(@Param("old_id") FileId oldFileId, @Param("new_id") FileId newFileId);
}
