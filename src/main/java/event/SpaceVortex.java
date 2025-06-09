package event;

import asset.Entity;
import misc.Coordinates;

import java.util.Random;

public class SpaceVortex extends Event{

    int maxDimensionNumberDelta;
    double probability;
    Random random;

    public SpaceVortex(){
        random = new Random();
        maxDimensionNumberDelta = 10;
        probability = 0.8;
        duration = 2;
        movesLeft = duration;
        active = false;
    }

    public void tryToRandomlyStart(){
        double randomResult = Math.abs(random.nextDouble() % 1);
        if (randomResult < probability){
            activate();
        }
    }

    public void swirl(Entity entity, Coordinates oldcoords, Coordinates coordinates){
        Coordinates dQ = coordinates.subtract(oldcoords);

        int changedAxis = 0;
        try {
            changedAxis = dQ.getAxi().stream().toList().getFirst();
        } catch (Exception e) {
            return;
        }

        int newChangedAxis = Math.abs(random.nextInt(changedAxis, maxDimensionNumberDelta));
        Coordinates newCoordinates = oldcoords.add(new Coordinates().withComponent(newChangedAxis, 1));
        entity.getMap().pureRelocate(entity, newCoordinates);
    }
}
