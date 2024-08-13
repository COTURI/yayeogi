package yayeogi.payment;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @GetMapping("/api/check-login")
    @ResponseBody
    public Map<String, Object> checkLogin(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        boolean loggedIn = session.getAttribute("accessToken") != null; // 세션에 accessToken이 있는지 확인
        response.put("loggedIn", loggedIn);
        return response;
    }
}
