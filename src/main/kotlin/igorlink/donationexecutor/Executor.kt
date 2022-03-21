package igorlink.donationexecutor

import igorlink.donationexecutor.executionsstaff.ExecUtils.giveToPlayer
import igorlink.service.announce
import igorlink.service.isBlackListed
import igorlink.service.logToConsole
import igorlink.service.sendSysMsgToPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import java.util.*

class Executor(val donationExecutor: DonationExecutor) {
    private val donationActions: MutableMap<String, DonationAction> = mutableMapOf()

    private fun MutableMap<String, DonationAction>.add(action: DonationAction) {
        put(action.executionName, action)
    }

    private inline fun onExecute(executionName: String, crossinline block: (Player, String, String) -> Unit) {
        donationActions.add(object : DonationAction(executionName) {
            override fun onAction(player: Player, donationUsername: String, donationAmount: String) = block(player, donationUsername, donationAmount)
        })
    }

    val executionsNamesList: Set<String>
        get() = donationActions.keys

    init {
        onExecute("Lesch", ::lesch)
        onExecute("DropActiveItem", ::dropActiveItem)
        onExecute("PowerKick", ::powerKick)
        onExecute("ClearLastDeathDrop", ::clearLastDeathDrop)
        onExecute("SpawnCreeper", ::spawnCreeper)
        onExecute("GiveDiamonds", ::giveDiamonds)
        onExecute("GiveStackOfDiamonds", ::giveStackOfDiamonds)
        onExecute("GiveBread", ::giveBread)
        onExecute("RandomChange", ::randomChange)
        onExecute("TamedBecomesEnemies", ::tamedBecomesEnemies)
        onExecute("HalfHeart", ::halfHeart)
        onExecute("BigBoom", ::bigBoom)
        onExecute("SetNight", ::setNight)
        onExecute("SetDay", ::setDay)
        onExecute("GiveIronSet", ::giveIronSet)
        onExecute("GiveIronSword", ::giveIronSword)
        onExecute("GiveIronKirka", ::giveIronKirka)
        onExecute("GiveDiamondKirka", ::giveDiamondKirka)
        onExecute("TakeOffBlock", ::takeOffBlock)
        onExecute("GiveDiamondSet", ::giveDiamondSet)
        onExecute("GiveDiamondSword", ::giveDiamondSword)
        onExecute("SpawnTamedDog", ::spawnTamedDog)
        onExecute("SpawnTamedCat", ::spawnTamedCat)
        onExecute("HealPlayer", ::healPlayer)
    }

    fun doExecute(streamerName: String, donationUsername: String, fullDonationAmount: String, executionName: String) {
        val streamerPlayer = Bukkit.getPlayerExact(streamerName)

        //Определяем игрока (если он оффлайн - не выполняем донат и пишем об этом в консоль), а также определяем мир, местоположение и направление игрока
        if (streamerPlayer == null || streamerPlayer.isDead) {
            logToConsole("Донат от §b$donationUsername §f в размере §b$fullDonationAmount§f выполнен из-за того, что целевой стример был недоступен.")
            return
        }

        //Если имя донатера не указано - устанавливаем в качестве имени "Кто-то"
        val validDonationUsername: String
        if (donationUsername == "") {
            validDonationUsername = "Аноним"
        } else if (!isBlackListed(donationUsername, donationExecutor.mainConfig.listOfBlackListedSubstrings)) {
            validDonationUsername = donationUsername
        } else {
            validDonationUsername = "Донатер"
            logToConsole("§eникнейм донатера §f$donationUsername§e был скрыт, как подозрительный")
            streamerPlayer.sendActionBar("НИКНЕЙМ ДОНАТЕРА БЫЛ СКРЫТ")
        }


        donationActions[executionName]?.onAction(streamerPlayer, validDonationUsername, fullDonationAmount)
    }

    private fun dropActiveItem(player: Player, donationUsername: String, donationAmount: String) {
        if (player.equipment.itemInMainHand.type == Material.AIR) {
            announce(donationUsername,
                "безуспешно пытался выбить у тебя предмет из рук",
                "безуспешно пытался выбить предмет из рук",
                player,
                donationAmount,
                true,
                donationExecutor.mainConfig)
        } else {
            announce(donationUsername, "выбил у тебя предмет из рук", "выбил предмет из рук", player, donationAmount, true, donationExecutor.mainConfig)
            player.dropItem(true)
            player.updateInventory()
        }
    }

