package com.pedestriamc.namecolor.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public abstract class InventoryGUI implements InventoryHandler{
    private final Inventory inventory;
    private final Map<Integer, InventoryButton> buttonMap = new HashMap<>();
    public InventoryGUI(){
        this.inventory = this.createInventory();

    }
    public Inventory getInventory(){
        return this.inventory;
    }
    public void addButton(int slot, InventoryButton button){
        this.buttonMap.put(slot, button);
    }
    public void decorate(){
        this.buttonMap.forEach((slot, button) -> this.inventory.setItem(slot,button.getIcon()));
    }
    @Override
    public void onClick(InventoryClickEvent event){
        event.setCancelled(true);
        int slot = event.getSlot();
        InventoryButton button = this.buttonMap.get(slot);
        if(button != null){
            button.onClick(event);
        }
    }
    @Override
    public void onOpen(InventoryOpenEvent event){
        this.decorate();
    }
    @Override
    public void onClose(InventoryCloseEvent event){}

    protected abstract Inventory createInventory();

}
