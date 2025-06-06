package menus;

import asset.Entity;
import asset.Hero;
import asset.Unit;
import castle.Castle;
import game.Game;
import map.GameMap;
import misc.Coordinates;
import misc.MapEditor;
import misc.SaveSystem;
import player.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;



public class GameMenu implements Serializable {
    private static final String MAPS_DIR = "maps";
    Game game;
    GameMap gameMap;
    GameMap battleMap;
    transient private Scanner scanner = new Scanner(System.in);
    private static final String BASE_DIR = "."; // Текущая директория
    private static final Logger logger = Logger.getLogger(GameMenu.class.getName());

    public GameMenu(Game game){
        this.game = game;
        this.gameMap = game.getGameMap();
        this.battleMap = game.getBattleMap();
    }

    public GameMenu(){

    }

    public void refreshBattleMap(){
        this.battleMap = game.getBattleMap();
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.scanner = new Scanner(System.in);
    }

    public void printMoveInterface(int mode){
        if (mode == 0){
            System.out.println("__________________________________________________");
            System.out.println("__________________________________________________");
            System.out.println("Игровое поле:");
            gameMap.render();
            System.out.println("Ходит " + game.getCurrentlyMoving().getNickname());
            System.out.println("__________________________________________________");
            System.out.println("__________________________________________________");
        } else if (mode == 1) {
            System.out.println("__________________________________________________");
            System.out.println("__________________________________________________");
            System.out.println("Поле сражения:");
            battleMap.render();
            System.out.println("Ходит " + game.getCurrentlyMoving().getNickname());
            System.out.println("__________________________________________________");
            System.out.println("__________________________________________________");
        }
    }

    void printEntityMapWithKeysAfter(GameMap map){
        System.out.println("Карта сущностей:");
        for (Entity entity: map.getEntitiesMap().values()){
            System.out.println(entity.toString() + entity.getCoordinates());
        }
        List<Map.Entry<Coordinates, Entity>> entityMapList = map.getEntitiesMap().entrySet().stream().toList();
        for (Map.Entry<Coordinates, Entity> entry: entityMapList){
            System.out.println("Хэшкод ключа " + entry.getKey() + " = " + entry.getKey().hashCode());
        }
    }

    public void printUnitExhausted(){
        System.out.println("Энергия юнита иссякла.");
    }

    public void printGivingMove(){
        System.out.println("Энергия юнитов закончилась. Передача хода...");
    }

    public void printGivingConrtol(Entity entity){
        System.out.println("Вы управляете " + entity.getType() + " " + entity.getDesign());
        System.out.println("Энергии на передвижение: " + entity.getCurrentMP());
    }

    public void printPlayerLost(Player player){
        System.out.println("ИГРОК " + player.getNickname() + " ПРОИГРАЛ.");
    }

    public void printGameOver(){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("ИГРА ОКОНЧЕНА.");
        Set<Player> losers = game.getLosers();
        Set<Player> players = game.getPlayers();
        for (Player loser: losers){
            System.out.println("Игрок " + loser + " проиграл. Он " + (loser.getCastle().isCaptured() ? "потерял замок." : "потерял всех героев."));
        }
        for (Player player: players){
            if (!losers.contains(player)){
                System.out.println("Игрок " + player + " выиграл.");
            }
        }
    }

    public void printBattleOver(){

    }

    public void printDealtDamage(Unit unit, int damage){
        System.out.println("Юниту " + unit.getDesign() + " нанесено " + damage + " урона.");
        System.out.println("У него осталось " + unit.getHealth() + "HP." + (unit.getHealth() <= 0 ? " Юнит " + unit.getDesign() + " повержен." : ""));
    }


