package yayeogi.Green3.service;

import yayeogi.Green3.entity.User;
import yayeogi.Green3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerNewUser(User user) {
        String fullEmail = user.getEmail() + "@" + user.getDomain();
        user.setEmail(fullEmail);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        // 비밀번호 암호화 (선택사항: Spring Security 사용 가능)
        // user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }
}
