package yayeogi.Green3.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(length = 50, nullable = false)
    private String email;

    @Transient
    private String domain; // transient 필드는 데이터베이스에 저장되지 않습니다.

    @Column(length = 100, nullable = false)
    private String password;

    @Transient
    private String passwordConfirm; // transient 필드는 데이터베이스에 저장되지 않습니다.

    @Column(length = 30, nullable = false)
    private String name;

    @Column(nullable = false)
    private String birthDate;
}
