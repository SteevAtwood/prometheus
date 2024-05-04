package com.example.application.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CompanyService {

    private final RestTemplate restTemplate;

    public CompanyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // public String getData() {
    // String url =
    // "https://api.damia.ru/spk/report?req=7707083893&key=db28411f27f2b1b286468ede0ed4f2fc30a4c5a8&format=json&sections=fns[1,2,4,9,10],bals,checks,isps[1],arbs[1],scoring";
    // return restTemplate.getForObject(url, String.class);
    // }

    public String getData(String InnParam) {
        String apiKey = "db28411f27f2b1b286468ede0ed4f2fc30a4c5a8";
        String sections = "fns[1,2,4,9,10],bals,checks,isps[1],arbs[1],scoring";
        String url = String.format("https://api.damia.ru/spk/report?req=%s&key=%s&format=json&sections=%s", InnParam,
                apiKey, sections);
        return restTemplate.getForObject(url, String.class);
    }

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
