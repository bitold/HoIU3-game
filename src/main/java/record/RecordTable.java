package record;

import java.io.*;
import java.util.*;

public class RecordTable implements Serializable {
    private Map<String, PlayerStats> stats = new HashMap<>();

    public RecordTable() {
        loadAllRecords();
    }

    /**
     * Сохраняет новую запись в файл, связанную с никнеймом игрока.
     */
    public void saveRecord(Record record) {
        String nickname = record.getNickname();
        File dir = new File("records", nickname);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "records.dat");

        List<Record> records = new ArrayList<>();
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Record> loaded = (List<Record>) obj;
                    records.addAll(loaded);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка чтения файла: " + file.getAbsolutePath());
                e.printStackTrace();
            }
        }

        records.add(record);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(records);
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + file.getAbsolutePath());
            e.printStackTrace();
        }

        PlayerStats ps = stats.getOrDefault(nickname, new PlayerStats());
        if (record.isVictory()) {
            ps.wins++;
        } else {
            ps.losses++;
        }
        stats.put(nickname, ps);
    }

    /**
     * Выводит таблицу рекордов в консоль.
     */
    public void printRecordTable() {
        List<Map.Entry<String, PlayerStats>> sorted = new ArrayList<>(stats.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue().wins, a.getValue().wins));

        System.out.println("Таблица рекордов:");
        System.out.println("Никнейм\t\tПобеды\tПоражения");
        for (Map.Entry<String, PlayerStats> entry : sorted) {
            String name = entry.getKey();
            PlayerStats ps = entry.getValue();
            System.out.printf("%-15s %d\t\t%d%n", name, ps.wins, ps.losses);
        }
    }

    /**
     * Загружает все существующие записи из папки records.
     */
    private void loadAllRecords() {
        File recordsDir = new File("records");
        if (!recordsDir.exists()) {
            recordsDir.mkdirs();
            return;
        }

        for (File dir : recordsDir.listFiles()) {
            if (dir.isDirectory()) {
                String nickname = dir.getName();
                File file = new File(dir, "records.dat");
                if (file.exists()) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        Object obj = ois.readObject();
                        if (obj instanceof List<?>) {
                            @SuppressWarnings("unchecked")
                            List<Record> records = (List<Record>) obj;
                            for (Record r : records) {
                                PlayerStats ps = stats.getOrDefault(nickname, new PlayerStats());
                                if (r.isVictory()) {
                                    ps.wins++;
                                } else {
                                    ps.losses++;
                                }
                                stats.put(nickname, ps);
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Ошибка загрузки записей из: " + file.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Статистика игрока: победы и поражения.
     */
    private static class PlayerStats implements Serializable {
        int wins;
        int losses;
    }
}