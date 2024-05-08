package com.example.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.application.data.History;
import com.example.application.repository.HistoryRepository;
import com.example.application.security.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    private final RestTemplate restTemplate;

    @Value("${app.file-storage-root}")
    private Path root;

    public CompanyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getData(String innParam) {
        Long userId = authenticatedUser.get()
                .map(user -> user.getId())
                .orElse(null);

        Path fileName = fetchData(innParam);
        String trimmedFileName = fileName.getFileName().toString();
        System.out.println("Сохранение истории для INN: " + innParam);
        saveHistory(innParam, trimmedFileName, userId);
        System.out.println("История сохранена");
        String result;
        try {
            result = Files.readString(fileName);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String();
    }

    public Path fetchData(String innParam) {

        Optional<Path> latestFile = findLatestFile(innParam);

        if (latestFile.isPresent()) {
            System.out.println("Чтение данных из файла: " + latestFile.get());
            return latestFile.get();
        }

        System.out.println("Отправка запроса к API для INN: " + innParam);

        String apiKey = "db28411f27f2b1b286468ede0ed4f2fc30a4c5a8";
        String sections = "fns[1,2,4,9,10],bals,checks,isps[1],arbs[1],scoring";
        String url = String.format("https://api.damia.ru/spk/report?req=%s&key=%s&format=json&sections=%s", innParam,
                apiKey, sections);

        String response = restTemplate.getForObject(url, String.class);

        if (Files.notExists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException("Не удалось создать директорию: " + root.toString(), e);
            }
        }

        String timestamp = new Timestamp(System.currentTimeMillis()).toString().replace(":", "-").replace(" ", "_");
        String filename = String.format("%s_%s.json", innParam, timestamp);

        Path filePath = root.resolve(filename);

        try {
            Files.writeString(filePath, response, StandardOpenOption.CREATE_NEW);
            System.out.println("Данные сохранены в файл: " + filePath);
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException("Файл уже существует: " + filename, e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи в файл: " + e.getMessage(), e);
        }

        return filePath;
    }

    // return filename and send it to getData

    private Optional<Path> findLatestFile(String innParam) {
        if (Files.exists(root)) {
            try {
                return Files.list(root)
                        .filter(path -> path.getFileName().toString().startsWith(innParam + "_"))
                        .max(Comparator.comparingLong(path -> {
                            try {
                                return Files.getLastModifiedTime(path).toMillis();
                            } catch (IOException e) {
                                return Long.MIN_VALUE;
                            }
                        }));
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при поиске файлов: " + root, e);
            }
        }

        return Optional.empty();
    }

    @Transactional
    public History saveHistory(String INN, String fileName, Long userId) {

        History history = new History();
        history.setINN(INN);
        history.setUserId(userId);
        history.setDate(new Timestamp(System.currentTimeMillis()));
        history.setFileName(fileName);

        return historyRepository.save(history);
    }

    public Page<History> list(Pageable pageable) {
        return historyRepository.findAll(pageable);
    }

    public Page<History> list(Pageable pageable, Specification<History> filter) {
        return historyRepository.findAll(filter, pageable);
    }

    // public String getData() {
    // String url =
    // "https://api.damia.ru/spk/report?req=7707083893&key=db28411f27f2b1b286468ede0ed4f2fc30a4c5a8&format=json&sections=fns[1,2,4,9,10],bals,checks,isps[1],arbs[1],scoring";
    // return restTemplate.getForObject(url, String.class);
    // }

    // public String getData(String InnParam) {
    // String apiKey = "db28411f27f2b1b286468ede0ed4f2fc30a4c5a8";
    // String sections = "fns[1,2,4,9,10],bals,checks,isps[1],arbs[1],scoring";
    // String url =
    // String.format("https://api.damia.ru/spk/report?req=%s&key=%s&format=json&sections=%s",
    // InnParam,
    // apiKey, sections);
    // return restTemplate.getForObject(url, String.class);
    // }

    // public String getData() {
    // String filePath = "/home/yozhef/Documents/response.json";
    // ObjectMapper objectMapper = new ObjectMapper();

    // try {
    // File file = new File(filePath);
    // if (!file.exists()) {
    // return "Файл не найден: " + filePath;
    // }

    // Object jsonData = objectMapper.readValue(file, Object.class);
    // return
    // objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData);

    // } catch (IOException e) {
    // return "Ошибка при чтении файла: " + e.getMessage();
    // }
    // }
}
