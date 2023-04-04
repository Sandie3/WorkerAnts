package moe.sandie.workerants;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    public final WorkerAnts plugin;
    public static ItemStack npcSpawnEgg;

    public ItemManager(WorkerAnts plugin) {
        this.plugin = plugin;
    }

    public static void init(){
        createNpcSpawnEgg();
    }

    private static void createNpcSpawnEgg() {
        ItemStack item = new ItemStack(Material.ZOMBIE_SPAWN_EGG, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Worker spawn egg");
        List<String> lore = new ArrayList<>();
        lore.add("§7Worker spawn egg");
        meta.setLore(lore);
        meta.setCustomModelData(10000);
        item.setItemMeta(meta);
        npcSpawnEgg = item;
    }

}
