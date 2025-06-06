package menus;
import map.GameMap;
import misc.MapEditor;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class EditorMenu {
    private static final Logger logger = Logger.getLogger(EditorMenu.class.getName());
    private static final String MAPS_DIR = "maps";
    private Scanner scanner = new Scanner(System.in);

    public void run() {
        // Создаем папку maps, если она не существует
        try {
            Files.createDirectories(Paths.get(MAPS_DIR));
        } catch (IOException e) {
            logger.severe("Ошибка создания папки maps: " + e.getMessage());
        }

        while (true) {
            System.out.println("\n=== Меню редактора карт ===");
            System.out.println("1. Создать новую карту");
            System.out.println("2. Загрузить существующую карту");
            System.out.println("3. Сохранить текущую карту");
            System.out.println("4. Редактировать карту");
            System.out.println("5. Выход");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createNewMap();
                    break;
                case "2":
                    loadMap();
                    break;
                case "3":
                    saveMap();
                    break;
                case "4":
                    editMap();
                    break;
                case "5":
                    System.out.println("Выход из редактора карт.");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createNewMap() {
        try {
            System.out.print("Введите ширину карты: ");
            int width = Integer.parseInt(scanner.nextLine());
            System.out.print("Введите высоту карты: ");
            int height = Integer.parseInt(scanner.nextLine());

            GameMap newMap = new GameMap(width, height);
            System.out.println("Новая карта создана.");
            this.map = newMap;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введены некорректные размеры карты.");
        }
    }

    private void loadMap() {
        System.out.print("Введите имя файла для загрузки (без расширения): ");
        String filename = scanner.nextLine();
        String path = MAPS_DIR + "/" + filename + ".dat";

        try {
            GameMap loadedMap = GameMap.loadFromFile(path);
            if (loadedMap != null) {
                System.out.println("Карта успешно загружена.");
                this.map = loadedMap;
            }
        } catch (Exception e) {
            logger.severe("Ошибка загрузки карты: " + e.getMessage());
        }
    }

    private void saveMap() {
        if (map == null) {
            System.out.println("Нет загруженной карты для сохранения.");
            return;
        }

        System.out.print("Введите имя файла для сохранения (без расширения): ");
        String filename = scanner.nextLine();
        String path = MAPS_DIR + "/" + filename + ".dat";

        try {
            map.saveToFile(path);
            System.out.println("Карта сохранена в файл: " + path);
        } catch (Exception e) {
            logger.severe("Ошибка сохранения карты: " + e.getMessage());
        }
    }

    private void editMap() {
        if (map == null) {
            System.out.println("Сначала загрузите или создайте карту.");
            return;
        }

        new MapEditor(map).run();
    }

    private GameMap map; // Текущая карта
}
