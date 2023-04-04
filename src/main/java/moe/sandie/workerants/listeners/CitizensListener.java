package moe.sandie.workerants.listeners;

import de.themoep.inventorygui.GuiStorageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import moe.sandie.workerants.ItemManager;
import moe.sandie.workerants.WorkerAnts;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static moe.sandie.workerants.WorkerEggs.WorkerSpawnEgg.AssociatedNPC;


public class CitizensListener implements Listener, InventoryHolder {

    private final WorkerAnts plugin;

    public CitizensListener(final WorkerAnts plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCRightClick(final NPCRightClickEvent event){
        if (plugin.getDependencies().getCitizens() == null){
            plugin.getLogger().severe("NPC was null while selecting by right-click");
            return;
        }
        if (plugin.getDependencies().getSelectingNpcs().contains(event.getClicker().getUniqueId())) {
            if (event.getNPC() == null) {
                plugin.getLogger().severe("NPC was null while selecting by right-click");
                return;
            }
            event.getClicker().acceptConversationInput(String.valueOf(event.getNPC().getUniqueId()));
        }
        plugin.getLogger().info("Right clicked npc");
        plugin.getPluginLogger().info(event.getNPC().getUniqueId().toString());
        String playername = Bukkit.getPlayer(event.getNPC().getTrait(Owner.class).getOwnerId()).getName();

        for (UUID i : AssociatedNPC.keySet()){
            event.getClicker().sendMessage(ChatColor.GOLD + AssociatedNPC.get(i).toString());
        }

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);

        // TODO: Add npc inventory

        if (event.getNPC().getTrait(Owner.class).isOwnedBy(event.getClicker())){
            event.getClicker().sendMessage(ChatColor.GREEN + "Npc is yours");
        }
        else{
            event.getClicker().sendMessage(ChatColor.RED + "Npc is not yours");
            event.getClicker().sendMessage(ChatColor.RED + "Npc is owned by " + playername);
        }
    }

    private void saveInv(Inventory inv) {
    }


    @EventHandler
    public void onNPCLeftClick(final NPCLeftClickEvent event) {
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        npcRegistry.deregister(event.getNPC());
        event.getNPC().destroy();
        npcRegistry.saveToStore();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}
