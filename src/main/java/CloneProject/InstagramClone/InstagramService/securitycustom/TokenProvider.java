package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.config.SpringConst;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {
    private Key access_key;
    private Key refresh_key;
    private final UserDetailsService userDetailsService;

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

    public String extractUsernameByRefresh(String token) {
        return extractRefreshClaim(token,Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(this.access_key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }

    private <T> T extractRefreshClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(this.refresh_key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }

    /**
     * Generate jwt token
     */
    public String generateAccessToken(Member member) {
        Claims claims = Jwts.claims().setSubject(member.getUsername());
        claims.put("roles", member.getAuthorities());

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SpringConst.ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(this.access_key)
                .compact();
    }

    public String generateRefreshToken(Member member) {
        Claims claims = Jwts.claims().setSubject(member.getUsername());
        claims.put("roles", member.getAuthorities());

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SpringConst.REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(this.refresh_key)
                .compact();
    }

    public boolean isTokenValid(String token) {
        final String username = extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isRefreshTokenValid(String token) {
        final String username = extractUsernameByRefresh(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return (username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private boolean isRefreshTokenExpired(String refreshToken) {
        return extractRefreshExpiration(refreshToken).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    private Date extractRefreshExpiration(String refreshToken) {
        return extractRefreshClaim(refreshToken,Claims::getExpiration);
    }
}
