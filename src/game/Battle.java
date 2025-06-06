package game;

import asset.Hero;
import asset.Unit;
import map.BattleMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Battle implements Serializable {
    private BattleMap battleMap;
    private Hero hero1; // герой игрока
    private Hero hero2;
    private Game game;
    private ArrayList<Unit> bodies;

    public Battle(Hero hero1, Hero hero2, Game game){
        this.hero1 = hero1;
        this.hero2 = hero2;
        this.battleMap = new BattleMap(10);
        this.game = game;
        game.setBattleMap(battleMap);
        game.setStatus("battle");
        game.setBattle(this);
        //game.reloadBattlers();
        loadUpBattleMap();
    }


    void loadUpBattleMap(){
        ArrayList<Unit> army1 = hero1.getArmy();
        int army1size = army1.size();
        System.out.println(army1);
        ArrayList<Unit> army2 = hero2.getArmy();
        int army2size = army2.size();
        System.out.println(army2);
        int x, y;

        while (army1size > 0){
            for (int n = 1; n<battleMap.getHeight(); n++){
                if (army1size <= 0){
                    break;
                }
                x = 0;
                y = n;
                for (int i = 0; i<=n; i++){
                    if (army1size <= 0){
                        break;
                    }
                    battleMap.loadEntity(army1.get(army1size-1), x, y);
                    System.out.println("Выгрузка юнита " + army1.get(army1size-1) + " на координаты " + x + ", " + y);
                    x++;
                    y--;
                    army1size--;
                }
            }
        }
        while (army2size > 0){
            for (int n = 1; n<battleMap.getHeight(); n++){
                if (army2size <= 0){
                    break;
                }
                x = battleMap.getWidth()-1-n;
                y = battleMap.getHeight()-1;
                for (int i = 0; i<=n; i++){
                    if (army2size <= 0){
                        break;
                    }
                    battleMap.loadEntity(army2.get(army2size-1), x, y);
                    System.out.println("Выгрузка юнита " + army2.get(army2size-1) + " на координаты " + x + ", " + y);
                    x++;
                    y--;
                    army2size--;
                }
            }
        }
    }

    public BattleMap getBattleMap() {
        return battleMap;
    }


    public Hero getHero1() {
        return hero1;
    }

    public Hero getHero2() {
        return hero2;
    }

    public Hero getHeroBelongingToComputer(){
        if (hero1.getOwner().getType().equals("COMPUTER")){
            return hero1;
        }
        if (hero2.getOwner().getType().equals("COMPUTER")){
            return hero2;
        }
        return null;
    }

    // Метод возвращает победителя, если он есть, и null, если битва не окончена
    public Hero isOver(){
        int army1size = hero1.getArmySize();
        int army2size = hero2.getArmySize();
        if (army2size == 0 && army1size!=0){
            return hero1;
        } else if (army1size == 0 && army2size!=0){
            return hero2;
        }
        return null;
    }
}
