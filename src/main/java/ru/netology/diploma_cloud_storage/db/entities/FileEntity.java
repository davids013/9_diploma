package ru.netology.diploma_cloud_storage.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int fileId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "filename")
    private FilenameEntity filename;
    @Column(length = 128, nullable = false)
    private String hash;
    @Column(nullable = false)
    private String file;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

//    public void setFilename(FilenameEntity filename) {
//        this.filename = filename;
//    }

    public FileEntity(FilenameEntity filename, String hash, String file) {
        this.filename = filename;
        this.hash = hash;
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FileEntity that = (FileEntity) o;
        return Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
