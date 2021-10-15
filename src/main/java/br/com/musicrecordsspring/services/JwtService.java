package br.com.musicrecordsspring.services;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService implements Serializable {

  private static final long serialVersionUID = 975654534114427388L;

  @Value("${jwt.secret}")
  private String jwtSecret;

  public String encode(String subject) {

    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder().setClaims(claims).setSubject(subject)
        .setExpiration(getTokenExpirationTime()).signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
  }

  public String encode(String subject, Date exp) {

    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder().setClaims(claims).setSubject(subject).setExpiration(exp)
        .signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
  }

  public String decodeSubject(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public Claims decode(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
  }

  private Date getTokenExpirationTime() {

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, 1);

    return calendar.getTime();
  }
}
