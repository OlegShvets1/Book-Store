package mate.academy.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mate.academy.bookstore.validation.FieldMatch;

@Getter
@Setter
@Builder
@FieldMatch(
        firstField = "password",
        secondField = "repeatPassword",
        message = "Fields 'password' and 'repeatPassword' must match."
)
public class UserRegistrationRequestDto {

    @Email
    @NotBlank
    @NotNull
    private String email;

    @NotBlank
    @Size(min = 8, max = 50)
    @NotNull
    private String password;

    @NotBlank
    @Size(min = 8, max = 50)
    @NotNull
    private String repeatPassword;

    @NotBlank
    @NotNull
    private String firstName;

    @NotBlank
    @NotNull
    private String lastName;

    private String shippingAddress;
}
