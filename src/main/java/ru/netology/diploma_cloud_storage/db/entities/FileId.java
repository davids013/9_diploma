package ru.netology.diploma_cloud_storage.db.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileId implements Serializable {
    private static final long serialVersionUID = 5783442874014148356L;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_login")
    private UserEntity owner;
    private String filename;
}
