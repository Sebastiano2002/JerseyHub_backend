package com.example.ecommercebackend.Support.Exception;

public class MailUserNotExistException extends RuntimeException{
    public MailUserNotExistException(){
        super("L'email fornita è inesistente 1");
    }
}
