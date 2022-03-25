package igorlink.command

import igorlink.donationexecutor.DonationExecutor
import igorlink.donationexecutor.playersmanagement.Donation
import igorlink.service.sendSysMsgToPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DonateSubCommand(private val donationExecutor: DonationExecutor) : SubCommand {
    private fun onDonateCommand(sender: CommandSender, args: Array<out String>) {
        val donationAmount = args[0].toFloat()
        val donationUsername = args
            .slice(1 until args.size)
            .joinToString(" ")


        donationExecutor.streamerPlayersManager.addToDonationsQueue(
            Donation(donationUsername, donationAmount)
        )
    }

    override val name = "donate"

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        if (sender !== Bukkit.getConsoleSender() && !sender.hasPermission("de.donate") && !sender.isOp) {
            sendSysMsgToPlayer(sender as Player, "У вас недостаточно прав для выполнения данной\nкоманды!")
            return true
        }
        if (args.size >= 2) {
            onDonateCommand(sender, args.sliceArray(1 until args.size))
            return true
        }
        return false
    }
}