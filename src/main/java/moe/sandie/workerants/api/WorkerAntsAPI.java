package moe.sandie.workerants.api;

import moe.sandie.workerants.api.IDependencies;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface WorkerAntsAPI {
    File getPluginDataFolder();
    IDependencies getDependencies();
    ISettings getSettings();
    Logger getPluginLogger();
    InputStream getPluginResource(String filename);
}
