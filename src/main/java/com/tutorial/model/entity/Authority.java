package com.tutorial.model.entity;

import com.tutorial.Enum.AuthorityEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
