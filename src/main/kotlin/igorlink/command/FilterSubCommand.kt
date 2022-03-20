package igorlink.command;

import igorlink.service.MainConfig;
import igorlink.service.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class FilterSubCommand implements CommandExecutor {
    public void onFilterCommand(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("on")) {
            Utils.logToConsole("Фильтр никнеймов донатеров §bВКЛЮЧЕН");
            if (sender instanceof Player) {
                Utils.sendSysMsgToPlayer((Player) sender, "Фильтр никнеймов донатеров §bВКЛЮЧЕН");
            }
        } else {
            Utils.logToConsole("Фильтр никнеймов донатеров §bВЫКЛЮЧЕН");
            if (sender instanceof Player) {
                Utils.sendSysMsgToPlayer((Player) sender,"Фильтр никнеймов донатеров §bВЫКЛЮЧЕН");
            }
        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if ((sender != Bukkit.getConsoleSender()) && (!sender.hasPermission("de.filter")) && (!sender.isOp())) {
            Utils.sendSysMsgToPlayer((Player) sender, "У вас недостаточно прав для выполнения данной\nкоманды!");
            return true;
        }
        if ((args.length == 2) && ((args[1].equals("on")) || (args[1].equals("off")))) {
            //Инициализируем список новых аргументов для субкоманды
            String[] newArgs = new String[args.length - 1];
            //Создаем новый список аргументов, копируя старый со смещением 1
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            //Вызываем обработку доната
            onFilterCommand(sender, newArgs);
            //Возвращаем true, к все прошло успешно
            return true;
        }
        return false;
    }
}
