package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.dto.SignUpDto;
import CloneProject.InstagramClone.InstagramService.repository.RoleRepository;
import CloneProject.InstagramClone.InstagramService.vo.Role;
import CloneProject.InstagramClone.InstagramService.vo.UserEntity;
import CloneProject.InstagramClone.InstagramService.exception.EmailAlreadyExistsException;
import CloneProject.InstagramClone.InstagramService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(SignUpDto signUpDto) {

        if (findUser(signUpDto.getEmail())) {
            UserEntity user = setRoleToUser(signUpDto);
            userRepository.save(user);
        } else {
            throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다!");
        }
    }

    private boolean findUser(String email) {
        return userRepository.findByEmail(email) == null;
    }

    private UserEntity setRoleToUser(SignUpDto signUpDto) {
        ModelMapper modelMapper = new ModelMapper();
        UserEntity user = modelMapper.map(signUpDto, UserEntity.class);
        Role role = roleRepository.findByName("ROLE_USER");

        if (role == null) {
            role = createRole();
        }

        user.setRoles(Arrays.asList(role));
        return user;
    }

    private Role createRole() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }
}
