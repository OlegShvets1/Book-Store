package mate.academy.bookstore.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.exception.RegistrationException;
import mate.academy.bookstore.mapper.UserMapper;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: "
                    + requestDto.getEmail() + " already exist");
        }
        User newUser = userMapper.mapToModel(requestDto);
        newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        return userMapper.mapToDto(userRepository.save(userMapper.mapToModel(requestDto)));
    }
}
