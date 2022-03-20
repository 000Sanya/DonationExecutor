package igorlink.donationexecutor.playersmanagement

import org.bukkit.command.CommandSender

class Donation(private val sender: CommandSender, _username: String, _amount: String) {
    var name: String? = null
    val amount: String
    var executionName: String? = null

    init {
        if (_username == "") {
            name = "Аноним"
        } else {
            name = _username
        }
        amount = _amount
    }
}