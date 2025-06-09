import asset.Hero;
import asset.Unit;
import game.Game;
import menus.GameMenu;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import castle.CastleUI;
import castle.Castle;
import classification.ShopBuildingClassificator;
import classification.UnitClassificator;
import map.GameMap;
import menus.BuyingMenu;
import asset.Hero;
import org.mockito.Mockito;
import player.Computer;
import player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GameLogicTest {


    @Mock
    private Game game;


    @Mock
    private GameMenu gameMenu;
    @Mock
    private GameMap gameMap;
    @Mock
    private Castle castle;

    private Player player;
    private Computer computer;

    private Hero playerHero;
    private Hero computerHero;


    private Unit playerUnit;
    private Unit computerUnit;

    @Test
    void gameLogicPlayerLossByDeath(){
        when(game.getGameMenu()).thenReturn(gameMenu);
        when(game.getGameMap()).thenReturn(gameMap);
        playerHero = new Hero();
        computerHero = new Hero();


        player = new Player(new ArrayList<>(List.of(playerHero)), 100, "Player", game);
        player.setCastle(castle);
        when(castle.isCaptured()).thenReturn(false);
        player.setHeroes(player.getHeroes());
        computer = new Computer(new ArrayList<>(List.of(computerHero)), 100, player, game.getGameMap(), null, game);
        computer.setHeroes(computer.getHeroes());

        playerUnit = UnitClassificator.createUnit(UnitClassificator.SWORDSMAN, playerHero);
        playerUnit.setMap(gameMap);
        playerHero.addUnit(playerUnit);
        computerUnit = UnitClassificator.createUnit(UnitClassificator.SWORDSMAN, computerHero);
        computerHero.addUnit(computerUnit);

        doNothing().when(gameMap).removeEntity(any());
        playerUnit.dealDamage(10^985);
        playerUnit.checkLiving();

        when(game.getCurrentlyMoving()).thenReturn(player);
        doCallRealMethod().when(game).gameCycle();
        doCallRealMethod().when(game).checkEndgame(player);
        //game.checkEndgame(player);

        game.gameCycle();
        verify(game).gameCycle();
        verify(game).checkEndgame(player);
        Assertions.assertTrue(game.checkEndgame(player));
        verify(game, atLeastOnce()).addLoser(player);
        verify(gameMenu, atLeastOnce()).printGameOver();
    }

}
