## Настройка проекта

* Необходимо обеспечить наличие Environment Variables
  * `spring.datasource.username`
  * `spring.datasource.password`
  * `spring.datasource.url`

## примитивное хранение файлов: 
        @PostMapping("/api/files") //Загрузка файлов на сервер BackEnd
    public StandardResponseDto upload(@RequestParam("file") MultipartFile file){

        String originalFileName = file.getOriginalFilename();

        String extension;
        if (originalFileName !=null) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        } else {
            throw new IllegalArgumentException("null original file name");
        }
        String uuid = UUID.randomUUID().toString();
        String fileNewName = originalFileName.substring(0,originalFileName.lastIndexOf(".")) + "_" + uuid + extension;

        try (InputStream inputStream = file.getInputStream();) {

            Files.copy(inputStream, Path.of("D:\\ProgrammingSchool\\BackEnd\\Lesson_21\\TemplateProject\\static\\" + fileNewName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new StandardResponseDto().builder()
                .message(fileNewName )
                .build();
    }