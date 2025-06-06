package asset;

import player.Player;

public class Cell extends Asset{
    int num;
    private final String design;
    private final int defaultCost;
    private final boolean wall;
    private final double enemyCoefficient;
    private final Player territoryOwner;

    public Cell(String type, String design, int x, int y, int defaultCost, boolean wall, double ememyCoefficient, Player territoryOwner){
        super(  type,
                design,
                x,
                y);
        this.num=num;
        this.defaultCost=defaultCost;
        this.design=design;
        this.wall = wall;
        this.enemyCoefficient = ememyCoefficient;
        this.territoryOwner = territoryOwner;
    }

    public boolean isWall() {
        return wall;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public int calculateCost(Player player) {
        int baseCost = this.getDefaultCost();
        Player owner = this.getTerritoryOwner();
        if (owner != null && !owner.equals(player)) {
            return (int) (baseCost * this.getEnemyCoefficient());
        }
        return baseCost;
    }

    public int getNum() {
        return num;
    }

    public int getDefaultCost() {
        return defaultCost;
    }

    public Player getTerritoryOwner() {
        return territoryOwner;
    }

    public double getEnemyCoefficient() {
        return enemyCoefficient;
    }

    @Override
    public String getDesign() {
        return design;
    }

    @Override
    public String getType() {
        return "cell";
    }

    public String getCellType(){
        return type;
    }
}
