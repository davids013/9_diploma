package ru.netology.diploma_cloud_storage.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FilenameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int filenameId;
    @Column(length = 64, nullable = false, unique = true)
    private String filename;

    public FilenameEntity(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FilenameEntity that = (FilenameEntity) o;
        return Objects.equals(filenameId, that.filenameId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
