package com.port.agendaApi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.port.agendaApi.model.Contato;
import com.port.agendaApi.service.ServiceContato;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.port.agendaApi.constant.Constant.PHOTO_DIRECTORY;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ControllerContato {

    private final ServiceContato serviceContato;

    @PostMapping
    public ResponseEntity<Contato> createContact(@RequestBody Contato contato) {
        //return ResponseEntity.ok().body(serviceContato.createContact(contato));
        return ResponseEntity.created(URI.create("/contacts/userID")).body(serviceContato.createContact(contato));
    }

    @GetMapping
    public ResponseEntity<Page<Contato>> getContacts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(serviceContato.getAllContacts(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contato> getContact(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(serviceContato.getContact(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id, @RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok().body(serviceContato.uploadPhoto(id, file));
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable String id) {
        serviceContato.deleteContact(id);
    }


    @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }
}
