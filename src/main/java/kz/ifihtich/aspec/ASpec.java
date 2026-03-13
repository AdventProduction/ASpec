package kz.ifihtich.aspec;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class ASpec extends JavaPlugin {

    private static ASpec instance;

    private SpectateManager spectateManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Utils.logo();
        spectateManager = new SpectateManager();
        SpectateCommand spectateCommand = new SpectateCommand(spectateManager);
        getCommand("spec").setExecutor(spectateCommand);
        getServer().getPluginManager().registerEvents(new SpectateListener(spectateManager),this);


    }

    @Override
    public void onDisable() {
        if (spectateManager != null) {
            for (Player spectator : new ArrayList<>(spectateManager.getSpectators())) {
                if (spectator.isOnline()) {
                    spectateManager.specStop(spectator);
                }
            }
        }
    }

    public static ASpec getInstance(){
        return instance;
    }
}
