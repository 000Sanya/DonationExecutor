package igorlink.donationexecutor

import igorlink.command.DonationExecutorCommand
import igorlink.donationexecutor.playersmanagement.StreamerPlayersManager
import igorlink.service.MainConfig
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class DonationExecutor : JavaPlugin() {
    lateinit var streamerPlayersManager: StreamerPlayersManager
    lateinit var executor: Executor
    lateinit var mainConfig: MainConfig

    override fun onEnable() {
        mainConfig = MainConfig()
        mainConfig.reload(this)
        executor = Executor(this)
        streamerPlayersManager = StreamerPlayersManager(this)
        getCommand("donationexecutor")?.setExecutor(DonationExecutorCommand(this))
        Bukkit.getPluginManager().registerEvents(GeneralEventListener(this), this)
    }

    override fun onDisable() {
        streamerPlayersManager.stop()
    }

    fun reloadMainConfig() {
        reloadConfig()

        mainConfig.reload(this)
        streamerPlayersManager.reload()
    }
}