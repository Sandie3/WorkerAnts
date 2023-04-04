package moe.sandie.workerants.api;

import io.github.znetworkw.znpcservers.ServersNPC;
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Location;

import java.util.Set;
import java.util.UUID;

public interface IDependencies {

    Economy getVaultEconomy();

    Permission getVaultPermission();

    CitizensPlugin getCitizens();

    void linkCitizens();

    void unlinkCitizens();

    ServersNPC getZnpcs();

    boolean isPluginAvailable(final String pluginName);

    Location getNPCLocation(final UUID uuid);

    String getNPCName(final UUID uuid);

    Set<UUID> getSelectingNpcs();

    void init();

}
