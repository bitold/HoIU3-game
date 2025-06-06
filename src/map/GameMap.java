package map;

import asset.Asset;
import asset.Cell;
import asset.Entity;
import castle.Castle;
import classification.CellClassificator;
import misc.Coordinates;
import misc.Locator;
import misc.SpaceWarden;
import player.Player;

import java.io.*;
import java.util.logging.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.lang.Math.ceil;

public class GameMap implements Serializable {
    private static final Logger logger = Logger.getLogger(GameMap.class.getName());

    int width;
    int height;
    Asset[][] mapMatrix;
    Asset[][] matrix;
    private final HashMap<Coordinates, Entity> entitiesMap = new HashMap<>();
    SpaceWarden warden = new SpaceWarden();

    GameMap(){

    }

    @Override
    public String toString() {
        return "Map{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    public GameMap(int h, int w, int x1, int y1, int x2, int y2){
        this.width = w;
        this.height = h;
        this.mapMatrix = generateMapMatrix(h,w,x1,y1,x2,y2);
        this.matrix = generateMapMatrix(h,w,x1,y1,x2,y2);
    }
    public GameMap(int h, int w){
        this.width = w;
        this.height = h;
    }

    public Asset[][] getMatrix(){
        return matrix;
    }

    public Asset getAsset(int x, int y){
        return matrix[y][x];
    }

    public void setMapMatrix(int x, int y, Asset asset){
        mapMatrix[y][x] = asset;
    }

    public void setMapMatrix(Asset[][] mapMatrix) {
        this.mapMatrix = mapMatrix;
    }

    public Asset[][] getMapMatrix() {
        return mapMatrix;
    }

    public void setMatrix(int x, int y, Asset asset) {
        matrix[y][x] = asset;
    }

    public void setMatrix(Asset[][] matrix) {
        this.matrix = matrix;
    }

    public HashMap<Coordinates, Entity> getEntitiesMap() {
        return entitiesMap;
    }



    public Entity getNon2DEntity(Coordinates coordinates){
//        System.out.println("Запрошена сущность с координатами " + coordinates + " hashCode которых " + coordinates.hashCode());
//        System.out.println(entitiesMap);

        if (entitiesMap.containsKey(coordinates)){
//            System.out.println("Запрос одобрен сущностью " + entitiesMap.get(coordinates));
            return entitiesMap.get(coordinates);
        }
//        System.out.println("Запрос отклонен сущностью " + entitiesMap.get(coordinates));
        return null;
    }

    public Asset[][] generateMapMatrix(int h, int w, int x1, int y1, int x2, int y2){
        Asset[][] result = new Asset[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++){
                result[i][j] = CellClassificator.createCell(CellClassificator.GRASS, null);
                result[i][j].setX(j);
                result[i][j].setY(i);
            }
        }

        for (int i = x1+1; i < x2; i++){
            int j = linear(i, x1,y1,x2,y2);
            Cell C = CellClassificator.createCell(CellClassificator.ROAD, null);
            result[j][i] = C;
            result[j][i].setX(i);
            result[j][i].setY(j);
        }

        for (int i = 0; i < h; i++){
            if (i == y1){
                continue;
            }
            Cell C = CellClassificator.createCell(CellClassificator.WALL, null);
            result[i][w/2] = C;
            result[i][w/2].setX(i);
            result[i][w/2].setY(w/2);
        }

