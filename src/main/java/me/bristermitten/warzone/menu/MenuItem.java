package me.bristermitten.warzone.menu;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public record MenuItem(String itemName, ItemStack item, List<Integer> slots, MenuAction action) {

}
