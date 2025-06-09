package menus;

import java.util.Scanner;

public class StarterMenu {
    GameMenu gameMenu;
    transient Scanner scanner = new Scanner(System.in);
    public StarterMenu(){
        gameMenu = new GameMenu();
    }

    public void startMenu(){
        System.out.print("Введите никнейм: ");
        String nickname = scanner.next();
        gameMenu.mainMenuCycle(nickname);
    }
}
