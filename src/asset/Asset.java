package asset;

import map.GameMap;
import misc.Coordinates;

import java.io.Serializable;

public class Asset implements Serializable {
    String type;
    private String design;
    private int x;
    private int y;
    private GameMap map;
    Coordinates coordinates;

    public Asset(String type, String design, int x, int y){
        this.design = design;
        this.type = type;
        this.x=x;
        this.y=y;
        this.coordinates = new Coordinates();
        this.coordinates = coordinates.withComponent(1, x);
        this.coordinates = coordinates.withComponent(2, y);
    }

    public Asset(String type, String design, Coordinates coordinates){
        this.design = design;
        this.type = type;
        this.x=coordinates.getX();
        this.y=coordinates.getY();
        this.coordinates = coordinates;
    }

    public Asset(){

    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public GameMap getMap() {
        return map;
    }

    @Override
    public String toString() {
        return (" design: ".concat(design));
    }
    public String render() {
        return this.design;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        this.x = coordinates.getX();
        this.y = coordinates.getY();
    }

    public int getX() {
        return coordinates.getX();
    }

    public int getY() {
        return coordinates.getY();
    }

    public void setX(int x) {
        this.coordinates = coordinates.withSetX(x);
        this.x = x;
    }

    public void setY(int y) {
        this.coordinates = coordinates.withSetY(y);
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public void increment(int dx, int dy){
        x+=dx;
        y+=dy;
        setX(x);
        setY(y);
    }
}
