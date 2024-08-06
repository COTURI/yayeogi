package yayeogi.Flight;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @GetMapping
    public ModelAndView showPaymentPage() {
        return new ModelAndView("payment"); // "payment.html" 파일을 렌더링
    }
}
