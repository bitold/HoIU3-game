package misc;

import asset.Cell;
import classification.CellClassificator;
import map.GameMap;

import java.io.Serializable;
import java.util.Scanner;
import java.util.logging.Logger;

public class MapEditor {
    private static final Logger logger = Logger.getLogger(MapEditor.class.getName());
    private GameMap map;

    public MapEditor(GameMap map) {
        this.map = map;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Редактор карты ===");
            System.out.println("Введите координаты (x y) или 'exit' для выхода:");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) break;

            try {
                String[] coords = input.split(" ");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                // Показываем доступные типы ландшафтов
                System.out.println("Выберите тип ландшафта:");
                CellClassificator[] types = CellClassificator.values();
                for (int i = 0; i < types.length; i++) {
                    System.out.println((i + 1) + ". " + types[i].getName());
                }
                String choice = scanner.nextLine();
                System.out.println(choice);
                int typeIndex = Integer.parseInt(choice) - 1;
                System.out.println(choice);

                if (typeIndex >= 0 && typeIndex < types.length) {
                    CellClassificator selectedType = types[typeIndex];

                    Cell cell = CellClassificator.createCell(selectedType, null);
                    map.setTile(x, y, cell);
                    System.out.println("Клетка на (" + x + ", " + y + ") изменена на " + selectedType.getName());
                } else {
                    System.out.println("Неверный номер типа.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка ввода. Попробуйте снова." + e);
                scanner.nextLine(); // Очистка буфера
            }
        }
    }
}