package igorlink.donationexecutor.playersmanagement.donationalerts

import igorlink.donationexecutor.DonationExecutor
import igorlink.donationexecutor.playersmanagement.Donation
import igorlink.donationexecutor.playersmanagement.StreamerPlayer
import igorlink.service.logToConsole
import org.bukkit.Bukkit
import java.net.URISyntaxException
import kotlin.math.roundToInt

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
            Bukkit.getPlayerExact(sp.name)?.let {
                if (!it.isDead) {
                    val donation = sp.takeDonationFromQueue() ?: return@let
                    logToConsole("Отправлен на выполнение донат §b${donation.executionName}§f для стримера §b${sp.name}§f от донатера §b${donation.name}")
                    donationExecutor.executor.doExecute(sp.name, donation.name, donation.amount, donation.executionName)
                }
            }
        }
    }

    fun getStreamerPlayer(name: String): StreamerPlayer? {
        return listOfStreamerPlayers
            .firstOrNull { it.name == name }
    }

    //Добавление доната в очередь
    fun addToDonationsQueue(donation: Donation) {
        logToConsole("Игроки ${listOfStreamerPlayers.joinToString(", ") { it.name }}")
        for (sp in listOfStreamerPlayers) {
            val execution = sp.checkExecution(donation.amount.roundToInt())
            logToConsole("За цену ${donation.amount.roundToInt()} игроку ${sp.name} выдан будет ${execution}")
            if (execution != null) {
                sp.putDonationToQueue(donation.copy(executionName = execution))
                logToConsole("Донат от §b${donation.name}§f в размере §b${donation.amount} руб.§f был обработан и отправлен в очередь на выполнение.")
            }
        }
    }

    fun disconnect() {
        donationAlertsConnection.disconnect()
    }

    val numberOfStreamerPlayers: Int
        get() = listOfStreamerPlayers.size
}