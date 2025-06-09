package player;

import asset.Asset;
import asset.Hero;
import castle.Castle;
import game.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Player implements Serializable {
    private ArrayList<Hero> heroes = new ArrayList<>();
    private int gold;
    private Castle castle;
    private int heroAmount;
    private String nickname;
    private Hero battler;
    private Set<Hero> castleCaptors = new HashSet<>();
    private Game game;
    String type;

    Player(){

    }
    public Player(ArrayList<Hero> heroes, int gold, String nickname, Game game){
        this.heroes = heroes;
        this.gold = gold;
        this.heroAmount = heroes.size();
        this.nickname = nickname;
        this.game = game;
        this.type = "PLAYER";
    }

    public void addCaptor(Hero hero){
        castleCaptors.add(hero);
    }


    public void castleCaptureIteration(){
        for (Hero captor: castleCaptors){
            captor.damageCastle();
            if (captor.getCurrentCastle().isCaptured()){
                game.addLoser(captor.getCurrentCastle().getOwner());
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
        castle.setOwner(this);
    }

    public void takeGold(int amount){
        if (this.gold > amount){
            gold-=amount;
        }
    }

    public Castle getCastle() {
        return castle;
    }

    public int getGold() {
        return gold;
    }

    public ArrayList<Hero> getHeroes() {
        return heroes;
    }


    public void setBattler(Hero battler) {
        this.battler = battler;
    }

    public Hero getBattler() {
        return battler;
    }

    void checkExpelHeroes(){
        if (!heroes.isEmpty()){
            heroes.removeIf(hero -> hero.getArmySize() == 0);
            if (!Objects.isNull(castleCaptors)){
                castleCaptors.removeIf(hero -> !heroes.contains(hero));
            }
            heroAmount = heroes.size();
        } else {
            heroAmount=0;
        }
    }

    public boolean haveLost(){
        checkExpelHeroes();
        if (castle.isCaptured()){
            System.out.println("Замок игрока " + this + " захвачен.");
        }
        return (heroAmount <= 0 || castle.isCaptured());
    }

    public void printStats(){
        System.out.println("Статистика игрока:");
        System.out.println("Золота: " + gold);
        System.out.println("    Список героев:");
        for (int i = 0; i<heroes.size(); i++) {
            Hero hero = heroes.get(i);
            System.out.println(("   " + i+1) + ". Герой: " + hero.getDesign() + "    Армия:");
            (hero).printArmy();
         }
    }

    public void expelHero(Hero hero){
        heroes.remove(hero);
        hero.expelFromCastle();
        castleCaptors.remove(hero);
        heroAmount--;
    }

    public void giveGold(int gold){
        this.gold = this.gold + gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setHeroes(ArrayList<Hero> heroes) {
        this.heroes = heroes;
        for (Hero hero: heroes){
            hero.setOwner(this);
        }
    }

    public void addHero(Hero hero){
        this.heroes.add(hero);
        this.heroAmount++;
    }

    public Game getGame() {
        return game;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.getNickname();
    }
}
