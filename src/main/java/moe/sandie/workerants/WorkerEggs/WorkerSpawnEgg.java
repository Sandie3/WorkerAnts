package moe.sandie.workerants.WorkerEggs;

import moe.sandie.workerants.ItemManager;
import moe.sandie.workerants.WorkerAnts;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class WorkerSpawnEgg implements Listener {

    public final WorkerAnts plugin;

    public WorkerSpawnEgg(WorkerAnts plugin) {
        this.plugin = plugin;
    }

    public static HashMap<UUID, UUID> AssociatedNPC = new HashMap<>();


    public void createWorkerNpc(Location spawnLocation, Location playerLocation, Player player) {
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        boolean npc = false;
        try {
            npc = plugin.getNPCDataConfig().contains("npcs.user." + player.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!npc) {
            NPC spawnNPC = npcRegistry.createNPC(player.getType(), player.getName() + "'s Worker", spawnLocation);
            spawnNPC.faceLocation(playerLocation);
            spawnNPC.getOrAddTrait(Owner.class).setOwner(player.getUniqueId());

            UUID myuuid = player.getUniqueId();
            UUID npcuuid = spawnNPC.getUniqueId();
            AssociatedNPC.put(myuuid, npcuuid);

            plugin.getNPCDataConfig().set("npcs.user." + player.getName() + ".NPC", npcuuid.toString());
            plugin.saveNPCDataConfig();

            Equipment equipTrait = spawnNPC.getOrAddTrait(Equipment.class);
            //ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD, 1);
            ItemStack IRONHelmet = new ItemStack(Material.IRON_HELMET, 1);
            ItemStack IRONChest = new ItemStack(Material.IRON_CHESTPLATE, 1);
            ItemStack IRONPants = new ItemStack(Material.IRON_LEGGINGS, 1);
            ItemStack IRONBoots = new ItemStack(Material.IRON_BOOTS, 1);

            //equipTrait.set(0, diamondSword);
            equipTrait.set(1, IRONHelmet);
            equipTrait.set(2, IRONChest);
            equipTrait.set(3, IRONPants);
            equipTrait.set(4, IRONBoots);

            player.sendMessage(ChatColor.GREEN + "Spawned in " + player.getName() + "'s worker");
            npcRegistry.saveToStore();
        } else {
            player.sendMessage(ChatColor.RED + "You already own a worker");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
                if (event.getItem() != null && event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getLore() != null
                        && event.getItem().getItemMeta().getLore().contains(ItemManager.npcSpawnEgg.getItemMeta().getLore().get(0))) {
                    Location spawnLocation;
                    Location playerLocation;
                    if (event.getClickedBlock().isPassable()) {
                        spawnLocation = event.getClickedBlock().getLocation().add(0.5, 0, 0.5);
                    } else {
                        spawnLocation = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().add(0.5, 0, 0.5);
                    }
                    playerLocation = event.getPlayer().getLocation();
                    CitizensAPI.getPlugin().saveConfig();
                    createWorkerNpc(spawnLocation, playerLocation, event.getPlayer());
                    if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
}
