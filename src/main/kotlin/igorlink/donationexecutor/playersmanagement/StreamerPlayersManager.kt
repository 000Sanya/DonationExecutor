package igorlink.donationexecutor.playersmanagement

import igorlink.donationexecutor.DonationExecutor
import igorlink.donationexecutor.playersmanagement.donationalerts.DonationAlertsToken
import igorlink.service.logToConsole
import org.bukkit.scheduler.BukkitRunnable
import kotlin.Throws
import java.lang.InterruptedException
import java.util.ArrayList

class StreamerPlayersManager(private val donationExecutor: DonationExecutor) {
    private val listOfDonationAlertsTokens: MutableList<DonationAlertsToken> = ArrayList()

    //Таймер будет выполнять донаты из очередей игроков каждые 2 сек, если они живы и онлайн - выполняем донат и убираем его из очереди
    init {
        tokensFromConfig
        object : BukkitRunnable() {
            override fun run() {
                for (token in listOfDonationAlertsTokens) {
                    token.executeDonationsInQueues()
                }
            }
        }.runTaskTimer(donationExecutor, 0, 40)
    }

    private val tokensFromConfig: Unit
        private get() {
            val tokensStringList = donationExecutor.mainConfig!!.getAmounts()!!.keys
            for (token in tokensStringList) {
                addTokenToList(token)
            }
            var numOfStreamerPlayers = 0
            for (token in listOfDonationAlertsTokens) {
                numOfStreamerPlayers += token.numberOfStreamerPlayers
            }
            logToConsole("Было добавлено §b" + listOfDonationAlertsTokens.size + " §fтокенов, с которыми связано §b" + numOfStreamerPlayers + " §fигроков.")
        }

    fun getStreamerPlayer(name: String): StreamerPlayer? {
        for (token in listOfDonationAlertsTokens) {
            if (token.getStreamerPlayer(name) != null) {
                return token.getStreamerPlayer(name)
            }
        }
        return null
    }

    fun reload() {
        for (token in listOfDonationAlertsTokens) {
            token.disconnect()
        }
        object : BukkitRunnable() {
            override fun run() {
                listOfDonationAlertsTokens.clear()
                tokensFromConfig
            }
        }.runTaskLater(donationExecutor, 20)
    }

    @Throws(InterruptedException::class)
    fun stop() {
        for (token in listOfDonationAlertsTokens) {
            token.disconnect()
        }
        Thread.sleep(1000)
        listOfDonationAlertsTokens.clear()
    }

    fun addToDonationsQueue(donation: Donation?) {
        for (token in listOfDonationAlertsTokens) {
            token.addToDonationsQueue(donation!!)
        }
    }

    private fun addTokenToList(token: String) {
        listOfDonationAlertsTokens.add(DonationAlertsToken(token, donationExecutor))
    }
}