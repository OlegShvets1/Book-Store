package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.exception.RegistrationException;
import mate.academy.bookstore.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User authentication", description = "Authentication a new user")
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
@Validated
@RestController
public class AuthenticationController {
    private final UserService userService;

    @Operation(summary = "User registration",
            description = "register a new user")
    @PostMapping(value = "/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
