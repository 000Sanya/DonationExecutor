package igorlink.donationexecutor

import org.bukkit.plugin.java.JavaPlugin
import igorlink.donationexecutor.playersmanagement.StreamerPlayersManager
import igorlink.service.MainConfig
import igorlink.command.ReloadSubCommand
import igorlink.command.DonateSubCommand
import igorlink.command.FilterSubCommand
import org.bukkit.Bukkit

class DonationExecutor : JavaPlugin() {
    lateinit var streamerPlayersManager: StreamerPlayersManager
    lateinit var executor: Executor
    lateinit var mainConfig: MainConfig

    override fun onEnable() {
        mainConfig = MainConfig()
        mainConfig.reload(this)
        executor = Executor(this)
        streamerPlayersManager = StreamerPlayersManager(this)
        getCommand("reload")!!.setExecutor(ReloadSubCommand(this))
        getCommand("donate")!!.setExecutor(DonateSubCommand(this))
        getCommand("filter")!!.setExecutor(FilterSubCommand())
        Bukkit.getPluginManager().registerEvents(GeneralEventListener(this), this)
    }

    override fun onDisable() {
        streamerPlayersManager.stop()
    }

    fun reloadMainConfig() {
        mainConfig.reload(this)
    }
}