package moe.sandie.workerants.api;

public interface ISettings {

    String getLanguage();
    void setLanguage(final String language);
    boolean canLanguageOverrideClient();
    void setLanguageOverrideClient(final boolean languageOverrideClient);
    boolean canGenFilesOnJoin();
    void setGenFilesOnJoin(final boolean genFilesOnJoin);
    int getConsoleLogging();
    void init();
}
