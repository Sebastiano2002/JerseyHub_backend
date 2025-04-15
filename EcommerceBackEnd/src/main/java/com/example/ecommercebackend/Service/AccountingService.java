package com.example.ecommercebackend.Service;

import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.User;
import com.example.ecommercebackend.Repository.UserRepository;
import com.example.ecommercebackend.Support.DTO.UserDTO;
import com.example.ecommercebackend.Support.Exception.MailUserAlreadyExistsException;
import com.example.ecommercebackend.Support.Exception.MailUserNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class AccountingService implements Serializable {

    @Autowired
    private UserRepository userRepository;

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public User registerUser(UserDTO user) throws MailUserAlreadyExistsException{
        if (userRepository.findByEmail(user.getEmail()) != null) {throw new MailUserAlreadyExistsException();}
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setNome(user.getNome());
        newUser.setCognome(user.getCognome());
        newUser.setTelefono(user.getTelefono());
        newUser.setCitta(user.getCitta());
        newUser.setIndirizzo(user.getIndirizzo());
        newUser.setCap(user.getCap());
        newUser.setCarrello(-1);
        newUser.setOrders(new HashSet<Ordine>());
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public User getUserByMail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean modifyUser(String currentEmail, UserDTO userDTO) {

        User user = userRepository.findByEmail(currentEmail);
        System.out.println(userDTO);
        if (user == null) {
            throw new MailUserNotExistException();
        }
        if (userDTO.getTelefono() != null && !userDTO.getTelefono().isEmpty()) {
            user.setTelefono(normalizePhoneNumber(userDTO.getTelefono()));
        }
        if (userDTO.getCap() != null && !userDTO.getCap().isEmpty()) {
            user.setCap(userDTO.getCap());
        }
        if (userDTO.getIndirizzo() != null && !userDTO.getIndirizzo().isEmpty()) {
            user.setIndirizzo(userDTO.getIndirizzo());
        }
        if (userDTO.getCitta() != null && !userDTO.getCitta().isEmpty()) {
            user.setCitta(userDTO.getCitta());
        }
        User updated =userRepository.saveAndFlush(user);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteUser(String currentEmail) {
        User user = userRepository.findByEmail(currentEmail);
        if (user == null) {
            throw new RuntimeException("Utente non trovato.");
        }
        userRepository.delete(user);
        return true;
    }


    @Transactional(readOnly = true)
    public List<User> getAll(){
        return userRepository.findAll();
    }

    public String normalizePhoneNumber(String phone) {
        String cleanedPhone = phone.replace("-", "").replace(" ", "");
        return cleanedPhone.startsWith("+") ? cleanedPhone : "+39" + cleanedPhone;
    }

}