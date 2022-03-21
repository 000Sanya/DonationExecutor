package igorlink.donationexecutor

import igorlink.service.decodeUsingBigInteger
import igorlink.service.sendSysMsgToPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class GeneralEventListener(private val donationExecutor: DonationExecutor) : Listener {
    //Закачка ресурспака и оповещение о том, что плагин не активен, если он не активен
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (donationExecutor.mainConfig.isForceResourcePack) {
            e.player.setResourcePack("https://download.mc-packs.net/pack/65429ea1f5aae3b47e879834a1c538fa390f4b9b.zip",
                decodeUsingBigInteger("65429ea1f5aae3b47e879834a1c538fa390f4b9b"))
        }
        if (donationExecutor.mainConfig.isOptifineNotificationOn) {
            sendSysMsgToPlayer(e.player,
                "для отображения кастомных скинов плагина на вашем\nклиенте игры должен быть установлен мод §bOptiFine.\n \n§fЕсли у вас не установлен данный мод, скачать его вы\nможете по ссылке: §b§nhttps://optifine.net/downloads\n\n§7§oДанное оповещение можно отключить в файле настроек\nплагина в папке сервера /plugins/DonationExecutor/\n \n")
        }
    }
}