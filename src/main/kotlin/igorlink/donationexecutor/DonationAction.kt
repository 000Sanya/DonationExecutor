package igorlink.donationexecutor

import org.bukkit.entity.Player

abstract class DonationAction protected constructor(val executionName: String) {
    abstract fun onAction(player: Player, donationUsername: String, donationAmount: String)
}