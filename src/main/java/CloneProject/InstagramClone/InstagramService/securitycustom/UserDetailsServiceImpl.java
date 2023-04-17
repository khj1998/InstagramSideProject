package CloneProject.InstagramClone.InstagramService.securitycustom;

import CloneProject.InstagramClone.InstagramService.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("UserNotFoundException occurred"));
    }
}