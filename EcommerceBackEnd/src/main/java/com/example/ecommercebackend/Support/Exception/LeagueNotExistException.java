package com.example.ecommercebackend.Support.Exception;

public class LeagueNotExistException extends RuntimeException{
    public LeagueNotExistException(){super("La lega non esiste");}
}
