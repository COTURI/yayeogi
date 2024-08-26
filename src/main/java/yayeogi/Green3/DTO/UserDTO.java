package yayeogi.Green3.DTO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;

@Getter
@Setter
public class UserDTO {
    private String email;  // Integer로 유지
    private String birth_date;
    private String name;
    private String password;




    // Getters and Setters (Lombok으로 자동 생성)
}