        return result;
    }

    public void surroundPointWithFriendlyGrass(int x, int y, double radius, Player owner){
        for (int i = (int) (-radius); i < radius; i++){
            for (int j = (int) (-radius); j < radius; j++){
                Coordinates coords = new Coordinates();
                coords = coords.withComponent(1, x+j).withComponent(2, y+i);
                if (!Coordinates.isOutOfBounds(coords, this)) {
                    if (Locator.distance(mapMatrix[y + i][x + j], mapMatrix[y][x]) <= radius && mapMatrix[y+i][x+j] instanceof Cell && ((Cell) mapMatrix[y + i][x + j]).getCellType().equals("GRASS")) {
                        if (mapMatrix[y+i][x+j] instanceof Castle){
                            continue;
                        }
                        if (mapMatrix[y+i][x+j] instanceof Cell && ((Cell) mapMatrix[y + i][x + j]).getCellType().equals("ROAD")){
                            continue;
                        }
                        Cell newCell = CellClassificator.createCellByName("GRASS", owner);
                        newCell.setX(x+j);
                        newCell.setY(y+i);
                        mapMatrix[y + i][x + j] = newCell;


                        if (!(matrix[y+i][x+j] instanceof Entity)) {
                            matrix[y+i][x+j] = mapMatrix[y+i][x+j];
                        }
                    }
                }
            }
        }
    }

    int linear(int x, int x1, int y1, int x2, int y2){
        double k = (double) (y2 - y1) /(x2-x1);
        double b = y1 - k*x1;
        return (int) ceil(k*x + b);
    }

    public int getWidth(){
        return this.width;
    }
    public int getHeight(){
        return this.height;
    }


    public void render(){
        for (int i = 0; i < this.getHeight(); i++){
            for (int j = 0; j < this.getWidth(); j++){
                System.out.print(this.matrix[i][j].render() + " ");
            }
            System.out.println(' ');
        }
    }

    public void increlocate(Asset asset, int dx, int dy){
        relocate(asset, new Coordinates().withComponent(1, asset.getX()+dx).withComponent(2, asset.getY()+dy));
    }

    public void relocate(Asset asset, Coordinates coordinates){
        Coordinates oldCoords = asset.getCoordinates();
        if (asset instanceof Entity){
            entityRelocationHandler((Entity) asset, coordinates);
        }
        // Работаем с картой сущностей
        if (asset instanceof Entity entity) {
            if (entity.getCoordinates().isNormally2D() && !coordinates.isNormally2D()){
                removeEntity(entity);
            }
            entitiesMap.remove(entity.getCoordinates(), entity);
            //System.out.println("Кладём в enttitesMap " + coordinates + " " + entity + ", entitiesMap до: " + entitiesMap);
            entitiesMap.put(coordinates, entity);
            //System.out.println("entitiesMap после: " + entitiesMap);
            // Обновление координат в самом ассете
            entity.setCoordinates(coordinates);
        }

        // Работаем с основной матрицей, т.к. ассет перемещается в неё
        if (coordinates.isNormally2D()){
            int newX = coordinates.getX();
            int newY = coordinates.getY();
            matrix[newY][newX] =  asset;
            if (oldCoords.isNormally2D()){
                int x = oldCoords.getX();
                int y = oldCoords.getY();
                matrix[y][x] = mapMatrix[y][x];
            }
        }
    }


    private void entityRelocationHandler(Entity entity, Coordinates coordinates){
        warden.judge(entity, coordinates);
    }

    public void loadEntity(Asset asset, int x, int y){
        matrix[y][x] = asset;
        asset.setX(x);
        asset.setY(y);
        asset.setMap(this);
        if (asset instanceof Entity) {
            entitiesMap.put(asset.getCoordinates(), (Entity) asset);
        }
    }

    public SpaceWarden getWarden() {
        return warden;
    }

    public void loadEntity(Asset asset, Coordinates coordinates){
        if (coordinates.isNormally2D()){
            int x = coordinates.getX();
            int y = coordinates.getY();
            matrix[y][x] = asset;
            asset.setX(x);
            asset.setY(y);
        }
        asset.setMap(this);
        if (asset instanceof Entity) {
            entitiesMap.put(asset.getCoordinates(), (Entity) asset);
        }
    }

    public void removeEntity(Asset entity){
        matrix[entity.getY()][entity.getX()] = mapMatrix[entity.getY()][entity.getX()];
        if (entity instanceof Entity){
            entitiesMap.remove(entity.getCoordinates());
        }
    }

    public void spawn(Entity entity){
        int x = entity.getOwner().getCastle().getX();
        int y = entity.getOwner().getCastle().getY();
        int j = 1;
        for (int i = 1; i<=height/2; i++){
            if (matrix[y+j][x] instanceof Entity){
                j=-i;
                continue;
            }
            loadEntity(entity, x, y+j);
            return;
        }
        return;
    }

    public ArrayList<Entity> findEntities(Predicate<Coordinates> condition) {
        return (ArrayList<Entity>) entitiesMap.entrySet().stream()
                .filter(entry -> condition.test(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    // Сохранение карты в файл
    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            logger.log(Level.INFO, "Карта успешно сохранена в файл: {0}", filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка сохранения карты в файл: " + filename, e);
        }
    }

    // Загрузка карты из файла
    public static GameMap loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            GameMap loadedMap = (GameMap) ois.readObject();
            logger.log(Level.INFO, "Карта успешно загружена из файла: {0}", filename);
            return loadedMap;
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Ошибка загрузки карты из файла: " + filename, e);
            return null;
        }
    }

    // Восстановление transient-полей после десериализации
    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }
}
