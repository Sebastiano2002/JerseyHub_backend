package com.example.ecommercebackend.Service;

import com.example.ecommercebackend.Entity.User;
import com.example.ecommercebackend.Support.DTO.UserDTO;
import com.example.ecommercebackend.Support.Exception.MailUserAlreadyExistsException;
import com.example.ecommercebackend.Support.Exception.NoMatchException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class KeycloakService {
    @Autowired
    private AccountingService accountingService;

    @Autowired
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    public User userRegistration(UserDTO user, String password){
        User u = accountingService.getUserByMail(user.getEmail());
        if(u != null) throw new MailUserAlreadyExistsException();
        user.setTelefono(accountingService.normalizePhoneNumber(user.getTelefono()));
        if(!matchControl(user.getEmail(), user.getTelefono(), user.getCap())) throw new NoMatchException();
        try {
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEnabled(true);
            userRepresentation.setUsername(user.getEmail());
            userRepresentation.setEmail(user.getEmail());
            userRepresentation.setFirstName(user.getNome());
            userRepresentation.setLastName(user.getCognome());
            userRepresentation.setEmailVerified(true);

            userRepresentation.setCredentials(Collections.singletonList(createPasswordCredentials(password)));

            RealmResource realmResource = keycloak.realm(keycloakRealm);
            UsersResource usersResource = realmResource.users();

            try (Response response = usersResource.create(userRepresentation)) {
                if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                    return accountingService.registerUser(user);
                } else {
                    throw new RuntimeException("Errore creazione utente Keycloak: " + response.getStatus());
                }
            }

        }catch (RuntimeException e){ throw new RuntimeException("Qualcosa è andato storto " + e.getMessage(), e);}
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public boolean matchControl(String email, String tel, String cap) {
        String regexEmail = "^[-A-Za-z0-9._%&$+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        String regexTel = "^([\\+][0-9][0-9])?[0-9][0-9][0-9][-\\s\\.]?[0-9][-\\s\\.]?[0-9][0-9][-\\s\\.]?[0-9][-\\s\\.]?[0-9][0-9][0-9]$";
        String regexCap = "^\\d{5}$";

        if (!email.matches(regexEmail)) {
            log.error("Email non valida: " + email);
        }
        if (!tel.matches(regexTel)) {
            log.error("Telefono non valido: " + tel);
        }
        if (!cap.matches(regexCap)) {
            log.error("CAP non valido: " + cap);
        }

        return email.matches(regexEmail) && cap.matches(regexCap) && tel.matches(regexTel);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteUser(String currentEmail) {
        try {
            UsersResource usersResource = keycloak.realm(keycloakRealm).users();
            String keycloakUserId = getKeycloakUserId(currentEmail);
            if (keycloakUserId != null) {
                usersResource.delete(keycloakUserId);
            } else {
                throw new RuntimeException("Utente non trovato in Keycloak.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'eliminazione dell'utente da Keycloak: " + e.getMessage(), e);
        }
        return true;
    }

    private String getKeycloakUserId(String email) {
        UsersResource usersResource = keycloak.realm(keycloakRealm).users();
        List<UserRepresentation> users = usersResource.search(email, null, null, null, null, null);
        if (!users.isEmpty()) {
            return users.get(0).getId();
        }
        return null;
    }
}