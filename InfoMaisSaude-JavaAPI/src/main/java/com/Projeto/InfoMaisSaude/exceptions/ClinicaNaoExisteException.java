package com.Projeto.InfoMaisSaude.exceptions;

public class ClinicaNaoExisteException extends RuntimeException {
     public ClinicaNaoExisteException(String message){
         super(message);
     }
}
