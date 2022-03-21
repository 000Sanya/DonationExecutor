package igorlink.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

interface SubCommand {
    val name: String

    fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean
}