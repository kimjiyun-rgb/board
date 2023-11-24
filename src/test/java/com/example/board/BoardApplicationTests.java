package com.example.board;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class BoardApplicationTests {

	@Test
	void stringToBase64() {
		String text = "jwt test";
		String encodedString = Base64.getEncoder().encodeToString(text.getBytes());
		log.info("encodedString {}", encodedString);
	}

	@Test
	void createJwt() {

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		// 문자 1개 = 6bit, 24bit(문자 4개) = 1개 단위
		// 240bit 보다 큰 값의 secretKey 사용 => 44개 이상의 문자 필요
		byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(
				"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqr");
		Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());
		JwtBuilder builder = Jwts.builder()
				// Header
				.setHeaderParam("typ", "JWT")
				// Payload - Registered Claim
				.setSubject("제목").setIssuer("kimjiyun-rgb.com").setAudience("service user")
				// Payload - Secret Claim
				.claim("username", "kimjiyun-rgb").claim("password", 1234).claim("hasPermission", "ADMIN")
				// Signature
				.signWith(signingKey, signatureAlgorithm);
		long now = System.currentTimeMillis();
		builder.setExpiration(new Date(now + 3600000)); // 1시간 뒤 토큰 유효기간 만료
		String token = builder.compact();
		log.info("jwt {}", token);
	}
}
