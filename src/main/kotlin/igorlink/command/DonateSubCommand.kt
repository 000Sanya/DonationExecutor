package igorlink.command

import igorlink.donationexecutor.DonationExecutor
import org.bukkit.command.CommandExecutor
import java.lang.StringBuilder
import igorlink.donationexecutor.playersmanagement.Donation
import igorlink.service.sendSysMsgToPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DonateSubCommand(private val donationExecutor: DonationExecutor) : CommandExecutor {
    private fun onDonateCommand(sender: CommandSender, args: Array<String?>) {

        //Getting donation's amount
        val donationAmount: String?
        val donationUsername = StringBuilder()

        //Getting donation's amount
        donationAmount = args[0]

        //Получаем имя донатера
        var i = 1
        while (i <= args.size - 1) {
            if (i == 1) {
                donationUsername.append(args[i])
            } else {
                donationUsername.append(' ')
                donationUsername.append(args[i])
            }
            i++
        }


        //Отправляем донат на исполнение
        donationExecutor.streamerPlayersManager.addToDonationsQueue(Donation(sender, donationUsername.toString(), "$donationAmount.00"))
    }

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (sender !== Bukkit.getConsoleSender() && !sender.hasPermission("de.donate") && !sender.isOp) {
            sendSysMsgToPlayer(sender as Player, "У вас недостаточно прав для выполнения данной\nкоманды!")
            return true
        }
        if (args.size >= 2) {
            //Инициализируем список новых аргументов для субкоманды
            val newArgs = arrayOfNulls<String>(args.size - 1)
            //Создаем новый список аргументов, копируя старый со смещением 1
            System.arraycopy(args, 1, newArgs, 0, args.size - 1)
            //Вызываем обработку доната
            onDonateCommand(sender, newArgs)
            //Возвращаем true, к все прошло успешно
            return true
        }
        return false
    }
}