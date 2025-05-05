package com.Roman21780.card_management.dto.response;

import com.Roman21780.card_management.model.User;
import lombok.*;

import javax.management.relation.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private User.Role role;
}