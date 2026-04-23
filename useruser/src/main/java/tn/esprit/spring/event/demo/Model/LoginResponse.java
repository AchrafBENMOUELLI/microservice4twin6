package tn.esprit.spring.event.demo.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String message;
    private boolean success;
}
