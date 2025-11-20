package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record RegisterRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        String email,

        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @CPF
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
        String cpf

) {}
