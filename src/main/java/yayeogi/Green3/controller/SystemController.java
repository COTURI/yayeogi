package yayeogi.Green3.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SystemController {


    @GetMapping("/slogin")
    public String slogin(){
        return "systemLogin";
    }

    @PostMapping("/slogin")
    public String showSystem(HttpServletRequest request, @RequestParam String username, @RequestParam String password, Model model) {
        if ("system".equals(username) && "1234".equals(password)) {
            return "redirect:/system"; // 관리자 페이지로 매핑해서 변경 해야됨
        } else {
            model.addAttribute("errorMessage", "아이디나 비밀번호가 올바르지 않습니다.");
            return "systemlogin"; // 로그인 페이지로 다시 이동
        }
    }

    @GetMapping("/system")
    public String adminDashboard() {
        return "system"; // 관리자 페이지 뷰
    }
}
