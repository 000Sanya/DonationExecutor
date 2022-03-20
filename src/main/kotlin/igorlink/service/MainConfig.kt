package igorlink.service

import igorlink.donationexecutor.DonationExecutor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.ConfigurationSection
import java.util.*

class MainConfig {
    var diamondsAmount = 0
        private set
    var breadAmount = 0
        private set
    var bigBoomRadius = 0
        private set
    var timeForAnnouncement = 0
        private set
    private var token: String? = null
    var listOfBlackListedSubstrings: List<String> = ArrayList()
        private set
    var listOfWhiteListedSubstrings: List<String> = ArrayList()
    var isForceResourcePack = false
        private set
    var isOptifineNotificationOn = false
        private set
    var showBigAnnouncement = false
    private lateinit var amounts: MutableMap<String, Map<String, Map<String, String?>>>

    //Загрузка данных из конфигфайла с указанным параметром перезагрузки
    fun reload(donationExecutor: DonationExecutor) {
        donationExecutor.saveDefaultConfig()
        donationExecutor.reloadConfig()
        val config = donationExecutor.config
        diamondsAmount = config.getInt("diamonds-amount")
        breadAmount = config.getInt("bread-amount")
        bigBoomRadius = config.getInt("big-boom-radius")
        timeForAnnouncement = config.getInt("announcement-duration-seconds")
        timeForAnnouncement = config.getInt("announcement-duration-seconds")
        token = config.getString("DonationAlertsToken")
        listOfBlackListedSubstrings = config.getStringList("BlacklistedSubstrings")
        listOfWhiteListedSubstrings = config.getStringList("WhitelistedSubstrings")
        showBigAnnouncement = config.getBoolean("show-big-announcement")
        isOptifineNotificationOn = config.getBoolean("notify-about-optifine")
        isForceResourcePack = config.getBoolean("force-download-resourcepack")
        amounts = HashMap()
        val tokens = config.getConfigurationSection("donation-amounts")!!
        for (token in Objects.requireNonNull(tokens).getKeys(false)) {
            val playersMap = HashMap<String, Map<String, String?>>()
            val players = tokens.getConfigurationSection(token)
            for (player in Objects.requireNonNull(players)!!.getKeys(false)) {
                val amountMap = HashMap<String, String?>()
                val amountSection = players!!.getConfigurationSection(player)
                for (amountKey in Objects.requireNonNull(amountSection)!!.getKeys(false)) {
                    amountMap[amountKey] = amountSection!!.getString(amountKey)
                }
                playersMap[player] = amountMap
            }
            amounts[token] = playersMap
        }
    }

    fun getAmounts(): Map<String, Map<String, Map<String, String?>>>? {
        return amounts
    }
}