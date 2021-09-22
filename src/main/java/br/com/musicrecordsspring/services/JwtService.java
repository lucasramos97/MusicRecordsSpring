package br.com.musicrecordsspring.services;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService implements Serializable {

  private static final long serialVersionUID = 975654534114427388L;

  private static final long TOKEN_VALIDITY = 60 * 60 * 24L;

  @Value("${jwt.secret}")
  private String jwtSecret;

  public String encode(String subject) {

    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder().setClaims(claims).setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
        .signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
  }

  public String decode(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

}
