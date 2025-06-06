package menus;

import asset.Hero;
import castle.Castle;
import castle.CastleUI;
import classification.ShopBuildingClassificator;
import classification.UnitClassificator;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.Scanner;


public class BuyingMenu {
    Castle castle;
    CastleUI castleUI;
    private Scanner scanner = new Scanner(System.in);

    public BuyingMenu(Castle castle, CastleUI castleUI){
        this.castle=castle;
        this.castleUI=castleUI;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void printNotEnoughGold(){
        System.out.println("НЕ ХВАТАЕТ ЗОЛОТА.");
    }

    public void printNoRequiredBuilding(){
        System.out.println("НЕТ НЕОБХОДИМОГО СТРОЕНИЯ.");
    }

    public void printUnitBought(UnitClassificator unit){
        System.out.println("Приобретён юнит "+unit.getName()+" для героя " + castleUI.getHero() +
                ". Баланс игрока: " + castle.getOwner().getGold());
    }

    public void printBuildingBought(ShopBuildingClassificator building){
        System.out.println("Приобретено строение " + building.getName()+
                ". Баланс игрока: " + castle.getOwner().getGold());
    }

    void printItemList(){
        System.out.println("Доступные к покупке строения: ");
        int i = 0;
        for (ShopBuildingClassificator building : ShopBuildingClassificator.values()) {
            System.out.println(i+1 + ". " + building.getName() + ", Цена: " + building.getCost());
            i++;
        }
        i = 0;

        System.out.println("Доступные к покупке юниты: ");

        for (UnitClassificator unit : UnitClassificator.values()) {
            if (castleUI.checkUnitAvailability(unit)) {
                System.out.println(i + 1 + ". " + unit.getName() + ", Цена: " + unit.getCost());
            }
            i++;
        }
        i = 0;
    }

    public void engageShopMenu(){
        System.out.println("Вы в магазине замка. Выберите действие: \n" +
                "1. Купить строение      2. Купить юнита      3. Выйти из замка" + (castle.isBuilt("Таверна") ? "    4. Нанять героя (100 монет)" : ""));
        String input = scanner.nextLine();
        while (processShopMainMenuInput(input)){
            System.out.println("Вы в магазине замка. Выберите действие: \n" +
                    "1. Купить строение      2. Купить юнита      3. Выйти из замка" + (castle.isBuilt("Таверна") ? "    4. Нанять героя (100 монет)" : ""));
            input = scanner.nextLine();
        }
    }

    boolean processShopMainMenuInput(String input){
        int num;
        try {
            num = Integer.parseInt(input);
        } catch (Exception e){
            System.out.println("Ошибка. Попробуйте снова.");
            return true;
        }
        switch (num){
            case 1:
                System.out.println("Перезод к покупке строения");
                engageBuildingPurchase();
                return true;
            case 2:
                engageUnitPurchase();
                return true;
            case 3:
                return false;
            case 4:
                engageHeroPurchase();
                return true;
            default:
                System.out.println("Ошибка. Попробуйте снова.");
        }
        return true;
    }
    void engageHeroPurchase(){
        Hero hero = new Hero(0,0, "Δ",10,100,50,1, castle.getOwner());
        castleUI.buyHero(hero);

    }

    public void printHeroBought(){
        System.out.println("Нанят новый герой. ");
    }


    void engageBuildingPurchase(){
        System.out.println("Введите номер строения из списка:");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        printItemList();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Введите номер строения из списка.");
        String input = scanner.nextLine();
        ShopBuildingClassificator building;
        int num = integerInputHandler(input);
        if (num < 0){
            return;
        }
        try {
            building = castleUI.getBuildings().get(num-1);
        } catch (Exception e) {
            System.out.println("Введён неверный номер.");
            return;
        }
        System.out.println("Производится покупка строения...");
        castleUI.buyBuilding(building);
    }

    void engageUnitPurchase(){
        System.out.println("Введите номер юнита из списка:");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        printItemList();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Введите номер юнита из списка.");
        String input = scanner.nextLine();
        UnitClassificator unit;
        int num = integerInputHandler(input);
        if (num < 0){
            return;
        }
        try {
            unit = castleUI.getUnits().get(num-1);
        } catch (Exception e) {
            System.out.println("Введён неверный номер.");
            return;
        }

        System.out.println("Введите номер героя, который получит юнита " + unit + ":");
        printHeroesForUnitBuy();
        input = scanner.nextLine();
        num = integerInputHandler(input);
        Hero hero;
        if (num < 0){
            return;
        }
        try {
            hero = castle.getOwner().getHeroes().get(num-1);
        } catch (Exception e) {
            System.out.println("Введён неверный номер.");
            return;
        }
        castleUI.buyUnit(unit, hero);
    }

    void printHeroesForUnitBuy(){
        int i = 1;
        for (Hero hero: castle.getOwner().getHeroes()){
            System.out.println(i + ". " + hero);
            i++;
        }
    }

    int integerInputHandler(String input){
        int num = -1;
        try {
            num = Integer.parseInt(input);
        } catch (Exception e){
            System.out.println("Ошибка. Попробуйте снова.");
            return -1;
        }
        return num;
    }

}
