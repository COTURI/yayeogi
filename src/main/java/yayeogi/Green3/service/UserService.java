package yayeogi.Green3.service;

import yayeogi.Green3.entity.User;
import yayeogi.Green3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerNewUser(User user) {
        // 이메일을 완성합니다
        String fullEmail = user.getEmail() + "@" + user.getDomain();
        user.setEmail(fullEmail);

        // 이메일 중복 체크
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // 비밀번호 확인
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // 사용자 저장
        return userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        // 이메일로 사용자 조회
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 비밀번호 확인
            if (user.getPassword().equals(password)) {
                return user; // 인증 성공
            }
        }

        throw new IllegalArgumentException("Invalid email or password.");
    }
}
