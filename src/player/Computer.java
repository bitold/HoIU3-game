package player;

import asset.*;
import castle.Castle;
import classification.UnitClassificator;
import game.Game;
import map.GameMap;
import misc.Coordinates;
import misc.Locator;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public class Computer extends Player {
    Player enemy;
    GameMap gameMap;
    GameMap battleMap;
    boolean move;
    public Computer(ArrayList<Hero> heroes, int gold, Player enemy, GameMap gameMap, GameMap battleMap, Game game){
        super(heroes, gold, "Computer", game);
        this.enemy = enemy;
        this.gameMap = gameMap;
        this.battleMap = battleMap;
        this.type = "COMPUTER";
    }

    public void setBattleMap(GameMap battleMap) {
        this.battleMap = this.getGame().getBattleMap();
    }
    public void acquireNormalArmy(){
        if (this.getHeroes().isEmpty()){
            return;
        } else {
            Hero hero = this.getHeroes().getFirst();
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero = this.getHeroes().getLast();
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
            hero.addUnit(UnitClassificator.createUnitByClass("paladin", hero));
        }
    }
    public void acquireWeakArmy(){
        if (this.getHeroes().isEmpty()){
            return;
        } else {
            Hero hero = this.getHeroes().getFirst();
            hero.addUnit(UnitClassificator.createUnitByClass("swordsman", hero));
            hero = this.getHeroes().getLast();
            hero.addUnit(UnitClassificator.createUnitByClass("swordsman", hero));
        }
    }

    /**
     * Цель этого метода: двинуться к замку игрока максимально "простым" путём - если на одной вертикали
     * с замком, спускаться/подниматься, если на одной горизонтали, идти влева/вправо. Если ни то, ни другое,
     * определить горизонталь замка и подняться/спуститься на неё.
     * Если на пути герой игрока - начать сражение.
     */
    public void makeBestHeroMove(Hero hero){
        int castleX = enemy.getCastle().getX();
        int castleY = enemy.getCastle().getY();
        int x = hero.getX();
        int y = hero.getY();
        System.out.println("МОИ КООРДИНАТЫ - (" + x + ", " + y + ")");
        System.out.println("ОБНАРУЖЕН ЗАМОК С КООРДИНАТАМИ (" + castleX + ", " + castleY + "). РАССЧИТЫВАЮ ТРАЕКТОРИЮ ДВИЖЕНИЯ...");
        //ArrayList<Integer> dQ = estimateRightAngleMove(x, y, castleX, castleY);
        Coordinates dQ = estimateUnitSpacialMove(hero.getCoordinates(), enemy.getCastle().getCoordinates());
        System.out.println("ТРАЕКТОРИЯ РАССЧИТАНА, ВЕКТОР ПЕРЕМЕЩЕНИЯ (" + dQ.get(1) + ", " + dQ.get(2) + ")");
        evaluateSimplestActionForAnyMovingEntity(hero, dQ);
    }

    /**
     * Выполняет простейшее ДЕЙСТВИЕ в указанном направлении. Грубо говоря,
     * пытается сходить в указанную сторону и взаимодействует/атакаует всё
     * что попадается на пути.
     */
    public void evaluateSimplestActionForAnyMovingEntity(Entity entity, Coordinates dQ){
        Asset cell = Locator.getCellWithDqOffset(entity, dQ);
        System.out.println("сущность бота " + entity + " хочет сместиться на " + dQ);

        if (Objects.isNull(cell)){
            proceedToCell(entity, dQ);
            return;
        } else {

        System.out.println("в точке перемещения находится " + cell + " " + cell.getCoordinates());}
        switch (cell.getType()){
            case "cell":
                proceedToCell(entity, dQ);
                break;
            case "castle":
                analyzeCastle((Hero) entity, (Castle) cell);
                break;
            case "unit":
                tryToAttack(entity, (Unit) cell);
                break;
            case "hero":
                if (checkFF(entity, (Hero)cell)){
                    entity.setCurrentMP(0);
                    break;
                }
                proceedToBattle(entity, (Hero) cell);
                break;
        }
    }



    public Coordinates leftMoveDq(){
        Coordinates result = new Coordinates();
        result = result.withComponent(1, -1).withComponent(2, 0);
        return result;
    }
    void analyzeCastle(Hero hero, Castle castle){
        if (castle.getOwner().equals(hero.getOwner())){
            evaluateSimplestActionForAnyMovingEntity(hero, leftMoveDq());
        } else {
            startCapturing(hero, castle);
        }
    }

    public void simpleBotMove() {
        move = true;
        getGame().replenish(this);
        System.out.println("БОТУ ДАЛИ ХОД !! ! !! !  !!  !! ! ! ! ! ! ! ! ! ! !   ! !! !  ! ! !");
        int i = 0;
        if (getGame().getStatus().equals("global")){
            while (i < this.getHeroes().size()){
                heroMoveCycle(this.getHeroes().get(i));
                i++;
            }
        }
        if (getGame().getStatus().equals("battle")){
            for (Unit unit: getGame().getBattle().getHeroBelongingToComputer().getArmy()){
                unitMoveCycle(unit);
            }
        }
    }

    void sleep(){
        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void heroMoveCycle(Hero hero){
        if (hero.isBusyCapturing()){
            return;
        }
        System.out.println("Бот начинает ходить героем " + hero.getDesign());
        while (hero.isAlive() && hero.getCurrentMP() > 0 && !hero.isBusyCapturing()){
            sleep();
            makeBestHeroMove(hero);
            getGame().render();
        }
    }
    public void unitMoveCycle(Unit unit){
        while (unit.isAlive() && unit.getCurrentMP() > 0 && move){
            sleep();
            makeBestUnitMove(unit);
            getGame().render();
        }
    }
    void stop(){
        move = false;
    }
    // Цель этого метода: вызвать "локатор" вражеских юнитов, выбрать того из них, евклидово расстояние
    // до которого меньше всего, идти в его направлении "простым" путём в виде прямого угла. Если на пути вражеский
    // юнит - атаковать, пробовать отступать в обратном атаке направлении, если на пути что угодно - пропуск хода.
    public void makeBestUnitMove(Unit unit){
        Unit closest = Locator.findClosestEnemyUnit(unit, battleMap);
        if (Objects.isNull(closest)){
            stop();
            return;
        }
        Coordinates dQ = estimateUnitSpacialMove(unit.getCoordinates(), closest.getCoordinates());
        evaluateSimplestActionForAnyMovingEntity(unit, dQ);
    }

    public void proceedToCell(Entity entity, Coordinates dQ){
        Asset cell = Locator.getCellWithDqOffset(entity, dQ);
        entity.getMap().relocate(entity, entity.getCoordinates().add(dQ));
        entity.takeMovementPointsForMovingTo((Cell)cell);
    }

    public void tryToAttack(Entity entity, Unit target){
        if (!checkFF(entity, target) && entity.isAbleToAttack()){
            target.dealDamage(entity.getDamage());
            target.checkLiving();
        }
        entity.setAbleToAttack(false);
        entity.setCurrentMP(0);
    }

    boolean checkFF(Entity entity1, Entity entity2){
        return entity1.getOwner().equals(entity2.getOwner());
    }

    public void proceedToBattle(Entity entity, Hero hero){
        getGame().beginBattle((Hero)entity, hero);
    }

    public void startCapturing(Hero hero, Castle castle){
        hero.takeAVisit(castle);
        this.addCaptor(hero);
        hero.setCurrentMP(0);
    }

    /**
     * Определяет направление хода для бота, двигающегося примитивно по прямому углу на плоскости.
     */
    ArrayList<Integer> estimateRightAngleMove(int x1, int y1, int x2, int y2){
        // me
        ArrayList<Integer> result = new ArrayList<>();
        if (y1 != y2){
            result.add(0);
            result.add((y2-y1)/abs(y2-y1));
        }
        else if (x1 != x2){
            result.add((x2-x1)/abs(x2-x1));
            result.add(0);
        }
        return result;
    }

    /**
     * Определяет направление хода для бота, двигающегося по прямой в пространстве.
     */
    Coordinates estimateUnitSpacialMove (Coordinates current, Coordinates target) {
        Coordinates result = target.subtract(current);
        System.out.println("Рассчитанный вектор движения " + result);
        Optional<Integer> dirInt = getRandomSetElement(result.getAxi());
        int dir = dirInt.orElse(42);
        System.out.println("Избранная для движения ось " + dir);
        Coordinates result_move = Coordinates.project(result, dir);
        result_move = Coordinates.integerNormalization(result_move);
        return result_move;
    }


    public static <T> Optional<T> getRandomSetElement(Set<T> set) {
        if (set.isEmpty()) {
            return Optional.empty();
        }
        List<T> list = new ArrayList<>(set);
        return Optional.of(list.get(new Random().nextInt(list.size())));
    }
}
