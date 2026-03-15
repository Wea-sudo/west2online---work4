package com.weasudo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
