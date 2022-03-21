package igorlink.command

import igorlink.service.logToConsole
import igorlink.service.sendSysMsgToPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FilterSubCommand : SubCommand {
    private fun onFilterCommand(sender: CommandSender, args: Array<out String>) {
        if (args[0].equals("on", ignoreCase = true)) {
            logToConsole("Фильтр никнеймов донатеров §bВКЛЮЧЕН")
            if (sender is Player) {
                sendSysMsgToPlayer(sender, "Фильтр никнеймов донатеров §bВКЛЮЧЕН")
            }
        } else {
            logToConsole("Фильтр никнеймов донатеров §bВЫКЛЮЧЕН")
            if (sender is Player) {
                sendSysMsgToPlayer(sender, "Фильтр никнеймов донатеров §bВЫКЛЮЧЕН")
            }
        }
    }

    override val name = "filter"

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        if (sender !== Bukkit.getConsoleSender() && !sender.hasPermission("de.filter") && !sender.isOp) {
            sendSysMsgToPlayer(sender as Player, "У вас недостаточно прав для выполнения данной\nкоманды!")
            return true
        }
        if (args.size == 2 && (args[1] == "on" || args[1] == "off")) {
            onFilterCommand(sender, args.sliceArray(1 until args.size))
            return true
        }
        return false
    }
}