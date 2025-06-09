package asset;

public class Unit extends Entity{
    private Hero hero;
    private String class_;

    public Unit(String class_, int x, int y, int dist, int hp, int dmg, int attackRange, String design, Hero hero){
        super("unit", design, x,y,dist,hp,dmg,attackRange, hero.getOwner());
        this.hero = hero;
        this.class_ = class_;
    }

    public Unit(){}


    @Override
    public String getType(){
        return "unit";
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    void refresh(){
        this.setCurrentMP(getDefaultMP());
        setAbleToAttack(true);
    }

    public void checkLiving(){
        this.checkHealth();
        if (!this.isAlive()){
            hero.removeUnit(this);
            this.getMap().removeEntity(this);
        }
    }

    @Override
    public String toString() {
        return class_ + " [" + this.getDesign() + "] " + this.getCurrentHealth() + "HP " + this.getCurrentMP() + "MP" + "   Координаты: " + this.getCoordinates();
/*        return "asset.Unit{" +
                "type='" + this.class_  + '\'' +
                this.getDesign() + '\'' +
                "dist='" + this.getCurrentMP() + '\'' +
                "hp='" + this.getHealth() + '\'' +
                "dmg='" + this.getDamage() + '\'' +
                '}';*/
    }

    public Hero getHero() {
        return hero;
    }
}
