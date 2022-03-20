package igorlink.command

import igorlink.service.logToConsole
import igorlink.service.sendSysMsgToPlayer
import org.bukkit.command.CommandExecutor
import org.bukkit.entity.Player
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class FilterSubCommand : CommandExecutor {
    private fun onFilterCommand(sender: CommandSender, args: Array<String?>) {
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

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (sender !== Bukkit.getConsoleSender() && !sender.hasPermission("de.filter") && !sender.isOp) {
            sendSysMsgToPlayer(sender as Player, "У вас недостаточно прав для выполнения данной\nкоманды!")
            return true
        }
        if (args.size == 2 && (args[1] == "on" || args[1] == "off")) {
            //Инициализируем список новых аргументов для субкоманды
            val newArgs = arrayOfNulls<String>(args.size - 1)
            //Создаем новый список аргументов, копируя старый со смещением 1
            System.arraycopy(args, 1, newArgs, 0, args.size - 1)
            //Вызываем обработку доната
            onFilterCommand(sender, newArgs)
            //Возвращаем true, к все прошло успешно
            return true
        }
        return false
    }
}