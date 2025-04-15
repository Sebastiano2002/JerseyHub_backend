package com.example.ecommercebackend.Support.Exception;

public class LeagueExistAlreadyException extends RuntimeException{
    public LeagueExistAlreadyException(){super("La lega esiste già");}
}
