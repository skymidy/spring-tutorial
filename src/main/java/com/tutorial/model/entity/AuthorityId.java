package com.tutorial.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.tutorial.Enum.AuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityId implements Serializable {

  private String username;
  private AuthorityEnum authority;
}