package com.example.ecommercebackend.Support.Exception;

public class NullParameterException extends RuntimeException{
    public NullParameterException(){
        super("Devi fornire tutti i campi obbligatori");
    }
}
