package com.example.ecommercebackend.Support;

import com.example.ecommercebackend.Support.DTO.UserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationReq {

    @Valid
    UserDTO user;
    @NotBlank
    private String pass;

    public RegistrationReq(UserDTO user, String pass){
        this.user=user;
        this.pass=pass;
    }
}
