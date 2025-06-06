package asset;

import misc.Coordinates;
import player.Player;

import java.util.Objects;

import static java.lang.Math.*;

public class Entity extends Asset{
    private Player owner; // иногда может быть null
    private int defaultMP;
    private double currentMP;
    private int maxHealth;
    private int currentHealth;
    private int damage;
    private int attackRange;
    private String type;
    private boolean ableToAttack = true;
    private boolean alive;
    private int spaceArrestHP;
    private boolean spaciallyArrested;
    private int defaultSpaceArrestHP;
    Entity(String type, String design, int x, int y, int dist, int hp, int dmg, int attackRange, Player owner){
        super("entity", design, x, y);
        this.type = type;
        this.defaultMP = dist;
        this.currentMP = dist;
        this.maxHealth = hp;
        this.currentHealth = hp;
        this.damage = dmg;
        this.attackRange = attackRange;
        this.alive = true;
        this.owner = owner;
        this.defaultSpaceArrestHP = 2;
        this.spaceArrestHP = 2;
    }
    Entity(){

    }


    public int getCurrentHealth() {
        return currentHealth;
    }

    public Player getOwner() {
        return owner;
    }

    public void setAbleToAttack(boolean ableToAttack) {
        this.ableToAttack = ableToAttack;
    }

    public boolean isAbleToAttack() {
        return ableToAttack;
    }

    public void checkHealth(){
        if (this.currentHealth <= 0){
            alive = false;
            setDesign("☓");
        } else {
            alive = true;
        }
    }

    public void takeMovementPointsForMovingTo(Cell cell){
        if (Objects.isNull(cell)){
            return;
        }
        double k = 1;
        if (this.getType().equals("hero")){
            boolean isHorsed = ((Hero)this).getOwner().getCastle().isBuilt("Конюшня");
            if (isHorsed){
                System.out.println("ЕСТЬ КОНЮШНЯ. ПРИМЕНЯЕМ МНОЖИТЕЛЬ.");
                k = 0.5;
            }
        }
        takeMP(cell.calculateCost(this.owner)*k);
    }

    public void takeMP(double MP){
        setCurrentMP(currentMP - MP);
    }

    @Override
    public void setDesign(String design) {
        super.setDesign(design);
    }

    public boolean isAlive() {
        return alive;
    }

    public double getCurrentMP() {
        return currentMP;
    }

    public int getDefaultMP() {
        return defaultMP;
    }

    public int getHealth() {
        return currentHealth;
    }

    public int getDamage() {
        return damage;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setHealth(int health) {
        this.currentHealth = health;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void dealDamage(int dmg){
        this.currentHealth = this.currentHealth - dmg;
    }


    public void setCurrentMP(double currentMP) {
        this.currentMP = currentMP;
    }

    public boolean isExhausted(){
        return (currentMP <= 0);
    }


    public boolean isSpaciallyArrested() {
        return spaciallyArrested;
    }

    public void setSpaciallyArrested(boolean spaciallyArrested) {
        this.spaciallyArrested = spaciallyArrested;
    }


    /**
     * Проверяет, легален ли ход сущности для Стража Пространства.
     * @param newcoords
     * @return
     */
    public boolean isLegalToMove(Coordinates newcoords){
        Coordinates oldcoords = this.coordinates;
        Coordinates dQ = newcoords.subtract(oldcoords);

        //System.out.println("сущность " + this.getDesign() + " хочет сместится на " + dQ + ", это " + (dQ.isNormally2D() ? " легально" : " нелегально"));
        return dQ.isNormally2D();
    }

    public void dealArrestDamage(int dmg){
        spaceArrestHP-=dmg;
        if (spaceArrestHP<=0){
            setSpaciallyArrested(true);
        }
    }

    public void letFree(){
        setSpaciallyArrested(false);
        restoreSpaceArrestHealth();
    }

    public void setSpaceArrestHealth(int spaceArrestHealth) {
        this.spaceArrestHP = spaceArrestHealth;
    }

    public void restoreSpaceArrestHealth(){
        spaceArrestHP = defaultSpaceArrestHP;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "owner=" + owner +
                ", defaultMP=" + defaultMP +
                ", currentMP=" + currentMP +
                ", maxHealth=" + maxHealth +
                ", currentHealth=" + currentHealth +
                ", damage=" + damage +
                ", attackRange=" + attackRange +
                ", type='" + type + '\'' +
                ", ableToAttack=" + ableToAttack +
                ", alive=" + alive +
                ", spaceArrestHP=" + spaceArrestHP +
                ", spaciallyArrested=" + spaciallyArrested +
                ", defaultSpaceArrestHP=" + defaultSpaceArrestHP +
                '}';
    }


    public int getSpaceArrestHP() {
        return spaceArrestHP;
    }


    public int getViolations(){
        return defaultSpaceArrestHP-spaceArrestHP;
    }
}
