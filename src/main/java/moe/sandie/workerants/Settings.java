package moe.sandie.workerants;

import moe.sandie.workerants.api.ISettings;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Settings implements ISettings {
    private final WorkerAnts plugin;

    private String language = "en-US";
    private boolean languageOverrideClient;
    private boolean genFilesOnJoin = true;
    private int consoleLogging = 1;

    public Settings(final WorkerAnts plugin){ this.plugin = plugin; }

    @Override
    public String getLanguage() {
        return language;
    }
    @Override
    public void setLanguage(final String language) {
        this.language = language;
    }
    @Override
    public boolean canLanguageOverrideClient() {
        return languageOverrideClient;
    }
    @Override
    public void setLanguageOverrideClient(final boolean languageOverrideClient) {
        this.languageOverrideClient = languageOverrideClient;
    }
    public boolean canGenFilesOnJoin() {
        return genFilesOnJoin;
    }
    public void setGenFilesOnJoin(final boolean genFilesOnJoin) {
        this.genFilesOnJoin = genFilesOnJoin;
    }
    public int getConsoleLogging() {
        return consoleLogging;
    }
    public void setConsoleLogging(final int consoleLogging) {
        this.consoleLogging = consoleLogging;
    }

    @Override
    public void init() {
        final FileConfiguration config = plugin.getConfig();
        genFilesOnJoin = config.getBoolean("generate-files-on-join", true);
        if (Objects.requireNonNull(config.getString("language")).equalsIgnoreCase("en")) {
            //Legacy
            language = "en-US";
        } else {
            language = config.getString("language", "en-US");
        }
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
