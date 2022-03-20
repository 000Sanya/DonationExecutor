package igorlink.service;

import igorlink.donationexecutor.DonationExecutor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MainConfig {
    private int diamondsAmount;
    private int breadAmount;
    private int bigBoomRadius;
    private int timeForAnnouncement;
    private String token;
    private List<String> listOfBlackListedSubstrings = new ArrayList<>();
    public List<String> listOfWhiteListedSubstrings = new ArrayList<>();
    private boolean forceResourcePack;
    private boolean optifineNotification;
    public boolean showBigAnnouncement;

    private Map<String, Map<String, Map<String, String>>> amounts;

    //Загрузка данных из конфигфайла с указанным параметром перезагрузки
    public void reload(DonationExecutor donationExecutor) {
        donationExecutor.saveDefaultConfig();
        donationExecutor.reloadConfig();

        FileConfiguration config = donationExecutor.getConfig();

        diamondsAmount = config.getInt("diamonds-amount");
        breadAmount = config.getInt("bread-amount");
        bigBoomRadius = config.getInt("big-boom-radius");
        timeForAnnouncement = config.getInt("announcement-duration-seconds");
        timeForAnnouncement = config.getInt("announcement-duration-seconds");

        token = config.getString("DonationAlertsToken");
        listOfBlackListedSubstrings = config.getStringList("BlacklistedSubstrings");
        listOfWhiteListedSubstrings = config.getStringList("WhitelistedSubstrings");

        showBigAnnouncement = config.getBoolean("show-big-announcement");
        optifineNotification = config.getBoolean("notify-about-optifine");
        forceResourcePack = config.getBoolean("force-download-resourcepack");

        amounts = new HashMap<>();

        var tokens = config.getConfigurationSection("donation-amounts");
        for (var token : Objects.requireNonNull(tokens).getKeys(false)) {
            var playersMap = new HashMap<String, Map<String, String>>();

            var players = tokens.getConfigurationSection(token);

            for (var player : Objects.requireNonNull(players).getKeys(false)) {
                var amountMap = new HashMap<String, String>();

                var amountSection = players.getConfigurationSection(player);

                for (var amountKey : Objects.requireNonNull(amountSection).getKeys(false)) {
                    amountMap.put(amountKey, amountSection.getString(amountKey));
                }

                playersMap.put(player, amountMap);
            }

            amounts.put(token, playersMap);
        }
    }

    public boolean isForceResourcePack() {
        return forceResourcePack;
    }

    public boolean isOptifineNotificationOn() {
        return optifineNotification;
    }

    public boolean getshowBigAnnouncement() {
        return showBigAnnouncement;
    }

    public int getBigBoomRadius() {
        return bigBoomRadius;
    }

    public int getDiamondsAmount() {
        return diamondsAmount;
    }

    public int getBreadAmount() {
        return breadAmount;
    }

    public int getTimeForAnnouncement() {
        return timeForAnnouncement;
    }

    public List<String> getListOfBlackListedSubstrings() {
        return listOfBlackListedSubstrings;
    }

    public Map<String, Map<String, Map<String, String>>> getAmounts() {
        return amounts;
    }
}
