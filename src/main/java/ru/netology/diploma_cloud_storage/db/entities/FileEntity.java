package ru.netology.diploma_cloud_storage.db.entities;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "file_storage")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {

    @Transient
    public static final String FILE_BYTES_SEPARATOR = " ";

    @EmbeddedId
    private FileId id;
    @Column(nullable = false)
    private String hash;
    @Column(nullable = false)
    private String file;
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updated;

    public FileEntity(FileId id, String hash, String file) {
        this.id = id;
        this.hash = hash;
        this.file = file;
    }

    public FileEntity(FileEntity another) {
        this.id = another.getId();
        this.hash = another.getHash();
        this.file = another.getFile();
        this.created = another.getCreated();
        this.updated = another.getUpdated();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FileEntity that = (FileEntity) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
