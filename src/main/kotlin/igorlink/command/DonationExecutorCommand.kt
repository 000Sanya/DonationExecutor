package igorlink.command

import igorlink.donationexecutor.DonationExecutor
import igorlink.service.logToConsole
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class DonationExecutorCommand(
    val donationExecutor: DonationExecutor,
    private val donateSubCommand: DonateSubCommand = DonateSubCommand(donationExecutor),
    private val filterSubCommand: FilterSubCommand = FilterSubCommand(),
    private val reloadSubCommand: ReloadSubCommand = ReloadSubCommand(donationExecutor),
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }
        try {
            when (args[0]) {
                "reload" -> reloadSubCommand.onCommand(sender, command, label, args)
                "donate" -> donateSubCommand.onCommand(sender, command, label, args)
                "filter" -> filterSubCommand.onCommand(sender, command, label, args)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logToConsole("Произошла неизвестная ошибка при выполнении команды!")
            return false
        }
        return false
    }
}