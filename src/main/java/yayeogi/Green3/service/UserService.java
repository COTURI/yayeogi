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
        String fullEmail = user.getEmail(); // 이메일은 이미 완전한 이메일로 입력 받는 것이 좋습니다.

        if (userRepository.findByEmail(fullEmail).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) { // 패스워드 확인 로직 추가
            throw new IllegalArgumentException("Passwords do not match.");
        }
        // 비밀번호 암호화 (선택사항: Spring Security 사용 가능)
        // user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }
}
