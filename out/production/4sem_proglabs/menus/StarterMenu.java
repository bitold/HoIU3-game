package menus;

public class StarterMenu {
    GameMenu gameMenu;
    public StarterMenu(){
        gameMenu = new GameMenu();
    }

    public void startMenu(){
        gameMenu.runMainMenu();
    }
}
