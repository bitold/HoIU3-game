package misc;

import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaveSystem {
    private static final Logger logger = Logger.getLogger(SaveSystem.class.getName());

    // Путь к корневой папке сохранений
    private final String baseDir;

    public SaveSystem(String baseDir) {
        this.baseDir = baseDir;
    }

    // Проверка наличия сохранений для игрока
    public boolean hasSaves(String nickname) {
        File dir = new File(baseDir + "/saves/" + nickname);
        return dir.exists() && dir.isDirectory();
    }

    // Получение списка доступных слотов для игрока
    public List<Integer> getAvailableSlots(String nickname) {
        List<Integer> slots = new ArrayList<>();
        File dir = new File(baseDir + "/saves/" + nickname);

        if (dir.exists() && dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.getName().startsWith("slot") && file.getName().endsWith(".dat")) {
                    try {
                        int slot = Integer.parseInt(file.getName()
                                .replace("slot", "")
                                .replace(".dat", ""));
                        slots.add(slot);
                    } catch (NumberFormatException e) {
                        logger.log(Level.WARNING,
                                "Неверный формат имени файла сохранения: " + file.getName(), e);
                    }
                }
            }
        }
        return slots;
    }

    // Удаление слота сохранения
    public void deleteSlot(String nickname, int slotNumber) {
        String filePath = baseDir + "/saves/" + nickname + "/slot" + slotNumber + ".dat";
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                logger.log(Level.INFO,
                        "Слот {0} для игрока {1} удален",
                        new Object[]{slotNumber, nickname});
            } else {
                logger.log(Level.SEVERE,
                        "Не удалось удалить слот {0} для игрока {1}",
                        new Object[]{slotNumber, nickname});
            }
        }
    }
}