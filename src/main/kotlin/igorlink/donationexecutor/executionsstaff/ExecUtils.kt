package igorlink.donationexecutor.executionsstaff

import org.bukkit.entity.Player
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.List

object ExecUtils {
    fun giveToPlayer(player: Player, material: Material?, amount: Int, donationUsername: String, itemName: String?) {
        val itemStack = ItemStack(material!!, amount)
        val meta = itemStack.itemMeta
        meta.setDisplayName(itemName)
        meta.lore = List.of("§7Подарочек от §e$donationUsername")
        itemStack.itemMeta = meta
        player.world.dropItemNaturally(player.location.clone().add(player.location.direction.setY(0).normalize()), itemStack)
    }

    @JvmStatic
    fun giveToPlayer(player: Player, material: Material?, amount: Int, donationUsername: String) {
        val itemStack = ItemStack(material!!, amount)
        val meta = itemStack.itemMeta
        meta.lore = List.of("§7Подарочек от §e$donationUsername")
        itemStack.itemMeta = meta
        player.world.dropItemNaturally(player.location.clone().add(player.location.direction.setY(0).normalize()), itemStack)
    }
}