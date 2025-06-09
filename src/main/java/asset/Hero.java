package asset;

import castle.Castle;
import castle.CastleUI;
import player.Player;

import java.util.ArrayList;
import java.util.Objects;

public class Hero extends Entity{
    String heroType;
    private ArrayList<Unit> army = new ArrayList<>();
    private int armySize;
    private Castle currentCastle;

    public Hero(int x, int y, String design, int energy, int hp, int dmg, int attackRange, Player owner){
        super("hero", design, x, y,energy,hp,dmg,attackRange, owner);
        armySize = army.size();
    }

    public Hero(){

    }

    public void setCurrentCastle(Castle currentCastle) {
        this.currentCastle = currentCastle;
    }

    public void takeAVisit(Castle castle){
        setCurrentCastle(castle);
        castle.setVisitor(this);
    }

    public Castle getCurrentCastle() {
        return currentCastle;
    }
    public boolean isBusyCapturing(){
        return (!Objects.isNull(currentCastle) && !currentCastle.getOwner().equals(getOwner()));
    }

    public void replenish(){
        for (int i = 0; i < armySize; i++){
            army.get(i).refresh();
        }
    }

    public void damageCastle(){
        if (isBusyCapturing()){
            this.currentCastle.reduceDurability();
        }
    }

    public void expelFromCastle(){
        if (currentCastle == null){
            return;
        }
        currentCastle.setVisitor(null);
        currentCastle = null;

    }


    @Override
    public String getType(){
        return "hero";
    }

    public void removeUnit(Unit unit){
        army.remove(unit);
        armySize-=1;
    }

    public boolean isAlive(){
        armySize = army.size();
        return (armySize > 0);
    }

    public void expel(){
        this.getOwner().expelHero(this);
    }

    public int getArmySize() {
        armySize = army.size();
        return armySize;
    }

    public void addUnit(Unit unit){
        army.add(unit);
        armySize++;
    }

    public ArrayList<Unit> getArmy() {
        return army;
    }

    public void setHeroType(String heroType) {
        this.heroType = heroType;
    }

    public String getHeroType() {
        return heroType;
    }

    public void printArmy(){
        for (int i = 0; i< army.size(); i++){
            System.out.print(army.get(i).toString());
        }
        System.out.println("_____________");
    }


    @Override
    public String toString() {
        return "Герой [" + this.getDesign() + "] " + this.getCurrentMP() + "MP" + "   Координаты: " + this.getCoordinates() + (isBusyCapturing() ? ", захватывает замок игрока " + this.getCurrentCastle().getOwner().getNickname() : "" + " " + (this.isSpaciallyArrested() ? "АРЕСТОВАН" : "НАРУШЕНИЙ: " + this.getViolations()));
    }

    public String toStringShortened(){
        return "Герой [" + this.getDesign() + "] " + this.getCurrentMP() + "MP";
    }


}
