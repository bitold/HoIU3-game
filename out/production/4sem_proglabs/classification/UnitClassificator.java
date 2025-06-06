package classification;

import asset.Hero;
import asset.Unit;
import castle.Castle;

public enum UnitClassificator {
    SPEARMAN("Копейщик", "><", 500, 100, 150, 2, 10),
    CROSSBOWMAN("Арбалетчик", ")(",40, 80, 30, 5, 20),
    SWORDSMAN("Мечник", "!¡",600, 120, 250, 1, 30),
    CAVALRYMAN("Кавалерист", "?¿",70, 150, 35, 3, 40),
    PALADIN("Паладин", "⎲⎳",100, 200, 40, 2, 80);


    private final String name;
    private final int defaultMP;
    private final int maxHealth;
    private final int damage;
    private final int attackRange;
    private final String design;
    private final int cost;

    UnitClassificator(String name, String design, int defaultMP, int maxHealth, int damage, int attackRange, int cost) {
        this.name = name;
        this.defaultMP = defaultMP;
        this.maxHealth = maxHealth;
        this.damage = damage;
        this.attackRange = attackRange;
        this.design = design;
        this.cost = cost;
    }

    // Метод для создания юнита конкретного класса (передаётся класс)
    public static Unit createUnit(UnitClassificator unitClass, Hero owner){
        return new Unit(
                unitClass.getName(),
                0,
                0,
                unitClass.getDefaultMP(),
                unitClass.getMaxHealth(),
                unitClass.getDamage(),
                unitClass.getAttackRange(),
                resolveDesignAccordingToSide(unitClass, owner),
                owner
        );
    }


    static String resolveDesignAccordingToSide(UnitClassificator unitClass, Hero owner){
        if (owner.getOwner().getNickname().equals("Computer")){
            return unitClass.getDesign().charAt(1) + "";
        } else {
            return unitClass.getDesign().charAt(0) + "";
        }
    }
    public String getDesign() {
        return design;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getDamage() {
        return damage;
    }

    public int getDefaultMP() {
        return defaultMP;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public static int getCostByName(String className){
        for (UnitClassificator unit : UnitClassificator.values()) {
            System.out.println(unit.getName());
            if (unit.getName().equals(className)) {
                return unit.getCost();
            }
        }
        throw new IllegalArgumentException("Неизвестный класс юнита: " + className);
    }

    // Создание юнита по строковому названию класса
    public static Unit createUnitByClass(String className, Hero owner) {
        className = className.toUpperCase();
        for (UnitClassificator unit : UnitClassificator.values()) {
            if (unit.name().equals(className)) {
                return createUnit(unit, owner);
            }
        }
        throw new IllegalArgumentException("Неизвестный класс юнита: " + className);
    }


}