    public void printStatistics(Player player){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Статистика игрока " + player.getNickname());
        System.out.println("Список героев:");
        for (Hero hero: player.getHeroes()){
            System.out.println(hero.toString() + ". Состав армии " + hero.getArmySize() );
            for (Unit unit: hero.getArmy()){
                System.out.println("    " + unit.toString());
            }
        }
        System.out.println("Состояние замка: Замок " + (player.getCastle().isBeingCaptured() ? "ЗАХВАТЫВАЕТСЯ" + printWhoIsCapturing(player.getCastle()) : "НЕ ЗАХВАТЫВАЕТСЯ"));
        System.out.println("Здоровье замка: " + player.getCastle().getCurrentDurability());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public String printWhoIsCapturing(Castle castle){
        if (!Objects.isNull(castle.getVisitor())){
            System.out.println("В замке игрока " + castle.getOwner().getNickname() + " находится " + castle.getVisitor().toStringShortened());
        }
        return "";
    }


    public void mainMenuCycle(String nickname){
        while (runMainMenu(nickname));
        game.gameCycle();
        //startGameIfNeverStarted();
    }


    public boolean runMainMenu(String nickname) {
        System.out.println("Главное меню. Выберите действие: ");
        if (!Objects.isNull(game)) {System.out.println("0. Продолжить");}
        System.out.println("1. Новая игра");
        System.out.println("2. Сохранить игру");
        System.out.println("3. Загрузить игру");
        System.out.println("4. Таблица рекордов");
        System.out.println("5. Редактор карт");
        System.out.println("6. Выйти");

        String input = scanner.nextLine();
        switch (input){
            case "0":
                if (!Objects.isNull(game)) {return false;} else break;
            case "1":
                createGame(nickname, scanner);
                break;
            case "2":
                saveGame(nickname, scanner);
                break;
            case "3":
                loadGame(nickname, scanner);
                break;
            case "4":
                break;
            case "5":
                EditorMenu editorMenu = new EditorMenu();
                editorMenu.run();
            case "6":
                System.exit(0);
                return false;
        }
        return true;
    }


    private void startGameIfNeverStarted(){
        if (game.getStatus().equals("pregame")){
            game.setStatus("buying");
            game.preGame();
        }
    }


    private void createGame(String nickname, Scanner scanner) {
        gameCreationSetupMenu(nickname, scanner);
        System.out.println("Введите номер слота (1-10): ");
        String input = scanner.nextLine();
        int slot = Integer.parseInt(scanner.nextLine());
        game.saveToSlot(BASE_DIR, nickname, slot);
        System.out.println("Ваша игра создана! Нажмите 0 чтобы перейти к игре.");
    }

    private void gameCreationSetupMenu(String nickname, Scanner scanner){
        System.out.println("Выберите карту: ");
        System.out.println("0. Сгенерировать стандартную карту");
        System.out.println("1. Выбрать кастомную карту");
        String choice = scanner.nextLine();
        switch (choice){
            case "0":
                standartGameCreationMenu(nickname);
                break;
            case "1":
                customMapGameCreationMenu(nickname);
                break;
        }
    }

    private void customMapGameCreationMenu(String nickname){
        printAvailableMaps();
        System.out.println("Введите название карты: ");
        String choice = scanner.nextLine();
        try {
            GameMap map = GameMap.loadFromFile(MAPS_DIR + "/" + choice + ".dat");
            Game game = new Game();
            game.initializeCustomMapGame(map, nickname);
            this.game = game;
            this.gameMap = game.getGameMap();
            game.setGameMenu(this);
        } catch (Exception e) {
            System.out.println("Что-то пошло не так. Попробуйте снова.");
        }
    }

    private void printAvailableMaps(){
        try {
            Path mapsDir = Paths.get(MAPS_DIR);

            // Проверяем существование папки
            if (!Files.exists(mapsDir)) {
                System.out.println("Папка '" + MAPS_DIR + "' не найдена. Доступных карт нет.");
                return;
            }

            // Получаем список файлов с расширением .dat
            System.out.println("Доступные карты:");
            Files.list(mapsDir)
                    .filter(path -> path.toString().endsWith(".dat"))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        String mapName = fileName.substring(0, fileName.lastIndexOf(".dat"));
                        System.out.println("- " + mapName);
                    });

        } catch (IOException e) {
            logger.severe("Ошибка при чтении папки maps: " + e.getMessage());
            System.out.println("Не удалось загрузить список карт.");
        }
    }

    private void standartGameCreationMenu(String nickname){
        System.out.println("Введите размеры карты. Её стороны должны быть длиной не менее 5 клеток.");
        int w, h;
        while (true) {
            try {
                System.out.print("Высота карты: ");
                h = Integer.parseInt(scanner.next());
                System.out.print("Ширина карты: ");
                w = Integer.parseInt(scanner.next());
                if (h < 5 || w < 5) {
                    System.out.println("Слишком маленькие размеры карты! Попробуйте снова.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Что то пошло не так. Попробуйте снова.");
            }
        }
        Game game = new Game();
        game.initializeDefaultGame(h, w, nickname);

        this.game = game;
        this.gameMap = game.getGameMap();
        game.setGameMenu(this);
    }

    private void saveGame(String nickname, Scanner scanner) {
        System.out.print("Введите номер слота (1-10): ");
        int slot = Integer.parseInt(scanner.nextLine());
        game.saveToSlot(BASE_DIR, nickname, slot);
    }

    private void loadGame(String nickname, Scanner scanner) {
        System.out.print("Введите номер слота (1-10): ");
        int slot = Integer.parseInt(scanner.nextLine());
        Game game = Game.loadFromSlot(BASE_DIR, nickname, slot);
        this.game = game;
        if (game != null) {
            System.out.println("Игра загружена.");
        } else {
            System.out.println("Ошибка загрузки.");
        }
    }

    private void viewSaves(SaveSystem saveSystem, String nickname) {
        List<Integer> slots = saveSystem.getAvailableSlots(nickname);
        System.out.println("Доступные слоты для игрока " + nickname + ": " + slots);
    }

    private void deleteSave(SaveSystem saveSystem, String nickname, Scanner scanner) {
        System.out.print("Введите номер слота для удаления: ");
        int slot = Integer.parseInt(scanner.nextLine());
        saveSystem.deleteSlot(nickname, slot);
    }
}

