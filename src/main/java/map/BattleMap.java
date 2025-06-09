package map;

import asset.Asset;
import classification.CellClassificator;

public class BattleMap extends GameMap {

    public BattleMap(int sideL){
        super(sideL, sideL, 0, 0, 0, 0);
        this.mapMatrix = generateMapMatrix(sideL);
        this.matrix = generateMapMatrix(sideL);
    }

    Asset[][] generateMapMatrix(int s) {
        Asset[][] result = new Asset[s][s];
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++){
                if (s-i == j && (i-s/2)>2 && (j-s/2)>2){
                    result[i][j] = CellClassificator.createCell(CellClassificator.WALL, null);
                    result[i][j].setX(j);
                    result[i][j].setY(i);
                } else {
                    result[i][j] = CellClassificator.createCell(CellClassificator.GRASS, null);
                    result[i][j].setX(j);
                    result[i][j].setY(i);
                }
            }
        }
        return result;
    }
}
