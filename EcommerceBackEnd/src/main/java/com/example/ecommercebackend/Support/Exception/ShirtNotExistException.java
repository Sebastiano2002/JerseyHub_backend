package com.example.ecommercebackend.Support.Exception;

public class ShirtNotExistException extends RuntimeException{
    public ShirtNotExistException(){super("La maglia non esiste");}
}
