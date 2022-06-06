package ru.netology.diploma_cloud_storage.db.entities;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity(name = "file_storage")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {

    @Transient
    public static final String FILE_BYTES_SEPARATOR = " ";
    @Transient
    public static final String DB_NAME = "file_storage";

    @Id
    @Column(length = 64, nullable = false, unique = true)
    private String filename;
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

    public FileEntity(String filename, String hash, String file) {
        this.filename = filename;
        this.hash = hash;
        this.file = file;
    }

    public FileEntity(FileEntity another) {
        this.filename = another.getFilename();
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
        return Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
