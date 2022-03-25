package igorlink.donationexecutor.playersmanagement

import igorlink.donationexecutor.DonationExecutor
import igorlink.donationexecutor.playersmanagement.donationalerts.DonationAlertsToken
import igorlink.service.logToConsole
import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

class StreamerPlayer(
    val name: String,
    donationAlertsToken: DonationAlertsToken,
    donationExecutor: DonationExecutor,
) {
    private val listOfDeathDropItems: MutableList<Item> = mutableListOf()
    private val listOfQueuedDonations: Queue<Donation> = LinkedList()
    private val listOfAmounts = HashMap<Int, String>()

    //Инициализация нового объекта стримера-игрока
    init {
        Bukkit.getPluginManager().registerEvents(
            object : Listener {
                @EventHandler
                private fun onPlayerDeath(event: PlayerDeathEvent) {
                    val deathDrop: MutableList<Item> = ArrayList()
                    if (event.player.name == name) {
                        for (i in event.drops) {
                            deathDrop.add(event.player.world.dropItemNaturally(event.player.location, i))
                        }
                    }
                    event.drops.clear()
                    setDeathDrop(deathDrop)
                }
            },
            donationExecutor
        )

        for (execName in donationExecutor.executor.executionsNamesList) {
            val amount = donationExecutor.mainConfig.getAmounts()[donationAlertsToken.token]!![name]!![execName]
            if (amount != null) {
                listOfAmounts[amount] = execName
            } else {
                logToConsole("Сумма доната, необходимая для $execName для стримера $name не найдена. Проверьте правильность заполнения файла конфигурации DonationExecutor.yml в папке с именем плагина.")
            }
        }
    }

    fun checkExecution(amount: Int): String? {
        return listOfAmounts[amount]
    }

    //Работа со списком выпавших при смерти вещей
    fun setDeathDrop(listOfItems: List<Item>?) {
        listOfDeathDropItems.clear()
        listOfDeathDropItems.addAll(listOfItems!!)
    }

    //Работа с очередью донатов
    //Поставить донат в очередь на выполнение донатов для этого игрока
    fun putDonationToQueue(donation: Donation) {
        listOfQueuedDonations.add(donation)
    }

    //Взять донат из очереди и убрать его из нее
    fun takeDonationFromQueue(): Donation? {
        return listOfQueuedDonations.poll()
    }

    //Удалить дроп игрока после смерти
    fun removeDeathDrop(): Boolean {
        var wasAnythingDeleted = false
        for (i in listOfDeathDropItems) {
            if (i.isDead) {
                continue
            }
            i.remove()
            wasAnythingDeleted = true
        }
        return wasAnythingDeleted
    }
}