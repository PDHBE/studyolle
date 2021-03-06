package com.github.pdhbe.studyolle.account;

import com.github.pdhbe.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account submitSignUp(SignUpFormDto signUpFormDto) {
        Account savedAccount = saveNewAccount(signUpFormDto);
        savedAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(savedAccount);
        return savedAccount;
    }

    public void sendSignUpConfirmEmail(Account savedAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedAccount.getEmail());
        mailMessage.setSubject("스터디 올레, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() +
                "&email=" + savedAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    private Account saveNewAccount(SignUpFormDto signUpFormDto) {
        Account account = Account.builder()
                .nickname(signUpFormDto.getNickname())
                .email(signUpFormDto.getEmail())
                .password(passwordEncoder.encode(signUpFormDto.getPassword()))
                .AlarmByWebStudyCreated(true)
                .AlarmByWebStudyUpdated(true)
                .AlarmByWebStudyEnrollmentResult(true)
                .build();

        return accountRepository.save(account);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
