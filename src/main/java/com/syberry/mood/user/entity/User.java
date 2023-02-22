package com.syberry.mood.user.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The entity representing a user in the application.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotNull
  @Column(length = 50, unique = true)
  private String username;
  @NotNull
  private String password;
  @ManyToOne
  @JoinColumn(name = "role_id", referencedColumnName = "id")
  @NotNull
  private Role role;
  @NotNull
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
  @Builder.Default
  private boolean disabled = false;
}
