package moe.sandie.workerants;

import moe.sandie.workerants.listeners.CitizensListener;
import moe.sandie.workerants.listeners.IDependencies;
import moe.sandie.workerants.listeners.ZnpcsListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorkerAnts extends JavaPlugin {

    private IDependencies depends;
    private CitizensListener citizensListener;
    private ZnpcsListener znpcsListener;

    @Override
    public void onEnable() {
        citizensListener = new CitizensListener(this);
        znpcsListener = new ZnpcsListener(this);
        depends = new Dependencies(this);

        depends.init();

        depends.linkCitizens();
        if (depends.getZnpcs() != null) {
            getServer().getPluginManager().registerEvents(getZnpcsListener(), this);
        }
    }

    @Override
    public void onDisable() {

    }

    public CitizensListener getCitizensListener() {
        return citizensListener;
    }

    public ZnpcsListener getZnpcsListener() {
        return znpcsListener;
    }

    public Dependencies getDependencies() {
        return (Dependencies) depends;
    }
}
