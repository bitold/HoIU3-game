package main;

import asset.Asset;
import map.GameMap;
import game.Game;
import menus.StarterMenu;


public class LAB1 extends Asset {
    public static void main(String[] args) {
        //,0,10,29,10
        StarterMenu startMenu = new StarterMenu();
        startMenu.startMenu();



//        GameMap map = new GameMap(15,30);
//        Game game = new Game(map);
//        game.preGame(10, 10);
    }
}
