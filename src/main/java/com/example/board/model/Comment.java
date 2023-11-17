package com.example.board.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Comment {
  @Id
  @GeneratedValue
  long id;
  String comment;

  @ManyToOne
  Board board;

  @ManyToOne
  User user;
}
