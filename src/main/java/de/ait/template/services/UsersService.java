package de.ait.template.services;

import de.ait.template.dto.NewUserDto;
import de.ait.template.dto.StandardResponseDto;
import de.ait.template.dto.UserDto;
import de.ait.template.exceptions.RestException;
import de.ait.template.mail.MailTemplatesUtil;
import de.ait.template.mail.TemlateProjectMailSender;
import de.ait.template.models.ConfirmationCode;
import de.ait.template.models.User;
import de.ait.template.repositories.ConfirmationCodsRepository;
import de.ait.template.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.UUID;

import static de.ait.template.dto.UserDto.from;


@RequiredArgsConstructor
@Service
@Slf4j
public class UsersService {

    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    private final ConfirmationCodsRepository confirmationCodsRepository;

    private final TemlateProjectMailSender mailSender;

    private final MailTemplatesUtil mailTemplatesUtil;

    @Value("${base.url}")
    private String baseurl;

    @Transactional
    public UserDto register(NewUserDto newUser) {
        log.info("Current thread for user registration " + Thread.currentThread().getName());
        checkIfExistByEmail(newUser);

        User user = saveNewUser(newUser);

        String codeValue = UUID.randomUUID().toString();

        saveConfirmCode(codeValue, user);

        String link = createLinkConfirmation(codeValue);

        String html = mailTemplatesUtil.createConfirmationMail(user.getFirstName(),user.getLastName(), link);


        mailSender.send(user.getEmail(), "Registration", html);

        return from(user);

    }


    private String createLinkConfirmation(String codeValue) {
        return baseurl + "api/users/confirm/" + codeValue;
    }

    private void saveConfirmCode(String codeValue, User user) {
        ConfirmationCode code = ConfirmationCode.builder()
                .code(codeValue)
                .user(user)
                .expiredDateTime(LocalDateTime.now().plusMinutes(10))
                .build();
        confirmationCodsRepository.save(code);
    }

    private User saveNewUser(NewUserDto newUser) {
        User user = User.builder()
                .email(newUser.getEmail())
                .hashPassword(passwordEncoder.encode(newUser.getPassword()))
                .role(User.Role.USER)
                .state(User.State.NOT_CONFIRMED)
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .build();

        usersRepository.save(user);
        return user;
    }

    private void checkIfExistByEmail(NewUserDto newUser) {
        if (usersRepository.existsByEmail(newUser.getEmail())) {
            throw new RestException(HttpStatus.CONFLICT,
                    "User with email <" + newUser.getEmail() + "> already exists");
        }
    }

    public UserDto getUserById(Long currentUserId) {
        return from(usersRepository.findById(currentUserId).orElseThrow());
    }

    @Transactional
    public UserDto confirm(String confirmCode) {
        ConfirmationCode code = confirmationCodsRepository
                .findByCodeAndExpiredDateTimeAfter(confirmCode, LocalDateTime.now())
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "Code not found or is expired"));

        User user = usersRepository
                .findFirstByCodesContains(code)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "User by code not found"));

        user.setState(User.State.CONFIRMED);

        usersRepository.save(user);

        return UserDto.from(user);
    }

}
