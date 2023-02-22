package com.example.emailsender.services;

import com.example.emailsender.entity.Email;
import com.example.emailsender.entity.RegistrationRequest;
import com.example.emailsender.entity.UserRole;
import com.example.emailsender.entity.User;
import com.example.emailsender.security.EmailValidator;
import com.example.emailsender.security.token.ConfirmationToken;
import com.example.emailsender.security.token.ConfirmationTokenService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RegistrationService {

    private final UserService userService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmTokenService;
    private final EmailSenderService emailSenderService;


    public RegistrationService(UserService userService, EmailValidator emailValidator, ConfirmationTokenService confirmTokenService, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.emailValidator = emailValidator;
        this.confirmTokenService = confirmTokenService;
        this.emailSenderService = emailSenderService;
    }

    public String register(RegistrationRequest request) throws MessagingException {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (isValidEmail) {
            String tokenForNewUser = userService.signUpUser(new User(request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword(),
                    UserRole.USER));

            String link = "http://localhost:8080/api/v1/registration/confirm?token=" + tokenForNewUser;
            Email email = new Email();
            email.setTo(request.getEmail());
            email.setFrom("thienvysg@gmail.com");
            email.setSubject("test test test");
            email.setTemplate("verification.html");
            Map<String, Object> properties = new HashMap<>();
            properties.put("link", link);
            email.setProperties(properties);
            emailSenderService.sendHtmlMessage(email);
            return tokenForNewUser;
        } else {
            throw new IllegalStateException(String.format("Email %s, not valid", request.getEmail()));
        }
    }


    @Transactional
    public String confirmToken(String token) {
        Optional<ConfirmationToken> confirmToken = confirmTokenService.getToken(token);

        if (confirmToken.isEmpty()) {
            throw new IllegalStateException("Token not found!");
        }

        if (confirmToken.get().getConfirmedAt() != null) {
            throw new IllegalStateException("Email is already confirmed");
        }

        LocalDateTime expiresAt = confirmToken.get().getExpiresAt();

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token is already expired!");
        }

        confirmTokenService.setConfirmedAt(token);
        userService.enableUsers(confirmToken.get().getUser().getEmail());

        //Returning confirmation message if the token matches
        return "Your email is confirmed. Thank you for using our service!";
    }
}
