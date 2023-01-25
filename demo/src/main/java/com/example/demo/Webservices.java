package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")

public class Webservices {

    List<Contact> contacts = new ArrayList<Contact>();


   /* @GetMapping(value = "/contacts")
    List<Contact> getContacts() {
        this.contacts = readFromJSON();
        return contacts;
    }*/

    /*@GetMapping("/contacts")
    public List<Contact> getContacts(HttpServletRequest request) {
        this.contacts = readFromJSON();
        String url = request.getRequestURL().toString();
        System.out.println("URL utilisee par le client " + url);
        return contacts;
    }*/
    @GetMapping("/contacts")
    public List<Contact> getContacts(HttpServletRequest request, @RequestParam(defaultValue = "0") int annee) {
        this.contacts = readFromJSON();
        String url = request.getRequestURL().toString();
        System.out.println("URL utilisee par le client " + url);
        if (annee == 0) {
            return contacts;
        } else {
            return contacts.stream()
                    .filter(c -> c.getAnnee() == annee)
                    .collect(Collectors.toList()); //transforme les données collectées en une liste de contacts
        }
        //http://localhost:8080/api/contacts?annee=2000
    }



    @PostMapping(value = "/contacts", consumes = "application/json")
    public List<Contact> newContact(@RequestBody Contact contact) {
        int maxId = 0;
        for (Contact c : contacts) {
            if (c.getId() > maxId) {
                maxId = c.getId();
            }
        }
        contact.setId(maxId + 1);
        contacts.add(contact);
        writeToJSON(contacts);
        return contacts;
    }

    public List<Contact> readFromJSON() {
        Type listType = new TypeToken<ArrayList<Contact>>(){}.getType();
        try {
            return new Gson().fromJson(new FileReader("contacts.json"),
                    listType);
        }
        catch (Exception ex ) {
            System.out.println("Erreur lecture du fichier:"+ex.getMessage());
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void writeToJSON(List<Contact> contacts) {
        try {
            FileWriter file = new FileWriter("contacts.json");
            new Gson().toJson(contacts, file);
            file.close();
        }
        catch (Exception ex ) {
            System.out.println("Erreur écriture du fichier:"+ex.getMessage());
            ex.printStackTrace();
        }
    }
   /* @DeleteMapping(value="/contact/{id}")
    public List<Contact> deleteContact(@PathVariable int id) {
        for(int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getId() == id) {
                contacts.remove(i);
                writeToJSON(contacts);
                break;
            }
        }
        return contacts;
    }*/

    @DeleteMapping(value="/contact/{id}")
    public ResponseEntity<List<Contact>> deleteContact(@PathVariable int id) {
        boolean contactDeleted = false;
        for(int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getId() == id) {
                contacts.remove(i);
                writeToJSON(contacts);
                contactDeleted = true;
                break;
            }
        }
        if(!contactDeleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @GetMapping("/contacts/{id}")
    public Contact getContact(@PathVariable int id) {
        for(Contact c : contacts) {
            if(c.getId() == id) {
                return c;
            }
        }
        return null;
    }

   /* @PutMapping(value = "/contact/{id}", consumes = "application/json")
    public void editContact(@PathVariable int id, @RequestBody Contact contact) {
        for(int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getId() == id) {
                contacts.set(i, contact);
                writeToJSON(contacts);
                break;
            }
        }
    }
*/

    @PutMapping(value = "/contact/{id}", consumes = "application/json")
    public ResponseEntity<Void> editContact(@PathVariable int id, @RequestBody Contact contact) {
        boolean contactEdited = false;
        for(int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getId() == id) {
                contacts.set(i, contact);
                writeToJSON(contacts);
                contactEdited = true;
                break;
            }
        }
        if (!contactEdited) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}



