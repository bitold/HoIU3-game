package misc;

import map.GameMap;

import java.util.ArrayList;

import static java.lang.Math.ceil;

public class Misc {
    public static ArrayList<Coordinates> balancedPositionFor2Castles(GameMap map){
        ArrayList<Coordinates> result = new ArrayList<>();
        Coordinates c1 = new Coordinates();
        Coordinates c2 = new Coordinates();
        int h = map.getHeight();
        int w = map.getWidth();
        int w1 = (int) ceil((w-2)/4);
        int h1 = (int) ceil((h-1)/2);
        c1=c1.withComponent(1, w1);
        c2=c2.withComponent(1, w1+1+2*w1);
        c1=c1.withComponent(2, h1);
        c2=c2.withComponent(2, h1);
        result.add(c1);
        result.add(c2);
        return result;
    }
}
