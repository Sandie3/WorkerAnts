package moe.sandie.workerants.listeners;

import moe.sandie.workerants.WorkerAnts;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class CitizensListener implements Listener {

    private final WorkerAnts plugin;

    public CitizensListener(final WorkerAnts plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCRightClick(final NPCRightClickEvent event){
        if (plugin.getDependencies().getCitizens() == null){
            return;
        }
    }

}
