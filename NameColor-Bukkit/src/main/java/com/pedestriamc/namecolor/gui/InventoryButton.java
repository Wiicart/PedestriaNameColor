package com.pedestriamc.namecolor.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryButton {
    private final ItemStack icon;

    public InventoryButton(ItemStack icon) {
        this.icon = icon;
    }
    public ItemStack getIcon() {
        return this.icon;
    }
    public abstract void onClick(InventoryClickEvent event);
}
