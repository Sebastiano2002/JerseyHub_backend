package com.example.ecommercebackend.Support.Exception;
public class MailUserAlreadyExistsException extends RuntimeException{
    public MailUserAlreadyExistsException(){
        super("L'email fornita è associata ad un altro account");
    }
}
