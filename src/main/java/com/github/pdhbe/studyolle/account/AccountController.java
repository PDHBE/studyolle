package com.github.pdhbe.studyolle.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    @GetMapping("sign-up")
    public String signUpForm(){
        return "account/sign-up";
    }
}