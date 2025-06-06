package misc;

import map.GameMap;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class Coordinates {
    private final Map<Integer, Integer> coordinates;

    public Coordinates() {
        this.coordinates = new HashMap<>();
    }

    public Coordinates(Map<Integer, Integer> coords) {
        this.coordinates = new HashMap<>(coords);
    }


    private void put(int dimension, int value) {
        if (value == 0 && dimension!=1 && dimension!=2){
            coordinates.remove(dimension);
            return;
        }
        coordinates.put(dimension, value);
    }

    // Метод для получения компоненты (0, если отсутствует)
    public int get(int dimension) {
        return coordinates.getOrDefault(dimension, 0);
    }


    public Coordinates add(Coordinates other) {
        Coordinates result = new Coordinates();
        Set<Integer> allDimensions = new HashSet<>(this.coordinates.keySet());
        allDimensions.addAll(other.coordinates.keySet());

        for (int dim : allDimensions) {
            int sum = this.get(dim) + other.get(dim);
            result.put(dim, sum);
        }
        return result;
    }

    public static Coordinates negative(Coordinates coords){
        Map<Integer, Integer> inverted = coords.getCoordinates().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> -entry.getValue()
                ));
        return new Coordinates(inverted);
    }

    public Coordinates subtract(Coordinates other){
        return this.add(negative(other));
    }

    /**
     * Проективное отображение.
     * @param input
     * @param axis
     * @return
     */
    public static Coordinates project(Coordinates input, int axis){
        Coordinates result = new Coordinates();
        result = result.withComponent(axis, input.get(axis));
        return result;
    }

    public static double norm(Coordinates input){
        Coordinates c = input.clearOfZeros(true);
        double result = 0;
        for (int i: c.getCoordinates().keySet()){
            result+=pow(c.get(i),2);
        }
        return pow(result, 0.5);
    }

    public Coordinates wholeDivide(int div){
        Coordinates result = this;
        for (int i: this.getAxi()){
            result = result.withComponent(i, this.get(i)/div);
        }
        return result;
    }

    public static Coordinates integerNormalization(Coordinates input){
        return input.wholeDivide((int) round(norm(input)));
    }
    /**
     * Очищает HashMap координат от нулевых составляющих.
     * @param strict определяет, очищать ли классические координаты x, y если они нулевые.
     * @return
     */
    public Coordinates clearOfZeros(boolean strict){
        Map<Integer, Integer> clean = this.getCoordinates().entrySet().stream()
                .filter(entry -> ((!strict) && (entry.getKey() == 1 || entry.getKey() == 2) || entry.getValue()!=0))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        return new Coordinates(clean);
    }


    public Set<Integer> getAxi(){
        return this.clearOfZeros(true).getCoordinates().keySet();
    }

    /**
     * Возвращает вектор со значением value у компоненты dimension.
     * @param dimension
     * @param value
     * @return
     */
    public Coordinates withComponent(int dimension, int value) {
        Map<Integer, Integer> newCoords = new HashMap<>(this.coordinates);
        newCoords.put(dimension, value);
        return new Coordinates(newCoords);
    }

    public static Coordinates createUnitVector(int dimension, boolean positive) {
        Coordinates vector = new Coordinates();
        vector.put(dimension, positive ? 1 : -1);
        return vector;
    }

    public static Coordinates createPositiveUnit(int dimension) {
        return createUnitVector(dimension, true);
    }

    public static Coordinates createNegativeUnit(int dimension) {
        return createUnitVector(dimension, false);
    }

    public static Coordinates boundaryLoop(Coordinates oldcoords, Coordinates newcoords, GameMap map) throws IllegalArgumentException {
        if (!isOutOfBounds(newcoords, map)){
            throw new IllegalArgumentException("Invalid boundaryLoop call. Coordinates are in bounds.");
        }
        int changedDim = abs(changedDimensionSignedNumber(oldcoords, newcoords));
        Coordinates result = new Coordinates(
                newcoords.clearOfZeros(false).getCoordinates().entrySet().stream().collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> (compare(entry.getKey(), changedDim)) ? getOpposite(entry.getValue(), changedDim, map) : entry.getValue()
                        )
                ));
        return result;
    }
    static boolean compare(int a, int b){
        //System.out.println("Сравниваю ключ " + a + " с измененным измерением " + b);
        return a==b;
    }

    static int getOpposite(int value, int dimension, GameMap map){
        System.out.println("Запрос противоположной координаты для координаты в измерении " + dimension + " со значением " + value);
        int h = map.getHeight();
        int w = map.getWidth();
        int result = switch (abs(dimension)) {
            case 1 -> (value+w) % w;
            case 2 -> (value+h) % h;
            default -> -value+(int)signum(value);
        };
        System.out.println("Противополжная координата - " + result);
        return result;
    }
    static int changedDimensionSignedNumber(Coordinates oldcoords, Coordinates newcoords){
        Coordinates difference = newcoords.add(negative(oldcoords)).clearOfZeros(true);
        if (difference.getCoordinates().keySet().size()!=1){
            return 0;
        }
        return difference.getCoordinates().keySet().stream().collect(Collectors.toList()).get(0);
    }

    public static boolean isOutOfBounds(Coordinates coordinates, GameMap map){
        int size = max(map.getHeight(), map.getWidth());
        if (coordinates.isNormally2D()){
            return basic2DOutOfBounds(coordinates, map);
        }
        for (int dim: coordinates.getCoordinates().keySet()){
            if (elementaryOutOfBoundsPredicate(dim, coordinates.getCoordinates().get(dim), size)){
                return true;
            }
        }

        return false;
    }
    static boolean elementaryOutOfBoundsPredicate(int dim, int coordinate, int size){
        if (dim == 1 || dim == 2){
            return (coordinate >= size || coordinate < 0);
        }
        return (abs(coordinate) > size/2);
    }

    static boolean basic2DOutOfBounds(Coordinates coordinates, GameMap map){
        return  (coordinates.getX() >= map.getWidth() || coordinates.getY() >= map.getHeight() || coordinates.getX() < 0 || coordinates.getY() < 0);
    }

    public int getX() {
        return get(1);
    }

    public int getY() {
        return get(2);
    }

    public Coordinates withSetX(int x){
        return this.withComponent(1, x);
    }
    public Coordinates withSetY(int y){
        return this.withComponent(2, y);
    }

    public Map<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "[" + coordinates + "]";
    }

    public boolean isNormally2D(){
        for (int dimension: coordinates.keySet()){
            if (dimension != 1 && dimension != 2 && coordinates.get(dimension) != 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinates other = ((Coordinates) obj).clearOfZeros(true);

        return Objects.equals(this.clearOfZeros(true).coordinates, other.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(coordinates);
    }
}