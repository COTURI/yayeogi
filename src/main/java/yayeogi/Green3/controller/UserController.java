package yayeogi.Green3.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;  // 추가
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;  // 수정된 import 문
import yayeogi.Green3.entity.User;
import yayeogi.Green3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signUp";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerNewUser(user);
            return "redirect:/register?success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signUp";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, @ModelAttribute User user, Model model) {
        try {
            User authenticatedUser = userService.authenticateUser(user.getEmail(), user.getPassword());
            HttpSession session = request.getSession();
            session.setAttribute("user", authenticatedUser);

            // 쿠키 설정 (선택 사항)
            Cookie loggedInCookie = new Cookie("loggedin", "true");
            Cookie usernameCookie = new Cookie("username", authenticatedUser.getEmail());
            loggedInCookie.setPath("/");
            usernameCookie.setPath("/");
            response.addCookie(loggedInCookie);
            response.addCookie(usernameCookie);

            return "";  // 메인 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "main";
        }
    }

    @GetMapping("/mypage")
    public String showUserPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user); // 모델에 user 객체 추가
            return "mypage"; // 실제 Thymeleaf 템플릿 파일명
        } else {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    }


    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // 세션 무효화
        return "redirect:/main";  // 로그인 페이지로 리다이렉트
    }
}
