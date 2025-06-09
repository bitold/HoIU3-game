package castle;

import asset.Hero;
import classification.ShopBuildingClassificator;
import classification.UnitClassificator;
import menus.BuyingMenu;
import player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;


public class CastleUI implements Serializable {
    private Castle castle;
    private Hero hero;
    private BuyingMenu buyingMenu;

    // Списки, наполненные товарами и использующиеся при выводе меню.
    private ArrayList<ShopBuildingClassificator> buildings = new ArrayList<>();
    private ArrayList<UnitClassificator> units = new ArrayList<>();

    public CastleUI(Castle castle){
        this.castle=castle;
        initializeShop();
        buyingMenu = new BuyingMenu(castle, this);
    }

    public ArrayList<ShopBuildingClassificator> getBuildings() {
        return buildings;
    }

    public ArrayList<UnitClassificator> getUnits() {
        return units;
    }

    public boolean checkUnitAvailability(UnitClassificator unit){
        for (ShopBuildingClassificator building: castle.getBuildings()){
            if (!Objects.isNull(building.getUnit()) && building.getUnit().equals(unit)){
                return true;
            }
        }
        return false;
    }

    public void startShop(){
        buyingMenu.engageShopMenu();
        leaveCastle();
    }

    void initializeShop(){
        int i = 0;
        for (ShopBuildingClassificator building : ShopBuildingClassificator.values()) {
            buildings.add(i, building);
            i++;
        }
        i = 0;

        for (UnitClassificator unit : UnitClassificator.values()) {
            units.add(i, unit);
            i++;
        }
        i = 0;

    }

    public void enterCastle(Hero hero){
        this.hero = hero;
    }

    public void leaveCastle(){
        this.hero = null;
    }

    public Hero getHero() {
        return hero;
    }

    public void buyBuilding(ShopBuildingClassificator building){

        if (castle.getOwner().getGold() > building.getCost()){
            castle.build(building);
            castle.getOwner().takeGold(building.getCost());
            buyingMenu.printBuildingBought(building);
        }
        else {
            buyingMenu.printNotEnoughGold();
        }
    }
    public void buyUnit(UnitClassificator unit, Hero hero){

        if (castle.getOwner().getGold() > unit.getCost() && checkUnitAvailability(unit)){
            hero.addUnit(UnitClassificator.createUnit(unit, hero));
            castle.getOwner().takeGold(unit.getCost());
            buyingMenu.printUnitBought(unit);
        }
        else if (castle.getOwner().getGold() < unit.getCost()) {
            buyingMenu.printNotEnoughGold();
        }
        else if (!checkUnitAvailability(unit)){
            buyingMenu.printNoRequiredBuilding();
        }
    }

    public void buyHero(Hero hero) {

        if (castle.getOwner().getGold() < 100){
            buyingMenu.printNotEnoughGold();
            return;
        }

        castle.getOwner().addHero(hero);
        castle.getOwner().takeGold(100);
        castle.getMap().spawn(hero);
        buyingMenu.printHeroBought();
    }

    public void setBuyingMenu(BuyingMenu buyingMenu) {
        this.buyingMenu = buyingMenu;
    }

    public BuyingMenu getBuyingMenu(){
        return buyingMenu;
    }
}