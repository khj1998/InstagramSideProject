package CloneProject.InstagramClone.InstagramService.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    public String extractUsername(String token);
    public boolean isTokenValid(String token, UserDetails userDetails);
    public String generateToken(UserDetails userDetails);
}
