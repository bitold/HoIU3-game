package game;

import asset.*;
import castle.Castle;
import control.Controller;
import map.GameMap;
import menus.ControllerMenu;
import menus.GameMenu;
import misc.Coordinates;
import misc.Misc;
import player.Computer;
import player.Player;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game implements Serializable {
    String status = "pregame"; // "pregame" "global" "battle" "endgame"
    GameMap gameMap;
    GameMap battleMap;
    Player currentlyMoving;
    transient Scanner scanner = new Scanner(System.in);
    Player player;
    Computer computer;
    Controller controller;
    ControllerMenu controllerMenu;
    Battle battle;
    ArrayList<Hero> battlers = new ArrayList<>();
    GameMenu gameMenu;
    Set<Player> players = new HashSet<>();
    Set<Player> losers = new HashSet<>();
    private static final long serialVersionUID = 1L;
    boolean freshlyLoaded;

    // Пример метода сохранения игры
    public void saveToSlot(String baseDir, String nickname, int slotNumber) {
        String dirPath = baseDir + "/saves/" + nickname;
        String filePath = dirPath + "/slot" + slotNumber + ".dat";

        try {
            // Создаем папку игрока, если её нет
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(dirPath));

            // Сохраняем объект Game в файл
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(this);
                Logger.getLogger(Game.class.getName()).log(Level.INFO,
                        "Игра сохранена в слот {0} для игрока {1}",
                        new Object[]{slotNumber, nickname});
            }
        } catch (IOException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE,
                    "Ошибка сохранения игры в слот " + slotNumber + " для игрока " + nickname, e);
        }
    }

    public static Game loadFromSlot(String baseDir, String nickname, int slotNumber) {
        String filePath = baseDir + "/saves/" + nickname + "/slot" + slotNumber + ".dat";

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Game loadedGame = (Game) ois.readObject();
            Logger.getLogger(Game.class.getName()).log(Level.INFO,
                    "Игра загружена из слота {0} для игрока {1}",
                    new Object[]{slotNumber, nickname});
            return loadedGame;
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE,
                    "Ошибка загрузки игры из слота " + slotNumber + " для игрока " + nickname, e);
            return null;
        }
    }

    public Game(GameMap map){
        this.gameMap = map;
        this.controller = new Controller(map, this);
        controllerMenu = new ControllerMenu(controller);
        controller.setBattling(false);
        gameMenu = new GameMenu(this);
    }
    public Game(){
        setFreshlyLoaded(true);
    }

    public void setFreshlyLoaded(boolean freshlyLoaded) {
        this.freshlyLoaded = freshlyLoaded;
    }


    public void initializeDefaultGame(int height, int width, String nickname){
        // Инициализация сражающихся-пустышек
        battlers.add(null);
        battlers.add(null);

        // Генерация карты и координат замков
        gameMap = new GameMap(height, width);
        ArrayList<Coordinates> castlesCoordinates = Misc.balancedPositionFor2Castles(gameMap);
        int c1x = castlesCoordinates.get(0).getX();
        int c1y = castlesCoordinates.get(0).getY();
        int c2x = castlesCoordinates.get(1).getX();
        int c2y = castlesCoordinates.get(1).getY();
        this.gameMap.setMapMatrix(gameMap.generateMapMatrix(gameMap.getHeight(),gameMap.getWidth(), c1x, c1y, c2x, c2y));
        this.gameMap.setMatrix(gameMap.generateMapMatrix(gameMap.getHeight(),gameMap.getWidth(), c1x, c1y, c2x, c2y));

        // Инициализация игрока
        ArrayList<Hero> heroes = new ArrayList<>();
        player = new Player(heroes, 20, nickname, this);
        player.setCastle(new Castle(true, "И", c1x, c1y, player, gameMap));
        player.addHero(new Hero(0,0,"Δ",10,100,50,1, player));
        player.giveGold(1000);

        // Выдача контроллера игроку (к задумке что при нескольких игроках у каждого был бы свой контроллер, и их был бы целый список?)
        controller = new Controller(gameMap, this);
        controller.setOwner(player);
        this.controllerMenu = controller.getControllerMenu();

        // Ввод дефолтного героя в замок
        player.getCastle().getCastleUI().enterCastle(player.getHeroes().getFirst());

        // Инициализация компьютера
        ArrayList<Hero> computerHeroes = new ArrayList<>();
        computer = new Computer(computerHeroes, 20, player, gameMap, battleMap, this);
        computer.setCastle(new Castle(false, "К", c2x, c2y, computer, gameMap));
        computer.addHero(new Hero(0,0,"∇",10,100,50,1, computer));
        computer.addHero(new Hero(0,0,"∇",10,100,50,1, computer));
        computer.giveGold(1000);
        System.out.println(computer.getNickname());
        computer.acquireWeakArmy();

        gameMap.surroundPointWithFriendlyGrass(c1x, c1y, 3, player);
        gameMap.surroundPointWithFriendlyGrass(c2x, c2y, 3, computer);


        // Добавление игроков в множество игроков
        players.add(player);
        players.add(computer);

        // Получение координат замка
        int cx = computer.getCastle().getX();
        int cy = computer.getCastle().getY();

        // Получение координат замка
        int x = player.getCastle().getX();
        int y = player.getCastle().getY();

        // Размещение замков на "ландшафтной" карте
        gameMap.setMapMatrix(x, y, player.getCastle());
        gameMap.setMapMatrix(cx, cy, computer.getCastle());
        // Размещение на обычной карте
        gameMap.setMatrix(x, y, player.getCastle());
        gameMap.setMatrix(cx, cy, computer.getCastle());

        // Выгрузка всех героев игкрока на карту на вертикали замка
        verticalHeroLoadup(player);

        // Выгрузка всех героев компьютера на карту на вертикали замка
        verticalHeroLoadup(computer);

        // Создание меню
        gameMenu = new GameMenu(this);
        gameMenu.setGameMap(gameMap);
        // Переключение статуса игры
        //status = "buying";
        // Запуск игрового цикла
        currentlyMoving = player;
    }

    public void initializeCustomMapGame(GameMap map, String nickname){
        // Инициализация сражающихся-пустышек
        battlers.add(null);
        battlers.add(null);

        // Генерация карты и координат замков
        gameMap = map;
        ArrayList<Coordinates> castlesCoordinates = Misc.balancedPositionFor2Castles(gameMap);
        int c1x = castlesCoordinates.get(0).getX();
        int c1y = castlesCoordinates.get(0).getY();
        int c2x = castlesCoordinates.get(1).getX();
        int c2y = castlesCoordinates.get(1).getY();

        // Инициализация игрока
        ArrayList<Hero> heroes = new ArrayList<>();
        player = new Player(heroes, 20, nickname, this);
        player.setCastle(new Castle(true, "И", c1x, c1y, player, gameMap));
        player.addHero(new Hero(0,0,"Δ",10,100,50,1, player));
        player.giveGold(1000);


        // Выдача контроллера игроку (к задумке что при нескольких игроках у каждого был бы свой контроллер, и их был бы целый список?)
        controller = new Controller(gameMap, this);
        controller.setOwner(player);
        this.controllerMenu = controller.getControllerMenu();

        // Ввод дефолтного героя в замок
        player.getCastle().getCastleUI().enterCastle(player.getHeroes().getFirst());

        // Инициализация компьютера
        ArrayList<Hero> computerHeroes = new ArrayList<>();
        computer = new Computer(computerHeroes, 20, player, gameMap, battleMap, this);
        computer.setCastle(new Castle(false, "К", c2x, c2y, computer, gameMap));
        computer.addHero(new Hero(0,0,"∇",10,100,50,1, computer));
        computer.addHero(new Hero(0,0,"∇",10,100,50,1, computer));
        computer.giveGold(1000);

        computer.acquireWeakArmy();


        // Размещение дружественной травы вокруг замков
        gameMap.surroundPointWithFriendlyGrass(c1x, c1y, 3, player);
        gameMap.surroundPointWithFriendlyGrass(c2x, c2y, 3, computer);


        // Добавление игроков в множество игроков
        players.add(player);
        players.add(computer);

        // Получение координат замка
        int cx = computer.getCastle().getX();
        int cy = computer.getCastle().getY();

        // Получение координат замка
        int x = player.getCastle().getX();
        int y = player.getCastle().getY();

        // Размещение замков на "ландшафтной" карте
        gameMap.setMapMatrix(x, y, player.getCastle());
        gameMap.setMapMatrix(cx, cy, computer.getCastle());
        // Размещение на обычной карте
        gameMap.setMatrix(x, y, player.getCastle());
        gameMap.setMatrix(cx, cy, computer.getCastle());

        // Выгрузка всех героев игкрока на карту на вертикали замка
        verticalHeroLoadup(player);

        // Выгрузка всех героев компьютера на карту на вертикали замка
        verticalHeroLoadup(computer);

        // Создание меню
        gameMenu = new GameMenu(this);
        gameMenu.setGameMap(gameMap);
        // Переключение статуса игры
        //status = "buying";
        // Запуск игрового цикла
        currentlyMoving = player;
    }

    public void setGameMenu(GameMenu gameMenu) {
        this.gameMenu = gameMenu;
    }

    public void preGame(){
        render();
        gameCycle();
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void addLoser(Player player){
        losers.add(player);
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public Battle getBattle() {
        return battle;
    }

    public void reloadBattlers() {
        battlers.set(0, battle.getHero1());
        battlers.set(1, battle.getHero2());
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public GameMap getBattleMap() {
        return battleMap;
    }

    public void verticalHeroLoadup(Player player){
        GameMap map = player.getCastle().getMap();
        int y = player.getCastle().getY();
        int x = player.getCastle().getX();
        int j = 1;
        int dy = j;
        for (int i = 0; i < player.getHeroes().size(); i++){
            if (i%2 == 0){
                dy = j;
            } else {
                dy = -j;
                j++;
            }
            map.loadEntity(player.getHeroes().get(i), player.getCastle().getX(),y+dy);
        }
    }


    public void render(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
        gameMenu.printStatistics(player);
        gameMenu.printStatistics(computer);
        if (Objects.equals(status, "global")) {
            gameMenu.printMoveInterface(0);
        } else if (Objects.equals(status, "battle")) {
            gameMenu.printMoveInterface(1);
        } else if (Objects.equals(status, "buying")) {
            player.getCastle().getCastleUI().startShop();
            status = "global";
        } else if (Objects.equals(status, "endgame")) {
            gameMenu.printMoveInterface(0);
            gameMenu.printGameOver();
        }
    }

    public void enterCastle(Castle castle, Hero hero){
        castle.getCastleUI().enterCastle(hero);
        setStatus("buying");
        render();
    }


    public void beginBattle(Hero hero1, Hero hero2){
        Battle battle = new Battle(hero1, hero2, this);
        gameMenu.refreshBattleMap();
        controller.setGameMap(battleMap);
        controller.setBattling(true);
        computer.setBattleMap(battleMap);
        battlers.set(0, hero1);
        battlers.set(01, hero2);
        currentlyMoving = hero1.getOwner();
        battlers.get(0).getOwner().setBattler(battlers.get(0));
        battlers.get(1).getOwner().setBattler(battlers.get(1));
        battleCycle();
    }

    public void endBattle(){
        setBattleMap(null);
        setStatus("global");
        controller.setBattling(false);
        controller.setControlled(null);
        controller.setGameMap(gameMap);
        setBattle(null);
        battlers.set(0, null);
        battlers.set(1, null);
    }

    public Player getCurrentlyMoving() {
        return currentlyMoving;
    }

    public boolean isBattling(){
        return !Objects.isNull(battle);
    }

    public Controller getController() {
        return controller;
    }

    public void gameCycle(){
        boolean flag = true;
        while (flag){
            if (checkEngame(currentlyMoving)){
                flag = false;
                break;
            }
            this.render();
            gameMap.getWarden().jailIteration(currentlyMoving);
            flag = giveMove(currentlyMoving);
            if (currentlyMoving.equals(player)){
                currentlyMoving = computer;
            } else {
                currentlyMoving = player;
            }
        }
        gameMenu.printGameOver();
    }


    public void battleCycle(){
        boolean flag = true;
        while (flag){
            if (checkEndBattle(currentlyMoving.getBattler())){
                flag = false;
                break;
            }
            this.render();
            battleMap.getWarden().jailIteration(currentlyMoving);
            flag = giveMove(currentlyMoving);
            if (currentlyMoving.equals(player)){
                currentlyMoving = computer;
            } else {
                currentlyMoving = player;
            }
        }
        System.out.println("Битва окончена");
        currentlyMoving = battlers.getFirst().getOwner();
        endBattle();
    }


    boolean checkEngame(Player player){
        if (player.haveLost() || losers.contains(player)){
            status = "endgame";
            losers.add(player);
            return true;
        }
        return false;
    }

    boolean checkEndBattle(Hero hero){
        if (!hero.isAlive()){
            gameMap.removeEntity(hero);
            hero.expel();
            return true;
        }
        return false;
    }

    public GameMenu getGameMenu() {
        return gameMenu;
    }

    boolean giveMove(Player player){
        if (!freshlyLoaded){
            replenishPlayerEntities(player);
        }
        freshlyLoaded = false;
        if (status.equals("global")){
            player.castleCaptureIteration();
        }

        if (player.getType().equals("COMPUTER")){
            ((Computer)player).simpleBotMove();
            return true;
        }
        if (Objects.equals(status, "global")){
            return controllerMenu.moveEntityControlCycle();
        } else if (Objects.equals(status, "battle")) {
            controllerMenu.setBattlingHero(player.getBattler());
            return controllerMenu.moveEntityControlCycle();
        } else if (Objects.equals(status, "endgame")) {
            return false;
        } else{
            System.out.println("WRONG GIVE MOVE CALL");
        }
        return false;
    }

    Hero getCurrentlyMovingBattler(){
        for (Hero hero: battlers){
            if (hero.getOwner().equals(currentlyMoving)){
                return hero;
            }
        }
        return null;
    }

    void replenishPlayerEntities(Player player){
        if (Objects.equals(status, "global")){ replenish((Player) player); } else if (Objects.equals(status, "battle")) {
            battlers.get(0).replenish();
            battlers.get(1).replenish();
        }
    }




    boolean entityIsHeroCapturingCastle(Entity entity){
        return (entity.getType().equals("hero") && ((Hero)entity).isBusyCapturing());
    }

    public void replenish(Player player){
        player.getCastle().replenishIfNoCaptor();
        ArrayList<Hero> heroes = player.getHeroes();
        for (Hero value : heroes) {
            ((Entity) value).setCurrentMP(((Entity) value).getDefaultMP());
        }
    }

    boolean entityMovementCycle(Entity entity){
        if (entity.getCurrentMP() > 0){
            System.out.println("УПРАВЛЕНИЕ СУЩНОСТЬЮ " + entity.getDesign());
            if (!giveControl(entity)){
                return false;
            }
            this.render();
            return true;
        }
        return false;
    }


    boolean giveControl(Entity entity){
        gameMenu.printGivingConrtol(entity);
        controller.setControlled(entity);
        return controller.control();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setBattleMap(GameMap battleMap) {
        this.battleMap = battleMap;
        gameMenu.refreshBattleMap();
    }

    public Set<Player> getLosers() {
        return losers;
    }

    /*   boolean controlCycleThroughEntities(Object player, ArrayList<? extends Entity> entities){
        if (entities.isEmpty()){
            System.out.println("Передано управление пустой армией.");
            return false;
        }
        if (player instanceof Computer){
            return true;
        }

        if (Objects.equals(status, "global")){ replenish((Player) player); } else if (Objects.equals(status, "battle")) {
            battlers.get(0).replenish();
            battlers.get(1).replenish();
        }

        boolean flag = true;
        for (int i = 0; i < entities.size(); i++){
            flag = true;
            Entity entity = (Entity)entities.get(i);
            if (entity.getCurrentMP() <= 0 || entityIsHeroCapturingCastle(entity)){
                continue;
            } else {
                while (flag) {
                    flag = entityMovementCycle(entity);
                }
                System.out.println("Сущность " + entity.getDesign() + " устала");
                gameMenu.printUnitExhausted();
            }
            gameMenu.printGivingMove();
        }
        return true;
    }*/
    /*    void keyboardHandler(Entity entity){
        System.out.println("Ваш ход: ");
        String input = scanner.nextLine();
        System.out.println("Вы сделали ход " + input + ". Передача хода в обработчик...");
        act(entity, input);
    }
    String estimateMoves(Entity entity){
        int x = entity.getX();
        int y = entity.getY();
        int Nx = x; int Ny = y+1;
        int Sx = x; int Sy = y-1;
        int Wx = x-1; int Wy = y;
        int Ex = x+1; int Ey = y;

        String result = "У вас нет ходов.";
        for (int i = 0; i < 4; i++){
            break;
        }
        return result;
    }
    void proceedToCell(Entity entity, Asset cell, int dx, int dy){
        gameMap.increlocate(entity, dx, dy);
        entity.setCurrentMP(entity.getCurrentMP()-((Cell)cell).getCost());
        this.render();
    }*/
    /*void estimateMove(Entity entity, int dx, int dy){
        int x = entity.getX();
        int y = entity.getY();
        x+=dx;
        y+=dy;
        if (x < 0 || y < 0 || x >= gameMap.getWidth() || y>=gameMap.getHeight()){
            System.out.println("Невозможно пройти наружу");
        }
        Asset asset = gameMap.getAsset(x, y);
        if (asset.getType() == "cell"){
            proceedToCell(entity, asset, dx, dy);
        } else if (asset.getType() == "entity") {
            boolean side = ((Entity)asset).getSide();
            if (!side){
                if (entity.getType() == "unit"){
                    System.out.println("Атаковать юнита " + ((Unit)asset).getType() + "?   \"Да\" - F, \"Отмена\" - C");
                    String input = scanner.nextLine();
                    attackHandler(input, (Unit) entity,(Unit) asset, "attack");
                } else {
                    System.out.println("Впереди враждебный герой. Начать сражение?    \"Да\" - F, \"Отмена\" - C");
                    String input = scanner.nextLine();
                    attackHandler(input, (Unit) entity,(Unit) asset, "battle");
                }
            } else {
                System.out.println("В этом месте уже стоит союзный персонаж.");
            }
        }
    }*/
   /* void attackHandler(String input, Asset attacker, Asset defender, String type){
        while (true){
            if (input == "F") {
                if (type == "attack"){
                    beginBattle((Hero) attacker, (Hero) defender);
                    return;
                }
                int dmg = ((Unit)attacker).getDamage();
                ((Unit)defender).setHealth(((Unit)defender).getHealth()-dmg);
                ((Unit)defender).checkHealth();
                return;
            } else if (input == "C") {
                return;
            } else {
                System.out.println("Введите F для атаки или C для отмены.");
            }
        }

    }*/
    /*void act(Entity entity, String action){
        switch (action) {
            case ("W"):
                estimateMove(entity, 0, -1);
                break;
            case ("A"):
                estimateMove(entity, -1, 0);
                break;
            case ("S"):
                estimateMove(entity, 0, 1);
                break;
            case ("D"):
                estimateMove(entity, 1, 0);
                break;
            default:
                break;
        }
    }
*/
}
