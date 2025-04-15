package com.example.ecommercebackend.Support.Exception;

public class ShirtAlreadyExistException extends RuntimeException{
    public ShirtAlreadyExistException(){
        super("La maglia fornita esiste già");
    }
}
