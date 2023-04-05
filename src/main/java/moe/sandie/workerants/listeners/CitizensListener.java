package moe.sandie.workerants.listeners;

import moe.sandie.workerants.InventoryManager;
import moe.sandie.workerants.WorkerAnts;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Objects;


public class CitizensListener extends InventoryManager implements Listener {

    private final WorkerAnts plugin;

    public CitizensListener(final WorkerAnts plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCRightClick(final NPCRightClickEvent event) {
        if (plugin.getDependencies().getCitizens() == null) {
            plugin.getLogger().severe("Citizens is not enabled!");
            return;
        }
        if (event.getClicker() != null) {
            Player p = event.getClicker();
            NPC npc = event.getNPC();
            if (plugin.getDependencies().getSelectingNpcs().contains(p.getUniqueId())) {
                if (event.getNPC() == null) {
                    plugin.getLogger().severe("NPC was null while selecting by right-click");
                    return;
                }
                event.getClicker().acceptConversationInput(String.valueOf(event.getNPC().getUniqueId()));
            }
            plugin.getLogger().info("Right clicked npc");
            plugin.getPluginLogger().info(npc.getUniqueId().toString());
            Player NPCOwner = Bukkit.getPlayer(npc.getTrait(Owner.class).getOwnerId());

            //event.getClicker().sendMessage(ChatColor.GREEN + "NPC UUID: " + ChatColor.GOLD + npc.getUniqueId().toString());
            assert NPCOwner != null;
            event.getClicker().sendMessage(ChatColor.GREEN + "NPC OWNER: " + ChatColor.GOLD + NPCOwner.getName());

            // TODO: Write another method to check who owns NPC and open inventory for said NPC.
            // TODO: Add OP bypass

            if (!npc.getTrait(Owner.class).isOwnedBy(p)) {
                p.sendMessage(ChatColor.RED + "You can't interact with " + NPCOwner.getName() + "'s worker");
                return;
            }

            if (!plugin.getNPCDataConfig().contains("npc." + npc.getId() + ".inventory")) {
                createNPCInventory(p, npc);
            }
            loadNPCInventory(p,"npc." + npc.getId() + ".inventory");
        }

    }

    @EventHandler
    public void onNPCLeftClick(final NPCLeftClickEvent event) {
        if (Objects.equals(Objects.requireNonNull(plugin.getNPCDataConfig().get("npc." + event.getNPC().getId() + ".owner.UUID")).toString(), event.getClicker().getUniqueId().toString()) || event.getClicker().isOp()) {
            NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
            npcRegistry.deregister(event.getNPC());
            event.getNPC().destroy();
            npcRegistry.saveToStore();
            plugin.getNPCDataConfig().set("npc." + event.getNPC().getId(), null);
            plugin.saveNPCDataConfig();
        } else {
            event.getClicker().sendMessage(ChatColor.RED + "You can't delete a NPC that's not yours!");
            event.getClicker().sendMessage(ChatColor.GREEN + "NPC OWNER: " + ChatColor.GOLD + Objects.requireNonNull(plugin.getNPCDataConfig().get("npc." + event.getNPC().getId() + ".owner.name")));
        }
    }

}
