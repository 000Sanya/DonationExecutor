package igorlink.command

import igorlink.donationexecutor.DonationExecutor
import igorlink.service.logToConsole
import igorlink.service.sendSysMsgToPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReloadSubCommand(private val donationExecutor: DonationExecutor) : SubCommand {
    private fun onReloadCommand(sender: CommandSender) {
        donationExecutor.reloadMainConfig()

        logToConsole("Настройки успешно обновлены!")
        if (sender is Player) {
            sendSysMsgToPlayer(sender.player!!, "Настройки успешно обновлены!")
        }
    }

    override val name = "reload"
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        if (sender !== Bukkit.getConsoleSender() && !sender.hasPermission("de.reload") && !sender.isOp) {
            sendSysMsgToPlayer(sender as Player, "У вас недостаточно прав для выполнения данной\nкоманды!")
            return true
        }
        if (args.size == 1) {
            onReloadCommand(sender)
            return true
        }
        return false
    }
}