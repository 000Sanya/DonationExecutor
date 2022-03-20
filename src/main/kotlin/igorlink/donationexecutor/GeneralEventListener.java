package igorlink.donationexecutor;

import igorlink.service.MainConfig;
import igorlink.service.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


import static igorlink.service.Utils.*;

public class GeneralEventListener implements Listener {
    private final DonationExecutor donationExecutor;

    public GeneralEventListener(DonationExecutor donationExecutor) {
        this.donationExecutor = donationExecutor;
    }

    //Закачка ресурспака и оповещение о том, что плагин не активен, если он не активен
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (donationExecutor.getMainConfig().isForceResourcePack()) {
            e.getPlayer().setResourcePack("https://download.mc-packs.net/pack/65429ea1f5aae3b47e879834a1c538fa390f4b9b.zip", Utils.decodeUsingBigInteger("65429ea1f5aae3b47e879834a1c538fa390f4b9b"));
        }

        if (donationExecutor.getMainConfig().isOptifineNotificationOn()) {
            sendSysMsgToPlayer(e.getPlayer(), "для отображения кастомных скинов плагина на вашем\nклиенте игры должен быть установлен мод §bOptiFine.\n \n§fЕсли у вас не установлен данный мод, скачать его вы\nможете по ссылке: §b§nhttps://optifine.net/downloads\n\n§7§oДанное оповещение можно отключить в файле настроек\nплагина в папке сервера /plugins/DonationExecutor/\n \n");
        }

        if (!isPluginActive()) {
            sendSysMsgToPlayer(e.getPlayer(), "плагин не активен. Укажите токен и свой никнейм в файле конфигурации плагина и перезапустите сервер.");
        }
    }
}

