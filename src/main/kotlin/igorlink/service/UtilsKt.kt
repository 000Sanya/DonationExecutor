package igorlink.service

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.math.BigInteger
import java.util.*
import java.util.regex.Pattern

fun logToConsole(text: String) {
    Bukkit.getConsoleSender().sendMessage("§c[DonationExecutor] §f$text")
}

fun sendSysMsgToPlayer(player: Player, text: String) {
    player.sendMessage("§c[DE] §f$text")
}

fun decodeUsingBigInteger(hexString: String): ByteArray? {
    val byteArray = BigInteger(hexString, 16).toByteArray()
    if (byteArray[0].toInt() == 0) {
        val output = ByteArray(byteArray.size - 1)
        System.arraycopy(byteArray, 1, output, 0, output.size)
        return output
    }
    return byteArray
}

fun announce(donaterName: String, subText: String, alterSubtext: String, player: Player, donationAmount: String, bigAnnounce: Boolean, mainConfig: MainConfig) {
    var _donaterName = donaterName
    if (bigAnnounce) {
        if (donaterName == "") {
            _donaterName = "Кто-то"
        }
        if (mainConfig.showBigAnnouncement) {
            player.sendTitle("§c$_donaterName", "§f$subText за §b$donationAmount§f руб.", 7, mainConfig.timeForAnnouncement * 20, 7)
        }
        player.sendMessage("§c[DE] §fДонатер §c$_donaterName", "§f$subText за §b$donationAmount§f руб.")
    }
    if (_donaterName == "") {
        _donaterName = "Кто-то"
    }
    for (p in Bukkit.getOnlinePlayers()) {
        if (p.name != player.name) {
            p.sendMessage("§c[DE] §fДонатер §c" + _donaterName + " §f" + alterSubtext + " §b" + player.name + " за §b" + donationAmount + "§f руб.")
        }
    }
}

fun cutOffKopeykis(donationAmountWithKopeykis: String): String {
    return donationAmountWithKopeykis
        .filter { it != ' ' }
        .takeWhile { it != '.' }
}

private val mapOfSynonimousChars = mapOf(
    'h' to listOf('x', 'х', 'н', 'n'),
    'n' to listOf('н', 'й', 'и'),
    'н' to listOf('h', 'n', 'й', 'и'),
    'e' to listOf('е', '3', 'з'),
    'е' to listOf('e', '3', 'з'),
    'г' to listOf('r', 'я', 'g', '7', '6'),
    'r' to listOf('г', 'я', 'g', '7', '6'),
    'g' to listOf('г', 'r', '7', '6'),
    'p' to listOf('п', 'р', 'n', 'я', 'r'),
    'р' to listOf('p', 'r', 'я'),
    'п' to listOf('p', 'n', 'и', 'р'),
    'o' to listOf('о', '0'),
    'о' to listOf('o', '0'),
    'a' to listOf('а'),
    'а' to listOf('a'),
    'и' to listOf('i', 'n', 'e', 'е', '|', 'l', '!', '1', '3', 'й'),
    'i' to listOf('1', 'и', 'e', 'е', '|', 'l', '!', 'й'),
    'с' to listOf('c', 's', '$', '5'),
    's' to listOf('c', 'с', '$', '5'),
    'c' to listOf('s', 'с', '$', '5'),
    'л' to listOf('l', '1', '|'),
    'l' to listOf('л', '1', '|', '!'),
    '1' to listOf('л', 'i', 'l', '|'),
    'd' to listOf('д', 'л'),
    'д' to listOf('d', 'л', '9'),
    'y' to listOf('у', 'u', 'ы'),
    'у' to listOf('y', 'u', 'ы'),
    'x' to listOf('х', 'h'),
    'х' to listOf('x', 'h'),
    'ы' to listOf('у', 'u', 'y'),
    'ч' to listOf('4'),
    'k' to listOf('к'),
    'к' to listOf('k'),
    '0' to listOf('о', 'o'),
    '3' to listOf('e', 'е', 'з'),
    '4' to listOf('ч'),
    '5' to listOf('с', 'c', 's'),
    '9' to listOf('r', 'я'),
)

fun isBlackListed(text: String, blackListedSubstrings: List<String>): Boolean {
    var validationText = text.lowercase(Locale.getDefault())
    val pattern = Pattern.compile("[l1i]*[\\-]*[l1i]*")
    val matcher = pattern.matcher(validationText)

    if (matcher.find() && matcher.group().isNotEmpty()) {
        validationText = validationText.replace(matcher.group(), "н")
    }

    validationText = validationText
        .replace("_", "")
        .replace(" ", "")
        .replace(",", "")
        .replace(".", "")
        .replace("-", "")
        .replace("%", "")
        .replace("*", "")
        .replace("?", "")

    if (validationText.isEmpty()) {
        return false
    }

    if (!validationText.matches(Regex("[a-zа-я0-9$!ё]*"))) {
        return true
    }

    for (ss in blackListedSubstrings) {
        for (i in 0..validationText.length - ss.length) {
            var tempi = i
            for (j in 0..ss.length) {
                if (j == ss.length) {
                    //если мы прошли всю субстроку до конца - значит слово содержит субстроку из блеклиста
                    return true
                }

                //Если текущая буква субстроки равна или синонимична текущей букве в слове, значит идем дальше смотреть следующий символ
                if (validationText[tempi + j] == ss[j]) {
                    continue
                } else if (mapOfSynonimousChars.containsKey(ss[j]) && mapOfSynonimousChars[ss[j]]!!.contains(
                        validationText[tempi + j])
                ) {
                    continue
                }
                while (true) {
                    if (j == 0) {
                        break
                    }
                    if (validationText[tempi + j] != validationText[tempi + j - 1]) {
                        if (!mapOfSynonimousChars.containsKey(validationText[tempi + j])) {
                            break
                        } else if (!mapOfSynonimousChars[validationText[tempi + j]]!!.contains(validationText[tempi + j - 1])) {
                            break
                        }
                    }
                    tempi++
                    if (validationText.length - tempi - j < ss.length - j) {
                        break
                    }
                }
                if (validationText.length - tempi - j < ss.length - j) {
                    break
                }
                if (validationText[tempi + j] == ss[j]) {
                    continue
                } else if (mapOfSynonimousChars.containsKey(ss[j])) {
                    if (mapOfSynonimousChars[ss[j]]!!.contains(validationText[tempi + j])) {
                        continue
                    }
                }
                break
            }
        }
    }
    return false
}

inline fun JavaPlugin.runTask(crossinline block: () -> Unit) {
    object : BukkitRunnable() {
        override fun run() = block()
    }.runTask(this)
}