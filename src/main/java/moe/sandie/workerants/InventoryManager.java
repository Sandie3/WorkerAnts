package moe.sandie.workerants;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Translatable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Objects;


public class InventoryManager {

    private final WorkerAnts plugin;

    public InventoryManager(WorkerAnts plugin) {
        this.plugin = plugin;
    }

    public NPC npc;
    public Inventory inv;

    public void createNPCInventory(Player player, NPC npc){
        this.npc = npc;
        this.inv = Bukkit.createInventory(player, InventoryType.CHEST, "Worker menu");
        ItemStack filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemStack ref1 = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta metaFiller = filler.getItemMeta();
        ItemMeta metaRef1 = ref1.getItemMeta();

        assert metaRef1 != null;
        metaRef1.setDisplayName(ChatColor.RED + "Close menu");
        assert metaFiller != null;
        metaFiller.setDisplayName("");

        ref1.setItemMeta(metaRef1);
        filler.setItemMeta(metaFiller);
        for (int i = 0; i < inv.getSize(); i++ ){
            inv.setItem(i, filler);
        }
        inv.setItem(26, ref1);

        //plugin.getNPCDataConfig().set("npc." + npc.getId() + ".inventory", inv);
        saveNPCInventory(npc, inv);

        player.openInventory(inv);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("Worker menu")) {
            switch (e.getSlot()) {
                case 26:
                    e.setCancelled(true);
                    saveNPCInventory(npc, inv);
                    p.closeInventory();
                    break;
            }
        }
    }
    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent e){
        if (e.getView().getTitle().equals("Worker menu")) saveNPCInventory(npc, inv);
    }

    public void saveNPCInventory(NPC npc, Inventory inventory){
        String path = "npc." + npc.getId() + ".inventory.slot.";
        for (int i = 0; i < inventory.getSize(); i++){
            plugin.getNPCDataConfig().set(path + i, inventory.getItem(i) != null ? inventory.getItem(i) : null);
        }
        plugin.saveNPCDataConfig();
    }

    public void loadNPCInventory(Player p, String path){
        Inventory inventory = Bukkit.createInventory(p, InventoryType.CHEST, "Worker menu");

        for (int i = 0; i < inventory.getSize(); i++){
            inventory.setItem(i, (ItemStack) plugin.getNPCDataConfig().get(path + ".slot." + i));
        }

        // probably don't need this / might actually break things...
        // Will have to test later. I've been debugging for 8 hours...
        inv = inventory;

        p.openInventory(inventory);
    }
}
