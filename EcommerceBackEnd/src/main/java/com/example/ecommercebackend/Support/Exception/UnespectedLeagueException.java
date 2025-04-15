package com.example.ecommercebackend.Support.Exception;

public class UnespectedLeagueException extends RuntimeException{
    public UnespectedLeagueException(){
        super("La league fornita è inesistente");
    }
}
