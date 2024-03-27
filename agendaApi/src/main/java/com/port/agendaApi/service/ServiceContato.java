package com.port.agendaApi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.port.agendaApi.model.Contato;
import com.port.agendaApi.repo.RepoContato;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.port.agendaApi.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ServiceContato {
    private final RepoContato repoContato;

    public Page<Contato> getAllContacts(int page, int size) {
        return repoContato.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Contato getContact(String id) {
        return repoContato.findById(id).orElseThrow(() -> new RuntimeException("Contato não encontrado!"));
    }

    public Contato createContact(Contato contato) {
        return repoContato.save(contato);
    }

    public void deleteContact(String id) {
        repoContato.deleteById(id);
    }

    public String uploadPhoto(String id, MultipartFile file) {
        log.info("Saving picture for user ID: {}", id);
        Contato contato = getContact(id);
        String photoUrl = photoFunction.apply(id, file);
        contato.setPhotoUrl(photoUrl);
        repoContato.save(contato);
        return photoUrl;
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if(!Files.exists(fileStorageLocation)) { Files.createDirectories(fileStorageLocation); }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contacts/image/" + filename).toUriString();
        }catch (Exception exception) {
            throw new RuntimeException("Não foi possível salvar a imagem");
        }
    };
}

