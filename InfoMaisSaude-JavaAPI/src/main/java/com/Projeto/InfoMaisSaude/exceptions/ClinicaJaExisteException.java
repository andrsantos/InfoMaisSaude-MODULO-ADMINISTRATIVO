package com.Projeto.InfoMaisSaude.exceptions;

public class ClinicaJaExisteException extends RuntimeException{
     public ClinicaJaExisteException(String message) {
        super(message);
    }
    
}
