package com.tutorial.model.entity;

import com.tutorial.Enum.AuthorityEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorities", indexes = {
        @Index(name = "ix_auth_username", columnList = "username,authority", unique = true)
})
@IdClass(AuthorityId.class)
public class Authority {

    @Id
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false, length = 50)
    private AuthorityEnum authority = AuthorityEnum.USER;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority)) return false;
        Authority that = (Authority) o;
        return Objects.equals(username, that.username) &&
                authority == that.authority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authority);
    }

}