    private fun lesch(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе леща", "дал леща", player, donationAmount, true, donationExecutor.mainConfig)
        val direction = player.location.direction
        direction.setY(0)
        direction.normalize()
        direction.y = 0.3
        player.velocity = direction.multiply(0.8)
        if (player.health > 2.0) {
            player.health = player.health - 2
        } else {
            player.health = 0.0
        }
        player.playSound(player.location, Sound.ENTITY_PLAYER_HURT, 1f, 1f)
    }

    private fun powerKick(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе смачного пинка под зад", "дал смачного пинка под зад", player, donationAmount, true, donationExecutor.mainConfig)
        val direction = player.location.direction
        direction.setY(0)
        direction.normalize()
        direction.y = 0.5
        player.velocity = direction.multiply(1.66)
        if (player.health > 3.0) {
            player.health = player.health - 3
        } else {
            player.health = 0.0
        }
        player.playSound(player.location, Sound.ENTITY_PLAYER_HURT, 1f, 1f)
    }

    private fun clearLastDeathDrop(player: Player, donationUsername: String, donationAmount: String) {
        //Remove Last Death Dropped Items
        if (donationExecutor.streamerPlayersManager.getStreamerPlayer(player.name)!!.removeDeathDrop()) {
            announce(donationUsername, "уничтожил твой посмертный дроп", "уничтожил посмертный дроп", player, donationAmount, true, donationExecutor.mainConfig)
        } else {
            announce(donationUsername, "безуспешно пытался уничтожить твой посмертный дроп...", "безуспешно пытался уничтожить посмертный дроп", player,
                donationAmount, true, donationExecutor.mainConfig)
        }
    }

    private fun spawnCreeper(player: Player, donationUsername: String, donationAmount: String) {
        //Spawn Creepers
        val direction = player.location.direction
        announce(donationUsername, "прислал тебе в подарок крипера", "прислал крипера в подарок", player, donationAmount, true, donationExecutor.mainConfig)
        direction.setY(0)
        direction.normalize()
        player.world.spawnEntity(player.location.clone().subtract(direction.multiply(1)), EntityType.CREEPER)
    }

    private fun giveDiamonds(player: Player, donationUsername: String, donationAmount: String) {
        //Give some diamonds to the player
        announce(donationUsername, "насыпал тебе §bАЛМАЗОВ", "насыпал §bАлмазов§f", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.DIAMOND, donationExecutor.mainConfig.diamondsAmount, donationUsername, "§bАлмазы")
    }

