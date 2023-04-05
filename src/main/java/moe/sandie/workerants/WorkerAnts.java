package moe.sandie.workerants;

import moe.sandie.workerants.WorkerEggs.WorkerSpawnEgg;
import moe.sandie.workerants.api.ISettings;
import moe.sandie.workerants.api.WorkerAntsAPI;
import moe.sandie.workerants.listeners.CitizensListener;
import moe.sandie.workerants.api.IDependencies;
import moe.sandie.workerants.commands.CommandManager;
import moe.sandie.workerants.listeners.ZnpcsListener;
import moe.sandie.workerants.util.Lang;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class WorkerAnts extends JavaPlugin implements WorkerAntsAPI {
    private boolean loading = false;
    private String bukkitVersion = "0";
    private IDependencies depends;
    private ISettings settings;
    private CitizensListener citizensListener;
    private ZnpcsListener znpcsListener;
    private TabExecutor cmdExecutor;
    private FileConfiguration NPCConfig = null;
    private File NPCConfigFile = null;

    @Override
    public void onEnable() {
        if(getServer().getPluginManager().getPlugin("Citizens") == null) {
            getLogger().log(Level.SEVERE, "Citizens not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            Material.matchMaterial("STONE", true);
        } catch (final NoSuchMethodError ignored) {
            // Do nothing
        }

        bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        settings = new Settings(this);

        citizensListener = new CitizensListener(this);
        znpcsListener = new ZnpcsListener(this);
        depends = new Dependencies(this);

        settings.init();

        // bStats
        //if (settings.getLanguage().contains("-")) {
        //    final Metrics metrics = new Metrics(this);
        //}

        // Setup lang
        try {
            setupLang();
        } catch (final IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        cmdExecutor = new CommandManager(this);

        depends.init();

        getConfig().options().copyDefaults(true);
        getConfig().options().header("See gitbook link for settings");
        saveConfig();

        depends.linkCitizens();
        if (depends.getZnpcs() != null) {
            getServer().getPluginManager().registerEvents(getZnpcsListener(), this);
        }

        ItemManager.init();
        Objects.requireNonNull(getCommand("worker")).setExecutor(getTabExecutor());
        Objects.requireNonNull(getCommand("resetnpcs")).setExecutor(getTabExecutor());

        getServer().getPluginManager().registerEvents(new WorkerSpawnEgg(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Closing WorkerAnts...");
        Bukkit.getScheduler().cancelTasks(this);
    }

    public boolean isLoading() {
        return loading;
    }

    private void setupLang() throws IOException, URISyntaxException {
        final String path = "lang";
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        if (jarFile.isFile()) {
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries();
            final Set<String> results = new HashSet<>();
            while (entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith(path + "/") && name.contains("strings.yml")) {
                    results.add(name);
                }
            }
            for (final String resourcePath : results) {
                saveResourceAs(resourcePath, resourcePath, false);
                saveResourceAs(resourcePath, resourcePath.replace(".yml", "_new.yml"), true);
            }
            jar.close();
        }
        try {
            Lang.init(this, settings.getLanguage());
        } catch (final InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveResourceAs(String resourcePath, final String outputPath, final boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        final InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath
                    + "' cannot be found in WorkerAnts jar");
        }

        final String outPath = outputPath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        final File outFile = new File(getDataFolder(), outPath);
        final File outDir = new File(outFile.getPath().replace(outFile.getName(), ""));

        if (!outDir.exists()) {
            if (!outDir.mkdirs()) {
                getLogger().log(Level.SEVERE, "Failed to make directories for " + outFile.getName() + " (canWrite= "
                        + outDir.canWrite() + ")");
            }
        }

        try {
            if (!outFile.exists() || replace) {
                final OutputStream out = new FileOutputStream(outFile);
                final byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
                if (!outFile.exists()) {
                    getLogger().severe("Unable to copy " + outFile.getName() + " (canWrite= " + outFile.canWrite()
                            + ")");
                }
            }
        } catch (final IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public void reloadNPCDataConfig() {
        String defConfigStream = "NPCData.yml";
        if (NPCConfigFile == null) {
            NPCConfigFile = new File(getDataFolder(), defConfigStream);
        }
        NPCConfig = YamlConfiguration.loadConfiguration(NPCConfigFile);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), defConfigStream));
            NPCConfig.setDefaults(defConfig);
        }

    }

    public FileConfiguration getNPCDataConfig() {
        if (NPCConfig == null) {
            reloadNPCDataConfig();
        }
        return NPCConfig;
    }

    public void saveNPCDataConfig() {
        if (NPCConfig == null || NPCConfigFile == null) {
            return;
        }
        try {
            getNPCDataConfig().save(NPCConfigFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + NPCConfigFile, ex);
        }
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

    public Settings getSettings() {
        return (Settings) settings;
    }

    public File getPluginDataFolder() {
        return getDataFolder();
    }
    public Logger getPluginLogger() {
        return getLogger();
    }
    public InputStream getPluginResource(String filename) {
        return getResource(filename);
    }
    @SuppressWarnings("unused")
    public CommandExecutor getCommandExecutor() {
        return cmdExecutor;
    }
    public TabExecutor getTabExecutor() {
        return cmdExecutor;
    }
}
