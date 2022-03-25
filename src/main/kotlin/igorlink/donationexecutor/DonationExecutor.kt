package igorlink.donationexecutor

import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import igorlink.command.DonationExecutorCommand
import igorlink.donationexecutor.playersmanagement.StreamerPlayersManager
import igorlink.service.MainConfig
import org.bukkit.Bukkit

class DonationExecutor : SuspendingJavaPlugin() {
    lateinit var streamerPlayersManager: StreamerPlayersManager
    lateinit var executor: Executor
    lateinit var mainConfig: MainConfig

    override suspend fun onEnableAsync() {
        mainConfig = MainConfig()
        mainConfig.reload(this)
        executor = Executor(this)
        streamerPlayersManager = StreamerPlayersManager(this)
        getCommand("donationexecutor")?.setExecutor(DonationExecutorCommand(this))
        Bukkit.getPluginManager().registerEvents(GeneralEventListener(this), this)
    }

    override suspend fun onDisableAsync() {
        streamerPlayersManager.stop()
    }

    fun reloadMainConfig() {
        reloadConfig()

        mainConfig.reload(this)
        streamerPlayersManager.reload()
    }
}