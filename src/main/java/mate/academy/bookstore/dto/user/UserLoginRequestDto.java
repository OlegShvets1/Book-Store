package mate.academy.bookstore.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLoginRequestDto {
    private String email;
    private String password;
}
