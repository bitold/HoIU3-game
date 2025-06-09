package castle;

import asset.Asset;
import asset.Hero;
import classification.ShopBuildingClassificator;
import map.GameMap;
import player.Player;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Castle extends Asset {
    private Player owner;
    private ArrayList<ShopBuildingClassificator> buildings = new ArrayList<>();
    private CastleUI castleUI;
    private int defaultDurability;
    private int currentDurability;
    private Hero visitor;

    public Castle(boolean side, String design, int x, int y, Player owner, GameMap map){
        super("castle", design, x, y);
        this.owner = owner;
        owner.setCastle(this);
        this.castleUI = new CastleUI(this);
        this.defaultDurability = 2;
        this.currentDurability = this.defaultDurability;
        this.setMap(map);
    }

    public void setVisitor(Hero visitor) {
        this.visitor = visitor;
    }

    public Hero getVisitor() {
        return visitor;
    }
    public boolean isBeingCaptured(){
        return (!Objects.isNull(visitor) && visitor.isBusyCapturing());
    }
    public ArrayList<ShopBuildingClassificator> getBuildings() {
        return buildings;
    }
    public void reduceDurability(){
        this.currentDurability = this.currentDurability-1;
    }
    public void resetDurability(){
        this.currentDurability=this.defaultDurability;
    }
    public void replenishIfNoCaptor(){
        if (Objects.isNull(visitor) || visitor.getOwner().equals(owner)){
            resetDurability();
        }
    }
    public boolean isCaptured(){
        return (currentDurability <= 0);
    }

    public boolean isBuilt(String buildingName){
        for (ShopBuildingClassificator building: buildings){
            if (building.getName().equals(buildingName)){
                return true;
            }
        }
        return false;
    }

    public CastleUI getCastleUI() {
        return castleUI;
    }

    boolean isBuilding(ShopBuildingClassificator building){
        return buildings.contains(building);
    }

    @Override
    public String getType(){
        return "castle";
    }

    public void build(ShopBuildingClassificator building){
        buildings.add(building);
    }

    void printWrongBuildingName(String name){
        System.out.println("WRONG_BUILDING_NAME: " + name);
    }

    public Player getOwner() {
        return owner;
    }

    public int getCurrentDurability() {
        return currentDurability;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

}
