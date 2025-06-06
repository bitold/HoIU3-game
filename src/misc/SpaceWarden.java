package misc;

import asset.Entity;
import player.Player;

import java.io.Serializable;
import java.util.*;

public class SpaceWarden implements Serializable {
    private HashMap<Entity, Integer> convicts = new HashMap<>();
    private int damageForViolation = 1;

    public SpaceWarden(){

    }

    public void judge(Entity entity, Coordinates newcoords){
        boolean punish = !entity.isLegalToMove(newcoords);
        if (punish){
            entity.dealDamage(damageForViolation);
            entity.dealArrestDamage(1);
            if (entity.isSpaciallyArrested() && !convicts.containsKey(entity)){
                convicts.put(entity, 0);
                System.out.println("СУЩНОСТЬ " + entity + " АРЕСТОВАНА ! ! ! ! !");

            }
        }
    }
    public void jailIteration(Player owner){
        convictIteration(owner);
        for (Map.Entry<Entity, Integer> entry : convicts.entrySet()) {
            if (entry.getValue() > 2) {
                entry.getKey().letFree();
                convicts.remove(entry.getKey());
                System.out.println("Сущность " + entry.getKey() + " ОТСИДЕЛА");
            }
        }
    }

    public void convictIteration(Player owner){
        for (Map.Entry<Entity, Integer> entry : convicts.entrySet()) {
            if (entry.getKey().getOwner().equals(owner)){
                entry.setValue(entry.getValue() + 1);
            }
        }
    }
}
