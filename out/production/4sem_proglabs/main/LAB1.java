package main;

import asset.Asset;
import map.GameMap;
import game.Game;


public class LAB1 extends Asset {
    public static void main(String[] args) {
        //,0,10,29,10
        GameMap map = new GameMap(15,30);
        Game game = new Game(map);
        game.preGame();
    }
}
