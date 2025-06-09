package misc;

import asset.Entity;
import player.Player;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import java.util.*;
import java.util.logging.*;
public class SpaceWarden implements Serializable {
    private HashMap<Entity, Integer> convicts = new HashMap<>();
    private int damageForViolation = 1;

    private static final Logger logger = Logger.getLogger(SpaceWarden.class.getName());
    static {
        try {
            FileHandler fileHandler = new FileHandler("spacewarden.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize file handler for logger", e);
        }
    }


    public SpaceWarden(){

    }

    public void judge(Entity entity, Coordinates oldcoords, Coordinates newcoords){
        boolean punish = !isLegalMove(oldcoords, newcoords);
        if (punish){
            entity.dealDamage(damageForViolation);
            entity.dealArrestDamage(1);
            logger.info("Entity " + entity + " violated movement rules at coordinates " + newcoords +
                    ". Damage applied: " + damageForViolation);
            if (entity.isSpaciallyArrested() && !convicts.containsKey(entity)){
                convicts.put(entity, 0);
                logger.warning("Сущность " + entity + " арестована");
            } else if (entity.isSpaciallyArrested()) {
                moveToPreviousPlace(entity, oldcoords, newcoords);
            }
        }
    }
    void moveToPreviousPlace(Entity entity, Coordinates oldcoords, Coordinates newcoords){
        entity.getMap().pureRelocate(entity, oldcoords);
    }

    public void jailIteration(Player owner){
        convictIteration(owner);
        for (Map.Entry<Entity, Integer> entry : convicts.entrySet()) {
            if (entry.getValue() > 2) {
                entry.getKey().letFree();
                convicts.remove(entry.getKey());
                logger.info("Арест сущности " + entry.getKey() + " окончен");
            }
        }
    }

    public boolean isLegalMove(Coordinates oldcoords, Coordinates newcoords){
        return newcoords.subtract(oldcoords).isNormally2D();
    }
    public void convictIteration(Player owner){
        for (Map.Entry<Entity, Integer> entry : convicts.entrySet()) {
            if (entry.getKey().getOwner().equals(owner)){
                entry.setValue(entry.getValue() + 1);
            }
        }
    }
}