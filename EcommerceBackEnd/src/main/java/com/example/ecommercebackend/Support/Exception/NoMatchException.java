package com.example.ecommercebackend.Support.Exception;

public class NoMatchException extends RuntimeException{
    public NoMatchException(){
        super("Il campo/i non è/sono nella forma richiesta");
    }
}
