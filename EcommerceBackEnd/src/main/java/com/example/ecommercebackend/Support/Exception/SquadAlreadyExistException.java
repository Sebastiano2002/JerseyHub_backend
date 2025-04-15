package com.example.ecommercebackend.Support.Exception;

public class SquadAlreadyExistException extends RuntimeException{
    public SquadAlreadyExistException(){super("La squadra fornita esiste già");}
}
