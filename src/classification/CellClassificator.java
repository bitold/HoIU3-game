package classification;

import asset.Hero;
import asset.Unit;
import player.Player;
import asset.Cell;

public enum CellClassificator {
    GRASS("Трава", "ӧоө", 5, false, 1.5, null),
    ROAD("Дорога", "■■■",1, false, 1.5, null),
    WALL("Стена", "⛓⛓⛓",-1, true, Double.POSITIVE_INFINITY, null);

    private final String name;
    private final String design;
    private final int defaultCost;
    private final boolean wall;
    private final double enemyCoefficient;
    private final Player territoryOwner;


    CellClassificator(String name, String design, int defaultCost, boolean wall, double enemyCoefficient, Player territoryOwner) {
        this.name = name;
        this.design = design;
        this.defaultCost = defaultCost;
        this.wall = wall;
        this.enemyCoefficient = enemyCoefficient;
        this.territoryOwner = territoryOwner;
    }

    // Создание клетки по классу и владельцу
    public static Cell createCell(CellClassificator cellClass, Player owner) {
        return new Cell(
                cellClass.name(),
                resolveDesign(cellClass, owner),
                0, 0,
                cellClass.getDefaultCost(),
                cellClass.wall,
                cellClass.enemyCoefficient,
                owner
        );
    }

    public int getDefaultCost() {
        return defaultCost;
    }

    public Player getTerritoryOwner() {
        return territoryOwner;
    }

    // Создание клетки по строковому названию и владельцу
    public static Cell createCellByName(String className, Player owner) {
        for (CellClassificator cell : values()) {
            if (cell.name().equalsIgnoreCase(className)) {
                return createCell(cell, owner);
            }
        }
        throw new IllegalArgumentException("Unknown cell type: " + className);
    }


    private static String resolveDesign(CellClassificator cellClass, Player owner) {
        String design = cellClass.getDesign();
        if (owner == null) return design.substring(1, 2);
        System.out.println("выбираем дизайн клетке игроку " + owner + design.substring(0, 1) + design.substring(2, 3));
        return switch (owner.getNickname()) {
            case "Player" -> design.substring(0, 1);
            case "Computer" -> design.substring(2, 3);
            default -> design.substring(1, 2);
        };
    }

    public String getDesign() {
        return design;
    }

    public String getName() {
        return name;
    }

}
