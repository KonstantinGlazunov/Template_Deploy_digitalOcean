package de.ait.template.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import de.ait.template.dto.StandardResponseDto;
import de.ait.template.models.FileInfo;
import de.ait.template.repositories.FilesInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * 26.10.2023
 * TemplateProject
 *
 * @author Konstantin Glazunov (AIT TR)
 */
@Service
@RequiredArgsConstructor
public class FileService {
    private final AmazonS3 amazonS3;

    private final FilesInfoRepository repository;

    @Transactional
    @SneakyThrows
    public StandardResponseDto upload(MultipartFile file) {

        String originalFileName = file.getOriginalFilename();

        String extension;
        if (originalFileName != null) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        } else {
            throw new IllegalArgumentException("null original file name");
        }
        String uuid = UUID.randomUUID().toString();
        String fileNewName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + "_" + uuid + extension;

        InputStream inputStream = file.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        PutObjectRequest request =
                new PutObjectRequest("ait-tr-base", "images/" + fileNewName, inputStream, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead); // разрешаем доступ к файлу;

        amazonS3.putObject(request);

        String link = amazonS3.getUrl("ait-tr-base", "images/" + fileNewName).toString();

        FileInfo fileInfo = FileInfo.builder()
                .link(link)
                .build();
        repository.save(fileInfo);

        return new StandardResponseDto().builder()
                .message(fileNewName)
                .message(link)
                .build();
    }
}
