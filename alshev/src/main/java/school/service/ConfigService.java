//package school.service;
//
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.Scanner;
//
//@Service
//public class ConfigService {
//
//    public String loadConfig() {
//        File file = new File("./config.txt");
//        StringBuilder content = new StringBuilder();
//
//        if (!file.exists()) {
//            System.err.println("Файл не найден: " + file.getAbsolutePath());
//            return null;
//        }
//
//        try (Scanner scanner = new Scanner(file)) {
//            while (scanner.hasNextLine()) {
//                content.append(scanner.nextLine()).append("\n");
//            }
//        } catch (FileNotFoundException e) {
//            System.err.println("Ошибка при открытии файла: " + e.getMessage());
//        }
//
//        return content.toString();
//    }
//}