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
    BuyingMenu buyingMenu;

    // Списки, наполненные товарами и использующиеся при выводе меню.
    private ArrayList<ShopBuildingClassificator> buildings = new ArrayList<>();
    private ArrayList<UnitClassificator> units = new ArrayList<>();

    CastleUI(Castle castle){
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

    // Класс, представляющиЙ товары в магазине.
   /* public static class Item {
        private int id;
        private String name;
        private int price;
        private String type; // "building" или "unit"
        public Item(int id, String name, String type, int price){
            this.id = id;
            this.name = name;
            this.price = price;
            this.type = type;
            fillUnitBuildingMap();
        }
        public Item(int id, String name, String type) {
            this.id = id;
            this.name = name;
            this.price = UnitClassificator.getCostByName(name);
            this.type = type;
            fillUnitBuildingMap();
        }
        String[][] unitBuildingMatrix = {{"Копейщик", "Арбалетчик", "Мечник", "Кавалерист", "Паладин"},
                {"Сторожевой пост", "Башня арбалетчиков", "Оружейная", "Арена", "Собор"}};
        HashMap<String, String> unitToBuildingMap = new HashMap<>();
        void fillUnitBuildingMap(){
            for (int i = 0; i < unitBuildingMatrix[0].length; i++) {
                String unit = unitBuildingMatrix[0][i];
                String building = unitBuildingMatrix[1][i];
                unitToBuildingMap.put(unit, building);
            }
        }
        public int getPrice() {
            return price;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        boolean available(Castle castle){
            if (type == "building"){
                return true;
            } else {
                String requiredBuilding = unitToBuildingMap.get(this.name);
                System.out.println("Для юнита \"" + this.name + "\" требуется строение: " + requiredBuilding);
                return castle.isBuilding(requiredBuilding);
            }
        }
    }*/


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
        else if (checkUnitAvailability(unit)){
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
/*    public boolean processPurchase(Player player, Scanner scanner) {
        System.out.println("Что вы хотите купить? (B - Строение; U - юнит; C - выйти из магазина)");
        String type = scanner.nextLine();
        System.out.println(type + " Проверяю выход из магазина...");
        if (Objects.equals(type, "C")){
            System.out.println("Выхожу из магазина");
            return false;
        }
        System.out.println("Введите номер из списка:");
        int num;
        int heroNum;
        try {
            num = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Ошибка. Попробуйте снова.");
            return true;
        }
        if (type.equalsIgnoreCase("B")) {
            buyBuilding(num);
        } else if (type.equalsIgnoreCase("U")) {
            System.out.println("Введите номер героя, который получит юнита.");
            try {
                heroNum = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Ошибка. Попробуйте снова.");
                return true;
            }
            buyUnit(num, heroNum);
        } else {
            System.out.println("Неверный тип покупки.");
        }
        return true;
    }*/
}