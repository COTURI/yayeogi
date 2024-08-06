package yayeogi.Flight;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    private final KakaoPayService kakaoPayService;

    public PaymentApiController(KakaoPayService kakaoPayService) {
        this.kakaoPayService = kakaoPayService;
    }

    @PostMapping("/prepare")
    public ResponseEntity<?> preparePayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            String response = kakaoPayService.preparePayment(
                    paymentRequest.getOrderId(),
                    paymentRequest.getUserId(),
                    paymentRequest.getItemName(),
                    paymentRequest.getQuantity(),
                    paymentRequest.getTotalAmount(),
                    paymentRequest.getTaxFreeAmount()
            );
            return ResponseEntity.ok(Map.of("approvalId", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to prepare payment"));
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approvePayment(@RequestBody ApprovalRequest approvalRequest) {
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    public static class ApprovalRequest {
        private String pgToken;

        public String getPgToken() {
            return pgToken;
        }

        public void setPgToken(String pgToken) {
            this.pgToken = pgToken;
        }
    }
}
