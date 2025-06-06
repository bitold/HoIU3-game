package classification;

import player.Player;

public enum ShopBuildingClassificator {
    GUARDPOST("Сторожевой пост", 10, UnitClassificator.SPEARMAN),
    CROSSBOWTOWER("Башня арбалетчиков", 20, UnitClassificator.CROSSBOWMAN),
    ARMORY("Оружейная", 30, UnitClassificator.SWORDSMAN),
    ARENA("Арена", 40, UnitClassificator.CAVALRYMAN),
    CATHEDRAL("Собор", 50, UnitClassificator.PALADIN),
    TAWERN("Таверна", 10, null),
    HORSESTABLE("Конюшня", 10, null);

    private final String name;
    private final int cost;
    private final UnitClassificator unit;

    ShopBuildingClassificator(String name, int cost, UnitClassificator unit) {
        this.name = name;
        this.cost = cost;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public UnitClassificator getUnit() {
        return unit;
    }
}
