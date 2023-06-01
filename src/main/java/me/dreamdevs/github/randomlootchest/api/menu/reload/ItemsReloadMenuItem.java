package me.dreamdevs.github.randomlootchest.api.menu.reload;

import me.dreamdevs.github.randomlootchest.RandomLootChestMain;
import me.dreamdevs.github.randomlootchest.api.events.ClickInventoryEvent;
import me.dreamdevs.github.randomlootchest.api.menu.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;

public class ItemsReloadMenuItem extends MenuItem {

    public ItemsReloadMenuItem() {
        super(Material.DIAMOND_SWORD, RandomLootChestMain.getInstance().getMessagesManager().getMessage("chest-menu-reload-items-reload-name"), new ArrayList<>());
    }

    @Override
    public void performAction(ClickInventoryEvent event) {
        event.getPlayer().closeInventory();
        RandomLootChestMain.getInstance().getConfigManager().reload("items.yml");
        RandomLootChestMain.getInstance().getItemsManager().load(RandomLootChestMain.getInstance());
        event.getPlayer().sendMessage(RandomLootChestMain.getInstance().getMessagesManager().getMessage("chest-command-reload-items"));
    }
}