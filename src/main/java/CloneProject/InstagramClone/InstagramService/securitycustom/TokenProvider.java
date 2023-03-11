package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.config.SpringConst;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class TokenProvider implements InitializingBean {
    private Key access_key;
    private Key refresh_key;

    @Override
    public void afterPropertiesSet() {
        byte[] accessKeyBytes = Base64.getDecoder().decode(SpringConst.ACCESS_SECRET_KEY);
        byte[] refreshKeyBytes = Base64.getDecoder().decode(SpringConst.REFRESH_SECRET_KEY);
        this.access_key = Keys.hmacShaKeyFor(accessKeyBytes);
        this.refresh_key = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(this.access_key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }

    /**
     * Generate jwt token
     */
    public String generateAccessToken(UserEntity userEntity) {
        Claims claims = Jwts.claims().setSubject(userEntity.getUsername());
        claims.put("roles",userEntity.getAuthorities());

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SpringConst.ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(this.access_key, SignatureAlgorithm.ES256)
                .compact();
    }

    public String generateRefreshToken(UserEntity userEntity) {
        Claims claims = Jwts.claims().setSubject(userEntity.getUsername());
        claims.put("roles",userEntity.getAuthorities());

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SpringConst.REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(this.refresh_key,SignatureAlgorithm.ES256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }
}
