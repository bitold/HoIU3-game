package control;

import asset.*;
import castle.Castle;
import classification.CellClassificator;
import game.Game;
import map.GameMap;
import menus.ControllerMenu;
import misc.Coordinates;
import misc.Locator;
import player.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Controller {
    GameMap gameMap;
    Entity controlled;
    Scanner scanner;
    boolean isBattling;
    Game game;
    ArrayList<Unit> targets = new ArrayList<>();
    Player owner;
    ControllerMenu controllerMenu;
    boolean move;

    public Controller(GameMap gameMap, Game game){
        this.gameMap=gameMap;
        this.scanner = new Scanner(System.in);
        this.game = game;
    }


    public void setMove(boolean move) {
        this.move = move;
    }

    // Главный метод данного класса. Выполняет весь цикл управления сущностью.
    public boolean control(){
        if (!move){
            return false;
        }
        if (Objects.isNull(controlled)){
            return false;
        }
        if (isCapturing()){
            System.out.println("В настоящий момент этот герой захватывает замок. Вы хотите прервать захват? \n Y. Да        N. Нет");
            if (!processStopCapturingInput(keyboardHandler())){
                controlled = null;
                return false;
            }
        }
        if (controlled.isExhausted()){
            printCanNoLongerMove();
            controlled = null;
            return false;
        }
        printMoveMenu();
        return processInput(keyboardHandler());
    }

    public Game getGame() {
        return game;
    }

    public void setControllerMenu(ControllerMenu controllerMenu) {
        this.controllerMenu = controllerMenu;
    }

    void printCanNoLongerMove(){
        System.out.println("Эта сущность уже не в силах ходить!");
    }
    void printMoveMenu(){
        System.out.println("""
                Ваш ход.
                   -2  \s
                -1     1
                    2  \s""");
        System.out.println("Чтобы полностью пропустить ход введите /PASS");
        System.out.println("Чтобы выбрать другого юнита введите /PICK");
        if (isRangedUnit()){
            System.out.println("Юнит дальнобоен, ");
            rangedUnitAttackListHandler();
        }
        System.out.println("Введите действие: ");
        System.out.print("> ");
    }

    boolean processStopCapturingInput(String input){
        if (input.equals("Y")){
            ((Hero)controlled).expelFromCastle();
            return true;
        } else if (input.equals("N")){
            return false;
        }
        return false;
    }


    boolean isRangedUnit(){
        return (controlled.getType().equals("unit") && controlled.getAttackRange() > 1);
    }

    public String keyboardHandler(){
        return scanner.nextLine();
    }


    public void printCantShoot(){
        System.out.println("Этот юнит не может атаковать далеко.");
    }

    public boolean processInput(String input){
        if (input.equals("/PASS")){
            controllerMenu.setMove(false);
            return false;
        }
        if (input.equals("/PICK")){
            controllerMenu.pickEntity();
            return true;
        }
        if (input.startsWith("/A ")){
            if (!isRangedUnit()){
                printCantShoot();
            }
            String target = input.substring(3);
            int targetNum = stringToInt(target);
            tryShooting(targetNum);
            return true;
        }
        int direction = stringToInt(input);
        if (abs(direction)>0){
            Coordinates newCoords = controlled.getCoordinates().add(Coordinates.createUnitVector(abs(direction), direction > 0));
            checkCoordinates2(newCoords, gameMap);
        }
        return true;
    }
    void tryShooting(int targetNum){
        if (targets.size() <= targetNum){
            printWrongTarget();
            return;
        }
        attackHandler(targets.get(targetNum));
    }

    void printWrongTarget(){
        System.out.println("Неверный выбор цели.");
    }

    boolean isCapturing(){
        if (controlled.getType().equals("hero")){
            return ((Hero)controlled).isBusyCapturing();
        }
        return false;
    }

    void printIsArrested(){
        System.out.println("Страж Пространства не позволяет сущности " + controlled.getDesign() + " сделать этот ход.");
    }

    void checkCoordinates2(Coordinates coordinates, GameMap map){
        Coordinates coordinates1 = coordinates;
        if (controlled.isSpaciallyArrested() && !controlled.isLegalToMove(coordinates)){
            printIsArrested();
            return;
        }
        if (Coordinates.isOutOfBounds(coordinates, map)){
            printOutOfBounds();
            coordinates1 = Coordinates.boundaryLoop(controlled.getCoordinates(), coordinates, map);
        }
        Entity entity = map.getNon2DEntity(coordinates1);
        if (Objects.isNull(entity)){
            if (coordinates1.isNormally2D()){
                Asset asset = map.getAsset(coordinates1.getX(), coordinates1.getY());
                if (asset.getType().equals("castle")){
                    castleInteractionHandler((Castle) map.getAsset(coordinates1.getX(), coordinates1.getY()));
                } else if (asset.getType().equals("cell") && ((Cell)asset).isWall()) {
                    printWall();
                    return;
                } else {
                    proceedToCell2(gameMap.getMapMatrix()[coordinates1.getY()][coordinates1.getX()], coordinates1);
                }
            } else {
                proceedToCell2(CellClassificator.createCell(CellClassificator.ROAD, null), coordinates1);
            }
        } else {
            if (!isBattling) {
                battleHandler((Hero) entity);
            } else {
                attackHandler(entity);
            }
        }
    }
    void printWall() {
        System.out.println("Впереди непроходимая преграда.");
    }

    void castleInteractionHandler(Castle castle){
        if (castle.getOwner().equals(((Hero)controlled).getOwner())){
            castleEnterHandler(castle);
        } else if (!(castle.getOwner().equals(((Hero)controlled).getOwner()))){
            castleCaptureHandler(castle);
        }
    }

    boolean castleEnterHandler(Castle castle){
        printCastleEnterRequest();
        String input = keyboardHandler();
        if (input.equals("Y")){
            game.enterCastle(castle, (Hero)controlled);
        } else if (input.equals("N")){
            return false;
        } else {
            return false;
        }
        return false;
    }

    boolean castleCaptureHandler(Castle castle){
        if (castle.isBeingCaptured()){
            printCastleAlreadyBeingCaptured();
            return false;
        }
        printCastleCaptureRequest();
        String input = keyboardHandler();
        if (input.equals("Y")){
            owner.addCaptor((Hero)controlled);
            ((Hero) controlled).setCurrentCastle(castle);
            controlled.setCurrentMP(0);
            castle.setVisitor((Hero)controlled);
            move = false;
            printCastleCaptureBegan();
            controlled = null;
            return true;
        } else if (input.equals("N")){
            return false;
        } else {
            return false;
        }
    }

    void printCastleAlreadyBeingCaptured(){
        System.out.println("Этот замок уже захватывает другой герой.");
    }

    void printCastleCaptureBegan(){
        System.out.println(((Hero)controlled).toStringShortened() + " начал захват замка.");
    }

    void evaluateOwner(){
        if (Objects.isNull(controlled)){ return;}
        if (controlled.getType().equals("unit")){
            owner = ((Unit)controlled).getHero().getOwner();
        } else if (controlled.getType().equals("hero")) {
            owner = ((Hero)controlled).getOwner();
        }
    }

    void printCastleEnterRequest(){
        System.out.println("Вы хотите войти в замок?\n" +
                "Y. Войти       N. Отмена");
    }

    void printCastleCaptureRequest(){
        System.out.println("Вы хотите захватить замок?\n" +
                "Y. Начать захват       N. Отмена");
    }

    void printOutOfBounds(){
        System.out.println("Это граница игрового поля");
    }

    boolean isOutOfBounds(int x, int y, GameMap map){
        return x >= map.getWidth() || x < 0 || y >= map.getHeight() || y < 0;
    }

    boolean attackConfirmationHandler(Unit defender){
        System.out.println("Вы хотите атаковать юнита " + defender.getDesign() + "?\n" + "Y. Да      N. Отмена" );
        String input = scanner.nextLine();
        return (Objects.equals(input, "Y"));
    }
    boolean battleConfirmationHandler(Hero defender){
        System.out.println("Вы хотите начать сражение с героем " + defender.getDesign() + "?\n" + "Y. Да      N. Отмена" );
        String input = scanner.nextLine();
        return (Objects.equals(input, "Y"));
    }

    void battleHandler(Hero defender){
        if (((Hero)controlled).getOwner().equals(defender.getOwner())){
            printFriendlyFire();
            return;
        } else if(battleConfirmationHandler(defender)) {
            game.beginBattle((Hero)controlled, defender);
        }
    }


    void attackHandler(Entity defender){
        if (((Unit)controlled).getHero() == ((Unit)defender).getHero()){
            printFriendlyFire();
            return;
        } else if (controlled.isAbleToAttack()){
            if (attackConfirmationHandler((Unit)defender)){
                defender.dealDamage(controlled.getDamage());
                game.getGameMenu().printDealtDamage((Unit)defender, controlled.getDamage());
                controlled.setAbleToAttack(false);
                ((Unit) defender).checkLiving();
            }
        } else {
            printUnableToAttack();
        }
    }

    void printFriendlyFire(){
        System.out.println("В этом месте стоит союзный юнит.");
    }
    void printUnableToAttack(){
        System.out.println("Юнит больше не может атаковать в этот ход.");
    }

    void proceedToCell2(Asset cell, Coordinates coordinates){
        gameMap.relocate(controlled, coordinates);
        controlled.takeMovementPointsForMovingTo((Cell)cell);
        game.render();
    }

    public static int stringToInt(String input) {
        //System.out.println("Попытка перевода в целое число строки " + input);
        //System.out.println("Длина строки: " + input.length());
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return input.hashCode();
        }
    }

    public void setControlled(Entity controlled) {
        this.controlled = controlled;
        evaluateOwner();
    }

    public Entity getControlled() {
        return controlled;
    }

    public void setBattling(boolean battling) {
        isBattling = battling;
    }

    public boolean isBattling() {
        return isBattling;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }



    public void rangedUnitAttackListHandler(){
        int range = controlled.getAttackRange();
        targets = (ArrayList<Unit>) Locator.locateUnitsInRange(controlled, range, gameMap).stream().filter(unit -> !unit.getOwner().equals(controlled.getOwner())).collect(Collectors.toList());
        printOutPossibleTargetsMenu();
    }

    public void printOutPossibleTargetsMenu(){
        if (targets.size() < 1){
            System.out.println("Но в радиусе атаки целей нет.");
            return;
        }
        System.out.println("Доступные для атаки цели:");
        for (int i = 0; i < targets.size(); i++) {
            System.out.println(i + ". Юнит " + targets.get(i).getDesign() + ", координаты: (" +
                    targets.get(i).getX() + ", " + targets.get(i).getY() + ")");
        }
        System.out.println("Для дальней атаки введите /A и номер юнита из списка.");
    }


/*    void checkCoordinates(int x, int y, GameMap map){
        if (isOutOfBounds(x,y,map)){
            printOutOfBounds();
            return;
        }
        Asset asset = map.getAsset(x,y);
        if (asset.getType().equals("cell")) {
            proceedToCell(asset, x, y);
        } else if (asset.getType().equals("castle")){
            castleInteractionHandler((Castle) asset);
        } else {
            if (!isBattling) {
                battleHandler((Hero) asset);
            } else {
                attackHandler((Entity) asset);
            }
        }
    }

    void proceedToCell(Asset cell, int x, int y){
        int dx = x-controlled.getX();
        int dy = y-controlled.getY();
        Coordinates dQ = new Coordinates(Map.of(1, dx, 2, dy));
        //System.out.println("Сущность продвигается на клетку. ds = (" + dx + ", " + dy + ")");
        //gameMap.increlocate(controlled, dx, dy);
        gameMap.relocate(controlled, controlled.getCoordinates().add(dQ));
        controlled.takeMovementPointsForMovingTo((Cell)cell);
        game.render();
    }*/
}
