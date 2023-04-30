package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.config.SpringConst;
import CloneProject.InstagramClone.InstagramService.entity.member.Member;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtExpiredException;
import CloneProject.InstagramClone.InstagramService.exception.jwt.JwtIllegalException;
import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenService implements InitializingBean {
    private Key access_key;
    private Key refresh_key;
    private final UserDetailsService userDetailsService;
    private final MemberRepository memberRepository;

    @Override
    public void afterPropertiesSet() {
        byte[] accessKeyBytes = Base64.getDecoder().decode(SpringConst.ACCESS_SECRET_KEY);
        byte[] refreshKeyBytes = Base64.getDecoder().decode(SpringConst.REFRESH_SECRET_KEY);
        this.access_key = Keys.hmacShaKeyFor(accessKeyBytes);
        this.refresh_key = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public String ExtractTokenFromReq(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader.isBlank() || !authorizationHeader.startsWith("Bearer ")) {
            throw new JwtIllegalException("인증 토큰이 유효하지 않습니다.");
        }

        return authorizationHeader.substring(7);
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

    public Member FindMemberByToken(String accessToken) {
        try {
            String email = extractUsername(accessToken);
            return memberRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException occurred"));
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("AccessToken Expired");
        }
    }
}
