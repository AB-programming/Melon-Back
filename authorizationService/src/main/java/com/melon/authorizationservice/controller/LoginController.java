package com.melon.authorizationservice.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class LoginController {
    @Value("${client.uri}")
    private String clientUri;

    @Resource
    private JwtDecoder jwtDecoder;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        return "login";
    }

    @GetMapping("/oauth2/consent")
    public String consent(@RequestParam String scope, @RequestParam String client_id, @RequestParam String state, Authentication authentication, Model model) {
        model.addAttribute("scopes", scope.split(" "));
        model.addAttribute("clientId", client_id);
        model.addAttribute("state", state);
        return "consent";
    }

    @GetMapping("/introspect")
    @ResponseBody
    public Map<String, Object> introspect(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return Map.of("active", true, "user", jwt.getClaims().get("user"));
        } catch(Exception e) {
            return Map.of("active", false);
        }
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:" + clientUri;
    }
}
