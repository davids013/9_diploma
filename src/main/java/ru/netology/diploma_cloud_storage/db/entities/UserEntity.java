package ru.netology.diploma_cloud_storage.db.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.netology.diploma_cloud_storage.domain.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Transient
    public static final String DB_NAME = "users";

    @Id
    @Column(length = 64, nullable = false, unique = true, updatable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date registered;

    public UserEntity (String login, String password) {
        this.login = login;
        this.password = password;
    }

    public UserEntity (User user) {
        login = user.getLogin();
        password = user.getPassword();
    }
}
