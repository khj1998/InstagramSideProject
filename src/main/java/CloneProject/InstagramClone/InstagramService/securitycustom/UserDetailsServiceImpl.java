package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.repository.UserRepository;
import CloneProject.InstagramClone.InstagramService.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("가입되지 않은 유저입니다.");
        }
        return user;
    }
}