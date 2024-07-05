package com.pedestriamc.namecolor.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

//GUI interface for objects
//https://www.spigotmc.org/threads/a-modern-approach-to-inventory-guis.594005/
public interface InventoryHandler {
    void onClick(InventoryClickEvent event);
    void onOpen(InventoryOpenEvent event);
    void onClose(InventoryCloseEvent event);

}
