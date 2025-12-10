package com.Projeto.InfoMaisSaude.exceptions;

public class LoginJaExisteException extends RuntimeException {
    public LoginJaExisteException(String message) {
        super(message);
    }
}
