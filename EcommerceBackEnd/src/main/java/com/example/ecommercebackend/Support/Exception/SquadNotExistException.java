package com.example.ecommercebackend.Support.Exception;

public class SquadNotExistException extends RuntimeException{
    public SquadNotExistException(){
        super("La squadra fornita è inesistente");
    }
}
