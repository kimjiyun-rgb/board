package com.example.board.email;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

public class SMTPAuthenticator extends Authenticator {
  @Override
  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(
        "faren02134@gmail.com", "yofcjhfncvmksbuj");
  }
}