package moe.sandie.workerants;

import moe.sandie.workerants.listeners.CitizensListener;
import moe.sandie.workerants.listeners.IDependencies;

import net.citizensnpcs.api.CitizensPlugin;
import io.github.znetworkw.znpcservers.ServersNPC;
import io.github.znetworkw.znpcservers.npc.NPC;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.RegisteredServiceProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Dependencies implements IDependencies {

    private final WorkerAnts plugin;
    private static Economy economy = null;
    public static CitizensPlugin citizens = null;
    private static ServersNPC znpcs = null;
    private static Permission permission = null;

    public Dependencies(final WorkerAnts plugin) {
        this.plugin = plugin;
    }

    public Economy getVaultEconomy() {
        if (economy == null && isPluginAvailable("Vault")) {
            if (!setupEconomy()) {
                plugin.getLogger().warning("Economy provider not found.");
            }
        }
        return economy;
    }

    public Permission getVaultPermission() {
        if (permission == null && isPluginAvailable("Vault")) {
            if (!setupPermissions()) {
                plugin.getLogger().warning("Permission provider not found.");
            }
        }
        return permission;
    }

    public CitizensPlugin getCitizens() {
        if (citizens == null) {
            linkCitizens();
        }
        return citizens;
    }

    public void linkCitizens() {
        if (isPluginAvailable("Citizens")) {
            try {
                citizens = (CitizensPlugin) plugin.getServer().getPluginManager().getPlugin("Citizens");
                boolean found = false;
                for (final RegisteredListener listener : HandlerList.getRegisteredListeners(plugin)) {
                    if (listener.getListener() instanceof CitizensListener) {
                        found = true;
                    }
                }
                if (!found) {
                    plugin.getServer().getPluginManager().registerEvents(plugin.getCitizensListener(), plugin);
                    //if (plugin.getSettings().canNpcEffects()) {
                    //    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, plugin.getNpcEffectThread(),
                    //            20, 20);
                    //}
                    plugin.getLogger().info("Successfully linked Quests with Citizens "
                            + citizens.getDescription().getVersion());
                }
            } catch (final Exception e) {
                plugin.getLogger().warning("Legacy version of Citizens found. Citizens in Quests not enabled.");
            }
        }
    }

    public void unlinkCitizens() {
        citizens = null;
    }

    public ServersNPC getZnpcs() {
        if (znpcs == null) {
            znpcs = (ServersNPC) plugin.getServer().getPluginManager().getPlugin("ServersNPC");
        }
        return znpcs;
    }

    public Set<UUID> getZnpcsUuids() {
        if (znpcs != null && isPluginAvailable("ServersNPC")) {
            // TODO - it seems ZNPCs UUIDs do not persist restart
            return io.github.znetworkw.znpcservers.npc.NPC.all().stream()
                    .map(io.github.znetworkw.znpcservers.npc.NPC::getUUID).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public @Nullable Location getNPCLocation(final UUID uuid) {
        if (citizens != null && citizens.getNPCRegistry().getByUniqueId(uuid) != null) {
            return citizens.getNPCRegistry().getByUniqueId(uuid).getStoredLocation();
        } else if (getZnpcsUuids().contains(uuid)) {
            final Optional<NPC> opt = NPC.all().stream().filter(npc1 -> npc1.getUUID().equals(uuid)).findAny();
            if (opt.isPresent()) {
                return opt.get().getLocation();
            }
        }
        return null;
    }

    public @Nullable Entity getNPCEntity(final UUID uuid) {
        if (citizens != null && citizens.getNPCRegistry().getByUniqueId(uuid) != null) {
            return citizens.getNPCRegistry().getByUniqueId(uuid).getEntity();
        } else if (getZnpcsUuids().contains(uuid)) {
            final Optional<NPC> opt = NPC.all().stream().filter(npc1 -> npc1.getUUID().equals(uuid)).findAny();
            if (opt.isPresent()) {
                return (Entity) opt.get().getBukkitEntity();
            }
        }
        return null;
    }

    public @NotNull String getNPCName(final UUID uuid) {
        Entity npc = null;
        if (citizens != null && citizens.getNPCRegistry().getByUniqueId(uuid) != null) {
            return citizens.getNPCRegistry().getByUniqueId(uuid).getName();
        } else if (getZnpcsUuids().contains(uuid)) {
            final Optional<NPC> opt = NPC.all().stream().filter(npc1 -> npc1.getUUID().equals(uuid)).findAny();
            if (opt.isPresent()) {
                npc = (Entity) opt.get().getBukkitEntity();
                if (npc.getCustomName() != null) {
                    return npc.getCustomName();
                } else {
                    return opt.get().getNpcPojo().getHologramLines().get(0);
                }
            }
        }
        return "NPC";
    }

    public @Nullable UUID getUUIDFromNPC(final Entity entity) {
        if (citizens != null && citizens.getNPCRegistry().isNPC(entity)) {
            return citizens.getNPCRegistry().getNPC(entity).getUniqueId();
        } else if (getZnpcsUuids().contains(entity.getUniqueId())) {
            final Optional<NPC> opt = NPC.all().stream().filter(npc1 -> npc1.getUUID().equals(entity.getUniqueId())).findAny();
            if (opt.isPresent()) {
                return opt.get().getUUID();
            }
        }
        return null;
    }

    public boolean isPluginAvailable(final String pluginName) {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null ) {
            try {
                if (!Objects.requireNonNull(plugin.getServer().getPluginManager().getPlugin(pluginName)).isEnabled()) {
                    plugin.getLogger().warning(pluginName
                            + " was detected, but is not enabled! Fix "+ pluginName + " to allow linkage.");
                } else {
                    return true;
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean setupEconomy() {
        try {
            final RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager()
                    .getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
            return economy != null;
        } catch (final Exception e) {
            return false;
        }
    }

    private boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (rsp != null) {
            permission = rsp.getProvider();
        }
        return permission != null;
    }

    public void init() {
        getCitizens();
        getVaultEconomy();
        getVaultPermission();
    }

}
