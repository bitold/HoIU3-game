package menus;

import asset.Entity;
import asset.Hero;
import asset.Unit;
import control.Controller;

import java.util.ArrayList;
import java.util.Objects;

public class ControllerMenu {
    private Controller controller;
    private Hero battlingHero;
    private boolean move;
    public ControllerMenu(Controller controller) {
        this.controller = controller;
        controller.setControllerMenu(this);
        move = false;
    }

    public boolean moveEntityControlCycle(){
        move = true;
        controller.setControlled(null);
        while (move) {
            engageInMoveMenu();
        }
        return true;
    }

    public void engageInMoveMenu(){
        if (Objects.isNull(controller.getControlled())){
            pickEntity();
        }
        if (Objects.isNull(controller.getControlled())){
            return;
        }
        controller.setMove(true);
        while (controller.control());
    }

    boolean endMoveCompletely(){
        return false;
    }


    public void pickEntity(){
        printEntityChooseMenu();
        processChoice(controller.keyboardHandler());
    }


    void processChoice(String input){
        if (input.startsWith("/PASS")){
            move = false;
            return;
        }
        int choosen = Controller.stringToInt(input);
        if (choosen < 0 || choosen > resolveControlledPool().size()-1) {
            System.out.println("Ошибка: с таким номером никого нет!");
            pickEntity();
        } else {
            controller.setControlled((Entity) resolveControlledPool().get(choosen));
        }
    }

    ArrayList<?> resolveControlledPool(){
        if (controller.getGame().isBattling()){
            return battlingHero.getArmy();
        } else {
            return controller.getOwner().getHeroes();
        }
    }

    public void printUnitChooseMenu(){
        ArrayList<Unit> army = battlingHero.getArmy();
        System.out.println("Выберите, кем ходить:");
        for (int i = 0; i < army.size(); i++) {
            System.out.println(i + ". " + army.get(i) + ", " + army.get(i).getCurrentMP() + " MP, " + army.get(i).getCurrentHealth() + " HP");
        }
        System.out.println("Для пропуска хода введите /PASS");
    }
    public void printHeroChooseMenu(){
        ArrayList<Hero> heroes = controller.getOwner().getHeroes();
        System.out.println("Выберите, кем ходить:");
        for (int i = 0; i < heroes.size(); i++) {
            System.out.println(i + ". " + heroes.get(i) + ", " + heroes.get(i).getCurrentMP() + " MP");
        }
        System.out.println("Для пропуска хода введите /PASS");
    }
    public void printEntityChooseMenu(){
        if (controller.getGame().isBattling()){
            printUnitChooseMenu();
        } else {
            printHeroChooseMenu();
        }
    }


    public void setBattlingHero(Hero battlingHero) {
        this.battlingHero = battlingHero;
    }

    public void setMove(boolean move) {
        this.move = move;
    }
}
