import map.GameMap;
import org.junit.jupiter.api.Assertions;
import misc.Coordinates;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Random;


class CoordinatesTest {

    private static GameMap gameMap;
    @BeforeAll
    static void setup(){
        Random gen = new Random();
        int w = Math.abs(gen.nextInt()) % 1000;
        int h = Math.abs(gen.nextInt()) % 1000;
        gameMap = new GameMap(h, w);
    }

    @Test
    void vectorAdditionTest(){
        Coordinates vector1 = new Coordinates();
        Coordinates vector2 = new Coordinates();
        vector1 = vector1.withComponent(1, 20).withComponent(2, 11).withComponent(100, -95);
        vector2 = vector2.withComponent(1, 34).withComponent(2, 48).withComponent(100, 32).withComponent(42, 42);
        Coordinates sum = vector1.add(vector2);
        Coordinates check = new Coordinates().withComponent(1, 54).withComponent(2, 59).withComponent(100, -63).withComponent(42, 42);
        Assertions.assertEquals(check, sum);
    }

    @Test
    void vectorSubtractionTest(){
        Coordinates vector1 = new Coordinates();
        Coordinates vector2 = new Coordinates();
        vector1 = vector1.withComponent(1, 20).withComponent(2, 11).withComponent(100, -95).withComponent(1234, 1234);
        vector2 = vector2.withComponent(1, 34).withComponent(2, 48).withComponent(100, 32).withComponent(42, 42).withComponent(1234, 1234);
        Coordinates diff = vector1.subtract(vector2);
        Coordinates check = new Coordinates().withComponent(1, -14).withComponent(2, -37).withComponent(100, -127).withComponent(42, -42);
        Assertions.assertEquals(check, diff);
    }

    @Test
    void vectorNormCalculationTest(){
        Coordinates vector = new Coordinates();
        vector = vector.withComponent(1, 4).withComponent(20, 3);
        Assertions.assertEquals(5, (int)(Coordinates.norm(vector)));
        vector = new Coordinates();
        vector = vector.withComponent(10, 5100).withComponent(200, 101);
        Assertions.assertEquals(5101, (int)(Coordinates.norm(vector)));
        vector = new Coordinates();
        vector = vector.withComponent(2385679, 2).withComponent(27640, 1).withComponent(284892, 2);
        Assertions.assertEquals(3, (int)(Coordinates.norm(vector)));
    }


    @Test
    void withComponentHashCodePreservationTest(){
        Coordinates vector = new Coordinates();
        Coordinates vector1 = new Coordinates();
        HashMap<Integer, Integer> coords = new HashMap<>();
        coords.put(1, 1);
        coords.put(42, 58);
        Coordinates vector2 = new Coordinates(coords);
        vector = vector.withComponent(1, 1).withComponent(42, 58);
        int firstCode = vector.hashCode();
        vector1 = vector1.withComponent(1, 1).withComponent(42, 58);
        int secondCode = vector1.hashCode();
        int thirdCode = vector2.hashCode();
        Assertions.assertEquals(firstCode, secondCode);
        Assertions.assertEquals(firstCode, thirdCode);
    }

    @Test
    void Vectors2DCheckingTest(){
        Coordinates vector = new Coordinates();
        vector = vector.withComponent(20, 20).withComponent(1, 2);
        Assertions.assertFalse(vector.isNormally2D());
        vector = vector.withComponent(20, 0);
        Assertions.assertTrue(vector.isNormally2D());
        vector = vector.withComponent(1, 0);
        Assertions.assertTrue(vector.isNormally2D());
    }

    @Test
    void OutOfBoundsCheckingForMovementVector(){
        Random gen = new Random();

        int w = gameMap.getWidth();
        int h = gameMap.getHeight();

        int x = Math.abs(gen.nextInt()) % w;
        int y = Math.abs(gen.nextInt()) % h;

        Coordinates coordinates = new Coordinates().withSetX(x).withSetY(y);
        Assertions.assertFalse(Coordinates.isOutOfBounds(coordinates, gameMap));

        x = x + w;
        y = y + h;
        coordinates = coordinates.withSetX(x).withSetY(y);
        Assertions.assertTrue(Coordinates.isOutOfBounds(coordinates, gameMap));
    }
}
