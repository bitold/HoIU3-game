import castle.CastleUI;
import castle.Castle;
import classification.ShopBuildingClassificator;
import classification.UnitClassificator;
import map.GameMap;
import menus.BuyingMenu;
import asset.Hero;
import player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CastleUITest {

    @Mock
    private Castle castle;
    @Mock
    private BuyingMenu buyingMenu;
    @Mock
    private Player player;
    @Mock
    private Hero hero;
    @Mock
    private GameMap gameMap;

    private CastleUI castleUI;

    @BeforeEach
    void setUp() {
        castleUI = new CastleUI(castle);
        castleUI.setBuyingMenu(buyingMenu);
    }

    @Test
    void testInitializeShop_PopulatesBuildingsAndUnits() {
        List<ShopBuildingClassificator> buildingValues = Arrays.asList(ShopBuildingClassificator.values());
        List<UnitClassificator> unitValues = Arrays.asList(UnitClassificator.values());

        assertEquals(buildingValues.size(), castleUI.getBuildings().size());
        assertTrue(castleUI.getBuildings().containsAll(buildingValues));

        assertEquals(unitValues.size(), castleUI.getUnits().size());
        assertTrue(castleUI.getUnits().containsAll(unitValues));
    }

    @Test
    void testCheckUnitAvailability_WhenBuildingExists_ReturnsTrue() {
        UnitClassificator unit = UnitClassificator.SWORDSMAN;
        ShopBuildingClassificator mockBuilding = mock(ShopBuildingClassificator.class);
        when(mockBuilding.getUnit()).thenReturn(unit);
        when(castle.getBuildings()).thenReturn(new ArrayList<>(Arrays.asList(mockBuilding)));
        assertTrue(castleUI.checkUnitAvailability(unit));
    }

    @Test
    void testCheckUnitAvailability_WhenNoBuilding_ReturnsFalse() {
        // Явно создаем пустой ArrayList нужного типа
        when(castle.getBuildings()).thenReturn(new ArrayList<ShopBuildingClassificator>());

        UnitClassificator unit = UnitClassificator.SWORDSMAN;
        assertFalse(castleUI.checkUnitAvailability(unit));
    }

    @Test
    void testBuyBuilding_SufficientGold_BuildsAndDeductsGold() {
        ShopBuildingClassificator building = mock(ShopBuildingClassificator.class);
        int cost = 500;
        when(building.getCost()).thenReturn(cost);
        when(player.getGold()).thenReturn(1000);
        when(castle.getOwner()).thenReturn(player);
        castleUI.buyBuilding(building);

        verify(castle).build(building);
        verify(player).takeGold(cost);
        verify(buyingMenu).printBuildingBought(building);
    }

    @Test
    void testBuyBuilding_InsufficientGold_PrintsError() {
        ShopBuildingClassificator building = mock(ShopBuildingClassificator.class);
        int cost = 500;
        when(building.getCost()).thenReturn(cost);
        when(player.getGold()).thenReturn(100);
        when(castle.getOwner()).thenReturn(player);
        castleUI.buyBuilding(building);

        verify(castle, never()).build(building);
        verify(player, never()).takeGold(anyInt());
        verify(buyingMenu).printNotEnoughGold();
    }

    @Test
    void testBuyUnit_WhenUnitAvailableAndEnoughGold_BuysUnit() {
        UnitClassificator unit = mock(UnitClassificator.class);
        ShopBuildingClassificator mockBuilding = mock(ShopBuildingClassificator.class);
        when(mockBuilding.getUnit()).thenReturn(unit);
        when(castle.getBuildings()).thenReturn(new ArrayList<>(List.of(mockBuilding)));
        int cost = 300;
        when(unit.getCost()).thenReturn(cost);
        when(player.getGold()).thenReturn(500);
        when(castle.getOwner()).thenReturn(player);
        when(hero.getOwner()).thenReturn(player);
        when(player.getNickname()).thenReturn("Player");
        when(unit.getDesign()).thenReturn("123");
        castleUI.buyUnit(unit, hero);

        verify(hero).addUnit(any());
        verify(player).takeGold(cost);
        verify(buyingMenu).printUnitBought(unit);
    }

    @Test
    void testBuyUnit_NotEnoughGold_PrintsError() {
        UnitClassificator unit = mock(UnitClassificator.class);
        ShopBuildingClassificator mockBuilding = mock(ShopBuildingClassificator.class);
//        when(mockBuilding.getUnit()).thenReturn(unit);
//        when(castle.getBuildings()).thenReturn(new ArrayList<>(List.of(mockBuilding)));
        int cost = 300;
        when(unit.getCost()).thenReturn(cost);
        when(player.getGold()).thenReturn(200);
        when(castle.getOwner()).thenReturn(player);

        castleUI.buyUnit(unit, hero);

        verify(hero, never()).addUnit(any());
        verify(player, never()).takeGold(anyInt());
        verify(buyingMenu).printNotEnoughGold();
    }

    @Test
    void testBuyUnit_NoRequiredBuilding_PrintsError() {
        when(castle.getOwner()).thenReturn(player);
        UnitClassificator unit = mock(UnitClassificator.class);
        when(castle.getBuildings()).thenReturn(new ArrayList<>(Collections.emptyList()));
        int cost = 300;
        when(unit.getCost()).thenReturn(cost);
        when(player.getGold()).thenReturn(500);

        castleUI.buyUnit(unit, hero);

        verify(buyingMenu).printNoRequiredBuilding();
    }

    @Test
    void testBuyHero_WhenEnoughGold_AddsHeroAndSpawns() {
        Hero heroToBuy = mock(Hero.class);
        when(castle.getOwner()).thenReturn(player);
        when(player.getGold()).thenReturn(150);
        when(castle.getMap()).thenReturn(gameMap);
        castleUI.buyHero(heroToBuy);
        verify(player).addHero(heroToBuy);
        verify(player).takeGold(100);
        verify(buyingMenu).printHeroBought();
    }

    @Test
    void testBuyHero_NotEnoughGold_PrintsError() {
        when(castle.getOwner()).thenReturn(player);
        when(player.getGold()).thenReturn(90);
        Hero hero = mock(Hero.class);

        castleUI.buyHero(hero);

        verify(player, never()).addHero(any());
        verify(buyingMenu).printNotEnoughGold();
    }

    @Test
    void testStartShop_CallsEngageAndLeaves() {
        castleUI.enterCastle(hero);
        assertNotNull(castleUI.getHero());
        castleUI.startShop();
        assertNull(castleUI.getHero());
        verify(buyingMenu).engageShopMenu();
    }

    @Test
    void testEnterAndLeaveCastle() {
        castleUI.enterCastle(hero);
        assertEquals(hero, castleUI.getHero());
        castleUI.leaveCastle();
        assertNull(castleUI.getHero());
    }
}