    private fun giveStackOfDiamonds(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "насыпал тебе КУЧУ §bАЛМАЗОВ!", "насыпал §bАлмазов§f", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.DIAMOND, 64, donationUsername, "§bАлмазы")
    }

    private fun giveBread(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе §6Советского Хлеба", "дал §6Советского §6Хлеба§f", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.BREAD, donationExecutor.mainConfig.breadAmount, donationUsername, "§6Советский Хлеб")
    }

    private fun randomChange(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "подменил тебе кое-что на камни", "призвал Сталина разобраться с", player, donationAmount, true, donationExecutor.mainConfig)
        val randoms = IntArray(5)
        for (i in 0..4) {
            var temp = 0
            var isUnique = false
            while (!isUnique) {
                temp = Math.round(Math.random() * 35).toInt()
                isUnique = true
                var n: Int
                n = 0
                while (n < i) {
                    if (randoms[n] == temp) {
                        isUnique = false
                        break
                    }
                    n++
                }
            }
            randoms[i] = temp
        }
        val replacedItems = StringBuilder()
        var replacedCounter = 0
        for (i in 0..4) {
            if (player.inventory.getItem(randoms[i]) != null) {
                replacedCounter++
                if (replacedCounter > 1) {
                    replacedItems.append("§f, ")
                }
                replacedItems.append("§b").append(Objects.requireNonNull(player.inventory.getItem(randoms[i]))!!.amount).append(" §f")
                    .append(Objects.requireNonNull(player.inventory.getItem(
                        randoms[i]))!!.i18NDisplayName)
            }
            player.inventory.setItem(randoms[i], ItemStack(Material.STONE, 1))
        }
        if (replacedCounter == 0) {
            sendSysMsgToPlayer(player, "§cТебе повезло: все камни попали в пустые слоты!")
        } else {
            sendSysMsgToPlayer(player, "§cБыли заменены следующие предметусы: §f$replacedItems")
        }
    }

    private fun halfHeart(player: Player, donationUsername: String, donationAmount: String) {
        player.health = 1.0
        announce(donationUsername, "оставил тебе лишь полсердечка", "оставил лишь полсердечка", player, donationAmount, true, donationExecutor.mainConfig)
    }

    private fun tamedBecomesEnemies(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername,
            "настроил твоих питомцев против тебя",
            "настроил прирученных питомцев против",
            player,
            donationAmount,
            true,
            donationExecutor.mainConfig)

        player.world.getEntitiesByClasses(Wolf::class.java, Cat::class.java)
            .asSequence()
            .filterNotNull()
            .filterIsInstance(Tameable::class.java)
            .filter { it.owner?.name == player.name }
            .forEach { e ->
                e.owner = null
                e.target = player
                when (e) {
                    is Cat -> {
                        e.owner = null
                        player.sendMessage("+")
                    }
                    is Wolf -> e.isSitting = false
                }
            }
    }

    private fun bigBoom(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "сейчас тебя РАЗНЕСЕТ В КЛОЧЬЯ", "сейчас РАЗНЕСЕТ В КЛОЧЬЯ", player, donationAmount, true, donationExecutor.mainConfig)
        player.world.createExplosion(player.location, donationExecutor.mainConfig.bigBoomRadius.toFloat(), true)
    }

    private fun setNight(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "включил на сервере ночь", "включил ночь ради", player, donationAmount, true, donationExecutor.mainConfig)
        player.world.time = 18000
    }

    private fun setDay(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "включил на сервере день", "включил день ради", player, donationAmount, true, donationExecutor.mainConfig)
        player.world.time = 6000
    }

    private fun giveIronSet(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе железную броню", "дал железную броню", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.IRON_HELMET, 1, donationUsername)
        giveToPlayer(player, Material.IRON_BOOTS, 1, donationUsername)
        giveToPlayer(player, Material.IRON_CHESTPLATE, 1, donationUsername)
        giveToPlayer(player, Material.IRON_LEGGINGS, 1, donationUsername)
    }

    private fun giveIronSword(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе железный меч", "дал железный меч", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.IRON_SWORD, 1, donationUsername)
    }

    private fun giveIronKirka(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе железную кирку", "дал железную кирку", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.IRON_PICKAXE, 1, donationUsername)
    }

    private fun giveDiamondKirka(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе алмазную кирку", "дал алмазную кирку", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.DIAMOND_PICKAXE, 1, donationUsername)
    }

    private fun takeOffBlock(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "убрал блок у тебя из-пол ног", "убрал блок из-под ног", player, donationAmount, true, donationExecutor.mainConfig)
        player.world.getBlockAt(player.location.clone().subtract(0.0, 1.0, 0.0)).type = Material.AIR
        player.world.getBlockAt(player.location.clone().subtract(1.0, 1.0, 0.0)).type = Material.AIR
        player.world.getBlockAt(player.location.clone().subtract(0.0, 1.0, 1.0)).type = Material.AIR
        player.world.getBlockAt(player.location.clone().subtract(-1.0, 1.0, 0.0)).type = Material.AIR
        player.world.getBlockAt(player.location.clone().subtract(0.0, 1.0, -1.0)).type = Material.AIR
    }

    private fun giveDiamondSet(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе алмазную броню", "дал алмазную броню", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.DIAMOND_HELMET, 1, donationUsername)
        giveToPlayer(player, Material.DIAMOND_BOOTS, 1, donationUsername)
        giveToPlayer(player, Material.DIAMOND_CHESTPLATE, 1, donationUsername)
        giveToPlayer(player, Material.DIAMOND_LEGGINGS, 1, donationUsername)
    }

    private fun giveDiamondSword(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "дал тебе алмазный меч", "дал алмазный меч", player, donationAmount, true, donationExecutor.mainConfig)
        giveToPlayer(player, Material.DIAMOND_SWORD, 1, donationUsername)
    }

    private fun spawnTamedDog(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "подарил тебе дружка", "подарил щенка", player, donationAmount, true, donationExecutor.mainConfig)
        (player.world.spawnEntity(player.location, EntityType.WOLF) as Wolf).apply {
            isTamed = true
            owner = player
            removeWhenFarAway = false
            setCustomName(donationUsername)
        }
    }

    private fun spawnTamedCat(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "подарил тебе котейку", "подарил котейку", player, donationAmount, true, donationExecutor.mainConfig)
        (player.world.spawnEntity(player.location, EntityType.CAT) as Cat).apply {
            isTamed = true
            owner = player
            removeWhenFarAway = false
            setCustomName(donationUsername)
        }
    }

    private fun healPlayer(player: Player, donationUsername: String, donationAmount: String) {
        announce(donationUsername, "полностью вас вылечил", "полностью вылечил", player, donationAmount, true, donationExecutor.mainConfig)
        player.health = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH))!!.value
    }
}