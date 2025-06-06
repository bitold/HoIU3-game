package menus;

import asset.Entity;
import asset.Hero;
import asset.Unit;
import castle.Castle;
import game.Game;
import map.GameMap;
import misc.Coordinates;
import player.Player;

import java.util.*;

public class GameMenu {
    Game game;
    GameMap gameMap;
    GameMap battleMap;
    private Scanner scanner = new Scanner(System.in);

    public GameMenu(Game game){
        this.game = game;
        this.gameMap = game.getGameMap();
        this.battleMap = game.getBattleMap();
    }

    public void refreshBattleMap(){
        this.battleMap = game.getBattleMap();
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
}

