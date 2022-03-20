package igorlink.donationexecutor;

import igorlink.command.DonateSubCommand;
import igorlink.command.FilterSubCommand;
import igorlink.command.ReloadSubCommand;
import igorlink.donationexecutor.playersmanagement.StreamerPlayersManager;
import igorlink.service.MainConfig;
import igorlink.service.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import static igorlink.service.Utils.*;

public final class DonationExecutor extends JavaPlugin {
    private static Boolean isRunningStatus = true;

    public StreamerPlayersManager streamerPlayersManager;
    private Executor executor;

    private MainConfig mainConfig;

    @Override
    public void onEnable() {
        mainConfig = new MainConfig();
        mainConfig.reload(this);

        executor = new Executor(this);
        streamerPlayersManager = new StreamerPlayersManager(this);

        getCommand("reload").setExecutor(new ReloadSubCommand(this));
        getCommand("donate").setExecutor(new DonateSubCommand(this));
        getCommand("filter").setExecutor(new FilterSubCommand());

        Utils.fillTheSynonimousCharsHashMap();


        Bukkit.getPluginManager().registerEvents(new GeneralEventListener(this),this);

    }

    @Override
    public void onDisable() {
        try {
            isRunningStatus = false;
            streamerPlayersManager.stop();
        } catch (InterruptedException e) {
            logToConsole("Какая-то ебаная ошибка, похуй на нее вообще");
        }
    }


    public static Boolean isRunning() {
        return isRunningStatus;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public void reloadMainConfig() {
        mainConfig.reload(this);
    }


    public Executor getExecutor() {
        return executor;
    }
}
