package com.pedestriamc.namecolor.gui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ColorGUI
{

    private HashMap<ItemStack, ChatColor> colorMap;


    public ColorGUI() {
    }

    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
    }
    public void onOpen(InventoryOpenEvent event) {}
    public void onClose(InventoryCloseEvent event) {}
}
