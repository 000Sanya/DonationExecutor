package igorlink.command;

import igorlink.donationexecutor.DonationExecutor;
import igorlink.donationexecutor.playersmanagement.Donation;
import igorlink.service.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DonateSubCommand implements CommandExecutor {
    private final DonationExecutor donationExecutor;

    public DonateSubCommand(DonationExecutor donationExecutor) {
        this.donationExecutor = donationExecutor;
    }

    public void onDonateCommand(CommandSender sender, String[] args) {
        int i;

        //Getting donation's amount
        String donationAmount;
        StringBuilder donationUsername = new StringBuilder();
        StringBuilder donationMessage = new StringBuilder();

        //Getting donation's amount
        donationAmount = args[0];

        //Получаем имя донатера
        for (i = 1; i <= args.length - 1; i++) {
            if (i == 1) {
                donationUsername.append(args[i]);
            } else {
                donationUsername.append(' ');
                donationUsername.append(args[i]);
            }
        }


        //Отправляем донат на исполнение
        donationExecutor.streamerPlayersManager.addToDonationsQueue(new Donation(sender, donationUsername.toString(), donationAmount+".00"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if ((sender != Bukkit.getConsoleSender()) && (!sender.hasPermission("de.donate")) && (!sender.isOp())) {
            Utils.sendSysMsgToPlayer((Player) sender, "У вас недостаточно прав для выполнения данной\nкоманды!");
            return true;
        }
        if (args.length >= 2) {
            //Инициализируем список новых аргументов для субкоманды
            String[] newArgs = new String[args.length - 1];
            //Создаем новый список аргументов, копируя старый со смещением 1
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            //Вызываем обработку доната
            onDonateCommand(sender, newArgs);
            //Возвращаем true, к все прошло успешно
            return true;
        }
        return false;
    }
}
