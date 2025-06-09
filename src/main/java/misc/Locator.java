package misc;

import asset.Asset;
import asset.Entity;
import asset.Unit;
import map.GameMap;
import player.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Locator {
    public static ArrayList<Unit> locateUnitsInRange(Asset center, int range, GameMap gameMap){
        int x = center.getX();
        int y = center.getY();

        ArrayList<Unit> result = new ArrayList<>();
        Asset[][] matrix = gameMap.getMatrix();


        for (int i = -range; i <= range; i++){
            for (int j = -range; j <= range; j++){
                if ((y+i >= 0) && (x+j >= 0) && ((x+j) < gameMap.getWidth()) && ((y+i) < gameMap.getHeight())){
                    //System.out.println("Рассмотрение координат " + (x+j) + ", " + (y+i) + " на карте размерами " + gameMap.getHeight() + "X" + gameMap.getWidth());
                    Asset target = matrix[y+i][x+j];
                    if (target.getType().equals("unit") && distance(center, target) <= range){
                        result.add((Unit) target);
                    }
                }
            }
        }
        return result;
    }

    public static ArrayList<Unit> locateAllEnemies(Asset center, GameMap gameMap){
        Player owner = ((Entity)center).getOwner();

        ArrayList<Unit> result = new ArrayList<>();
        Asset[][] matrix = gameMap.getMatrix();
        int size = gameMap.getHeight();
        HashMap<Coordinates, Entity> entities = gameMap.getEntitiesMap();
        for (Entity e: entities.values()){
            if (e.getType().equals("unit") && !((Entity)e).getOwner().equals(owner)){
                result.add((Unit)e);
            }
        }

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                Asset currentAsset = matrix[i][j];
                if (currentAsset.getType().equals("unit") && !((Entity)currentAsset).getOwner().equals(owner)){
                    result.add((Unit)currentAsset);
                }
            }
        }

        return result;
    }

    public static Unit findClosestEnemyUnit(Asset center, GameMap battleMap){
        int mapSize = battleMap.getHeight();
        ArrayList<Unit> enemies = locateAllEnemies(center, battleMap);
        System.out.println("ОБНАРУЖЕНО ВРАГОВ: " + enemies.size());
        Unit closest = null;
        double minDist = mapSize+10;
        double dist;
        for (Unit enemy: enemies){
            dist = distance(center, enemy);
            System.out.println("РАССТОЯНИЕ ДО ВРАГА " + dist);
            if (dist <= minDist){
                System.out.println("БЛИЖЕ ЧЕМ " + minDist + ". ПЕРЕОПРЕДЕЛЯЮ БЛИЖАЙШЕГО.");
                closest = enemy;
                minDist = dist;
            } else {
                System.out.println("ДАЛЕКОВАТ.");
            }
        }
        return closest;
    }


    public static double distance(Asset center, Asset target){
//        double x1 = target.getX();
//        double y1 = target.getY();
//        double x2 = center.getX();
//        double y2 = center.getY();
        //System.out.println("Вызов функции distance, Координаты центра " + center.getCoordinates() + " Координаты цели " + target.getCoordinates() + " Разность " + center.getCoordinates().subtract(target.getCoordinates()));
        return (Coordinates.norm(center.getCoordinates().subtract(target.getCoordinates())));
        //return sqrt(pow(x1 - x2, 2) + pow(y1-y2, 2));
    }

    public static Asset getCellWithDqOffset(Entity entity, ArrayList<Integer> dQ){
        return entity.getMap().getMatrix()[entity.getY()+dQ.get(1)][entity.getX()+dQ.get(0)];
    }

    public static Asset getCellWithDqOffset(Entity entity, Coordinates dQ){
        Coordinates newcoords = entity.getCoordinates().add(dQ);
        System.out.println("новые координаты = " + newcoords);
        if ((newcoords.isNormally2D())){
            return entity.getMap().getMatrix()[entity.getY()+dQ.get(2)][entity.getX()+dQ.get(1)];
        } else {
            return entity.getMap().getNon2DEntity(newcoords);
        }
    }
}

