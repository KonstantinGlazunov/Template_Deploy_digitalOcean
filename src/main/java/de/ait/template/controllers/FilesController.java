package de.ait.template.controllers;


import de.ait.template.controllers.api.FilesApi;
import de.ait.template.dto.StandardResponseDto;
import de.ait.template.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 25.10.2023
 * TemplateProject
 *
 * @author Konstantin Glazunov (AIT TR)
 */

@RequiredArgsConstructor
@RestController
public class FilesController implements FilesApi {

    private final FileService fileService;


    public StandardResponseDto upload(MultipartFile file) {
        return fileService.upload(file);
    }

}
