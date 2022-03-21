package igorlink.donationexecutor.playersmanagement.donationalerts

import igorlink.donationexecutor.playersmanagement.Donation
import igorlink.service.logToConsole
import igorlink.service.runTask
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.bukkit.plugin.java.JavaPlugin
import org.json.JSONObject
import java.net.URI

class DonationAlertsConnection(
    private val donationAlertsToken: DonationAlertsToken,
    javaPlugin: JavaPlugin,
) {
    private val socket: Socket

    init {
        val url = URI(DASERVER)
        socket = IO.socket(url)
        val connectListener = Emitter.Listener {
            socket.emit(
                "add-user",
                JSONObject()
                    .put("token", donationAlertsToken.token)
                    .put("type", "minor")
            )
            logToConsole("Произведено успешное подключение для токена §b${donationAlertsToken.token}")
        }
        val disconectListener = Emitter.Listener { logToConsole("Произведено отключение для токена §b${donationAlertsToken.token}") }
        val errorListener = Emitter.Listener { logToConsole("Произошла ошибка подключения к Donation Alerts!") }
        val donationListener = Emitter.Listener { arg0 ->
            val json = JSONObject(arg0[0] as String)

            javaPlugin.runTask {
                if (json.getInt("alert_type") != 1) {
                    return@runTask
                }
                val donationUsername = if (json.isNull("username")) {
                    ""
                } else {
                    json.getString("username")
                }
                val donationAmount = json.getString("amount_formatted")
                donationAlertsToken.addToDonationsQueue(
                    Donation(
                        donationUsername,
                        donationAmount
                    )
                )
            }
        }
        socket.on(Socket.EVENT_CONNECT, connectListener)
            .on(Socket.EVENT_DISCONNECT, disconectListener)
            .on(Socket.EVENT_ERROR, errorListener)
            .on("donation", donationListener)
    }

    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    companion object {
        private const val DASERVER = "https://socket.donationalerts.ru:443"
    }
}