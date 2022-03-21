package igorlink.donationexecutor.playersmanagement.donationalerts

import igorlink.donationexecutor.DonationExecutor
import igorlink.donationexecutor.playersmanagement.Donation
import igorlink.donationexecutor.playersmanagement.StreamerPlayer
import igorlink.service.cutOffKopeykis
import igorlink.service.logToConsole
import org.bukkit.Bukkit
import java.net.URISyntaxException
import java.util.*

class DonationAlertsToken(
    val token: String,
    private val donationExecutor: DonationExecutor,
) {
    private var donationAlertsConnection: DonationAlertsConnection = DonationAlertsConnection(this, donationExecutor)
    private val listOfStreamerPlayers: MutableList<StreamerPlayer> = ArrayList()

    init {
        try {
            donationAlertsConnection.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        for (spName in donationExecutor.mainConfig.getAmounts()[token]!!.keys) {
            listOfStreamerPlayers.add(StreamerPlayer(spName, this, donationExecutor))
        }
    }

    fun executeDonationsInQueues() {
        for (sp in listOfStreamerPlayers) {
            if (Bukkit.getPlayerExact(sp.name) != null && !Objects.requireNonNull(Bukkit.getPlayerExact(sp.name))!!.isDead) {
                val donation = sp.takeDonationFromQueue() ?: continue
                logToConsole("Отправлен на выполнение донат §b" + donation.executionName + "§f для стримера §b" + sp.name + "§f от донатера §b" + donation.name)
                donationExecutor.executor.doExecute(sp.name, donation.name, donation.amount, donation.executionName)
            }
        }
    }

    fun getStreamerPlayer(name: String): StreamerPlayer? {
        for (p in listOfStreamerPlayers) {
            if (p.name == name) {
                return p
            }
        }
        return null
    }

    //Добавление доната в очередь
    fun addToDonationsQueue(donation: Donation) {
        for (sp in listOfStreamerPlayers) {
            val execution = sp.checkExecution(cutOffKopeykis(donation.amount))!!
            sp.putDonationToQueue(donation.copy(executionName = execution))
            logToConsole("Донат от §b" + donation.name + "§f в размере §b" + donation.amount + " руб.§f был обработан и отправлен в очередь на выполнение.")
            return
        }
    }

    fun disconnect() {
        donationAlertsConnection.disconnect()
    }

    val numberOfStreamerPlayers: Int
        get() = listOfStreamerPlayers.size
}