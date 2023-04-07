package moe.sandie.workerants;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Translatable;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST, "Worker menu");

        ItemStack filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemStack close = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemStack stats = new ItemStack(Material.PLAYER_HEAD, 1);

        ItemMeta metaFiller = filler.getItemMeta();
        ItemMeta metaClose = close.getItemMeta();
        SkullMeta metaStats = (SkullMeta) stats.getItemMeta();

        assert metaFiller != null;
        assert metaClose != null;
        assert metaStats != null;

        ArrayList<String> loreStats = new ArrayList<>();

        metaFiller.setDisplayName(" ");

        metaClose.setDisplayName(ChatColor.RED + "Close menu");

        metaStats.setDisplayName(ChatColor.GOLD + "Worker stats");
        metaStats.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        metaStats.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        loreStats.add(player.getName() + "'s worker");
        loreStats.add(" ");
        loreStats.add(ChatColor.RESET + "" + ChatColor.GRAY + "Worker level: " + ChatColor.LIGHT_PURPLE + 1);
        loreStats.add(ChatColor.RESET + "" + ChatColor.GRAY + "Worker level: " + ChatColor.LIGHT_PURPLE + 9999);
        loreStats.add(ChatColor.RESET + "" + ChatColor.GRAY + "Worker level: " + ChatColor.LIGHT_PURPLE + "AYO");
        loreStats.add(ChatColor.RESET + "" + ChatColor.GRAY + "Worker level: " + ChatColor.LIGHT_PURPLE + "?????");

        metaStats.setLore(loreStats);

        metaStats.setOwnerProfile(player.getPlayerProfile());

        filler.setItemMeta(metaFiller);
        close.setItemMeta(metaClose);
        stats.setItemMeta(metaStats);

        for (int i = 0; i < inv.getSize(); i++ ){
            inv.setItem(i, filler);
        }
        inv.setItem(15, close);
        inv.setItem(11, stats);

        saveNPCInventory(npc, inv);
        this.inv = inv;

        player.openInventory(inv);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("Worker menu")) {
            e.setCancelled(true);
            switch (e.getSlot()) {
                case 11:
                    break;
                case 15:
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

    public void loadNPCInventory(Player p, NPC npc){
        this.npc = npc;
        String path = "npc." + npc.getId() + ".inventory.slot.";
        Inventory inventory = Bukkit.createInventory(p, InventoryType.CHEST, "Worker menu");

        for (int i = 0; i < inventory.getSize(); i++){
            inventory.setItem(i, (ItemStack) plugin.getNPCDataConfig().get(path + i));
        }

        inv = inventory;

        p.openInventory(inventory);
    }
}
