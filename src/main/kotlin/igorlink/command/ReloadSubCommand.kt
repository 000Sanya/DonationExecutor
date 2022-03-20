package igorlink.command;

import igorlink.donationexecutor.DonationExecutor;
import igorlink.service.MainConfig;
import igorlink.service.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReloadSubCommand implements CommandExecutor {
    private final DonationExecutor donationExecutor;

    public ReloadSubCommand(DonationExecutor donationExecutor) {
        this.donationExecutor = donationExecutor;
    }

    public void onReloadCommand(CommandSender sender) {
        donationExecutor.reloadConfig();
        donationExecutor.streamerPlayersManager.reload();
        Utils.logToConsole("Настройки успешно обновлены!");
        if (sender instanceof Player) {
            Utils.sendSysMsgToPlayer(Objects.requireNonNull(((Player) sender).getPlayer()), "Настройки успешно обновлены!");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if ((sender != Bukkit.getConsoleSender()) && (!sender.hasPermission("de.reload")) && (!sender.isOp())) {
            Utils.sendSysMsgToPlayer((Player) sender, "У вас недостаточно прав для выполнения данной\nкоманды!");
            return true;
        }
        if (args.length == 1) {
            onReloadCommand(sender);
            return true;
        }
        return false;
    }
}
