package com.woory.backend.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.woory.backend.domain.TokenStatus;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JWTUtil {

	private static final String AUTHORITIES_KEY = "auth";
	private static final Logger log = LoggerFactory.getLogger(JWTUtil.class);
	private final long ACCESS_TOKEN_EXPIRE_TIME;
	private final SecretKey key;

	public JWTUtil(
		@Value("${jwt.secret}") String salt,
		@Value("${jwt.access_token_exp}") Long accessExp
	) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(salt));
		this.ACCESS_TOKEN_EXPIRE_TIME = accessExp;
	}

	public String generateAccessToken(Long userId, String authorities) {
		Date now = new Date();
		Date tokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME * 1_000);

		return Jwts.builder()
			.subject(Long.toString(userId))
			.issuedAt(new Date())
			.claim(AUTHORITIES_KEY, authorities)
			.expiration(tokenExpiresIn)
			.signWith(key, Jwts.SIG.HS512)
			.compact();
	}

	private Claims parseClaims(String accessToken) {
		try {
			return Jwts
				.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(accessToken)
				.getPayload();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public Long getUserId(String token) {
		return Long.valueOf(parseClaims(token).getSubject());
	}

	public List<? extends GrantedAuthority> getAuthorities(String token) {
		return Arrays.stream(parseClaims(token).get(AUTHORITIES_KEY).toString().split(","))
			.map(SimpleGrantedAuthority::new)
			.toList();
	}

	public long getAccTokenExpireTime() {
		return ACCESS_TOKEN_EXPIRE_TIME;
	}

	public TokenStatus validateAccessToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return TokenStatus.IS_VALID;
		} catch (SignatureException | MalformedJwtException exception) {
			log.error("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT 서명입니다.");
			return TokenStatus.IS_EXPIRED;
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 JWT 서명입니다.");
		} catch (IllegalArgumentException e) {
			log.error("JWT 토큰이 잘못 되었습니다.");
		}
		return TokenStatus.IS_NOT_VALID;

	}
}
