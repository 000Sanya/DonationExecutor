package igorlink.donationexecutor

import igorlink.donationexecutor.executionsstaff.ExecUtils.giveToPlayer
import igorlink.service.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.random.Random

data class ExecuteRequest(
    val player: Player,
    val donationUsername: String,
    val donationAmount: Float,
)

class Executor(val donationExecutor: DonationExecutor) {
    private val donationActions: MutableMap<String, DonationAction> = mutableMapOf()

    private fun MutableMap<String, DonationAction>.add(action: DonationAction) {
        put(action.executionName, action)
    }

    private inline fun onExecute(executionName: String, crossinline block: (ExecuteRequest) -> Unit) {
        donationActions.add(object : DonationAction(executionName) {
            override fun onAction(request: ExecuteRequest) = block(request)
        })
    }

    private fun ExecuteRequest.announce(
        subText: String,
        alterSubtext: String,
        bigAnnounce: Boolean,
    ) = announce(
        donationUsername, subText, alterSubtext, player, donationAmount, bigAnnounce, donationExecutor.mainConfig
    )

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
        onExecute("ZaOrdu", ::spawnAgresiveWolf)
        onExecute("DonateScreamer", ::donateScreamer)
    }

    fun doExecute(streamerName: String, donationUsername: String, fullDonationAmount: Float, executionName: String) {
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
            streamerPlayer.sendActionBar(Component.text("НИКНЕЙМ ДОНАТЕРА БЫЛ СКРЫТ"))
        }


        donationActions[executionName]?.onAction(ExecuteRequest(streamerPlayer, validDonationUsername, fullDonationAmount))
    }

    private fun dropActiveItem(request: ExecuteRequest) {
        if (request.player.equipment.itemInMainHand.type == Material.AIR) {
            request.announce(
                "безуспешно пытался выбить у тебя предмет из рук", "безуспешно пытался выбить предмет из рук", true
            )
        } else {
            request.announce(
                "выбил у тебя предмет из рук", "выбил предмет из рук", true
            )
            request.player.dropItem(true)
            request.player.updateInventory()
        }
    }

    private fun lesch(request: ExecuteRequest) {
        request.announce(
            "дал тебе леща", "дал леща", true
        )
        val direction = request.player.location.direction
        direction.setY(0)
        direction.normalize()
        direction.y = 0.3
        request.player.velocity = direction.multiply(0.8)

        request.player.health = max(0.0, request.player.health - 2)
        request.player.playSound(request.player.location, Sound.ENTITY_PLAYER_HURT, 1f, 1f)
    }

    private fun powerKick(request: ExecuteRequest) {
        request.announce(
            "дал тебе смачного пинка под зад", "дал смачного пинка под зад", true
        )
        val direction = request.player.location.direction
        direction.setY(0)
        direction.normalize()
        direction.y = 0.5
        request.player.velocity = direction.multiply(1.66)
        request.player.health = max(0.0, request.player.health - 3)
        request.player.playSound(request.player.location, Sound.ENTITY_PLAYER_HURT, 1f, 1f)
    }

    private fun clearLastDeathDrop(request: ExecuteRequest) {
        //Remove Last Death Dropped Items
        if (donationExecutor.streamerPlayersManager.getStreamerPlayer(request.player.name)!!.removeDeathDrop()) {
            request.announce(
                "уничтожил твой посмертный дроп", "уничтожил посмертный дроп", true
            )
        } else {
            request.announce(
                "безуспешно пытался уничтожить твой посмертный дроп...", "безуспешно пытался уничтожить посмертный дроп", true
            )
        }
    }

    private fun spawnCreeper(request: ExecuteRequest) {
        //Spawn Creepers
        val direction = request.player.location.direction
        request.announce(
            "прислал тебе в подарок крипера", "прислал крипера в подарок", true
        )
        direction.setY(0)
        direction.normalize()
        request.player.world.spawnEntity(request.player.location.clone().subtract(direction.multiply(1)), EntityType.CREEPER)
    }

    private fun giveDiamonds(request: ExecuteRequest) {
        //Give some diamonds to the player
        request.announce(
            "насыпал тебе §bАЛМАЗОВ", "насыпал §bАлмазов§f", true
        )
        giveToPlayer(request.player, Material.DIAMOND, donationExecutor.mainConfig.diamondsAmount, request.donationUsername, "§bАлмазы")
    }

    private fun giveStackOfDiamonds(request: ExecuteRequest) {
        request.announce(
            "насыпал тебе КУЧУ §bАЛМАЗОВ!", "насыпал §bАлмазов§f", true
        )
        giveToPlayer(request.player, Material.DIAMOND, 64, request.donationUsername, "§bАлмазы")
    }

    private fun giveBread(request: ExecuteRequest) {
        request.announce(
            "дал тебе §6Советского Хлеба", "дал §6Советского §6Хлеба§f", true
        )
        giveToPlayer(request.player, Material.BREAD, donationExecutor.mainConfig.breadAmount, request.donationUsername, "§6Советский Хлеб")
    }

    private fun randomChange(request: ExecuteRequest) {
        request.announce(
            "подменил тебе кое-что на камни", "призвал Сталина разобраться с", true
        )
        val randoms = (0 until 35).shuffled().takeLast(5)
        val replacedItems = StringBuilder()
        var replacedCounter = 0
        for (i in 0..4) {
            if (request.player.inventory.getItem(randoms[i]) != null) {
                replacedCounter++
                if (replacedCounter > 1) {
                    replacedItems.append("§f, ")
                }
                val item = request.player.inventory.getItem(randoms[i])
                if (item != null) {
                    replacedItems
                        .append("§b")
                        .append(item.amount).append(" §f")
                        .append(Component.translatable(item).toString())
                }

            }
            request.player.inventory.setItem(randoms[i], ItemStack(Material.STONE, 1))
        }
        if (replacedCounter == 0) {
            sendSysMsgToPlayer(request.player, "§cТебе повезло: все камни попали в пустые слоты!")
        } else {
            sendSysMsgToPlayer(request.player, "§cБыли заменены следующие предметусы: §f$replacedItems")
        }
    }

    private fun halfHeart(request: ExecuteRequest) {
        request.player.health = 1.0
        request.announce(
            "оставил тебе лишь полсердечка", "оставил лишь полсердечка", true
        )
    }

    private fun tamedBecomesEnemies(request: ExecuteRequest) {
        request.announce(
            "настроил твоих питомцев против тебя", "настроил прирученных питомцев против", true
        )

        request.player.world.getEntitiesByClasses(Wolf::class.java, Cat::class.java)
            .asSequence()
            .filterNotNull()
            .filterIsInstance(Tameable::class.java)
            .filter { it.owner?.name == request.player.name }
            .forEach { e ->
                e.owner = null
                e.target = request.player
                when (e) {
                    is Cat -> {
                        request.player.sendMessage("+")
                    }
                    is Wolf -> e.isSitting = false
                }
            }
    }

    private fun bigBoom(request: ExecuteRequest) {
        request.announce(
            "сейчас тебя РАЗНЕСЕТ В КЛОЧЬЯ", "сейчас РАЗНЕСЕТ В КЛОЧЬЯ", true
        )
        request.player.world.createExplosion(request.player.location, donationExecutor.mainConfig.bigBoomRadius.toFloat(), true)
    }

    private fun setNight(request: ExecuteRequest) {
        request.announce(
            "включил на сервере ночь", "включил ночь ради", true
        )
        request.player.world.time = 18000
    }

    private fun setDay(request: ExecuteRequest) {
        request.announce(
            "включил на сервере день", "включил день ради", true
        )
        request.player.world.time = 6000
    }

    private fun giveIronSet(request: ExecuteRequest) {
        request.announce(
            "дал тебе железную броню", "дал железную броню", true
        )
        giveToPlayer(request.player, Material.IRON_HELMET, 1, request.donationUsername)
        giveToPlayer(request.player, Material.IRON_BOOTS, 1, request.donationUsername)
        giveToPlayer(request.player, Material.IRON_CHESTPLATE, 1, request.donationUsername)
        giveToPlayer(request.player, Material.IRON_LEGGINGS, 1, request.donationUsername)
    }

    private fun giveIronSword(request: ExecuteRequest) {
        request.announce(
            "дал тебе железный меч", "дал железный меч", true
        )
        giveToPlayer(request.player, Material.IRON_SWORD, 1, request.donationUsername)
    }

    private fun giveIronKirka(request: ExecuteRequest) {
        request.announce(
            "дал тебе железную кирку", "дал железную кирку", true
        )
        giveToPlayer(request.player, Material.IRON_PICKAXE, 1, request.donationUsername)
    }

    private fun giveDiamondKirka(request: ExecuteRequest) {
        request.announce(
            "дал тебе алмазную кирку", "дал алмазную кирку", true
        )
        giveToPlayer(request.player, Material.DIAMOND_PICKAXE, 1, request.donationUsername)
    }

    private fun takeOffBlock(request: ExecuteRequest) {
        request.announce(
            "убрал блок у тебя из-пол ног", "убрал блок из-под ног", true
        )
        request.player.world.getBlockAt(request.player.location.clone().subtract(0.0, 1.0, 0.0)).type = Material.AIR
        request.player.world.getBlockAt(request.player.location.clone().subtract(1.0, 1.0, 0.0)).type = Material.AIR
        request.player.world.getBlockAt(request.player.location.clone().subtract(0.0, 1.0, 1.0)).type = Material.AIR
        request.player.world.getBlockAt(request.player.location.clone().subtract(-1.0, 1.0, 0.0)).type = Material.AIR
        request.player.world.getBlockAt(request.player.location.clone().subtract(0.0, 1.0, -1.0)).type = Material.AIR
    }

    private fun giveDiamondSet(request: ExecuteRequest) {
        request.announce(
            "дал тебе алмазную броню", "дал алмазную броню", true
        )
        giveToPlayer(request.player, Material.DIAMOND_HELMET, 1, request.donationUsername)
        giveToPlayer(request.player, Material.DIAMOND_BOOTS, 1, request.donationUsername)
        giveToPlayer(request.player, Material.DIAMOND_CHESTPLATE, 1, request.donationUsername)
        giveToPlayer(request.player, Material.DIAMOND_LEGGINGS, 1, request.donationUsername)
    }

    private fun giveDiamondSword(request: ExecuteRequest) {
        request.announce(
            "дал тебе алмазный меч", "дал алмазный меч", true
        )
        giveToPlayer(request.player, Material.DIAMOND_SWORD, 1, request.donationUsername)
    }

    private fun spawnTamedDog(request: ExecuteRequest) {
        request.announce(
            "подарил тебе дружка", "подарил щенка", true
        )
        (request.player.world.spawnEntity(request.player.location, EntityType.WOLF) as Wolf).apply {
            isTamed = true
            owner = request.player
            removeWhenFarAway = false
            customName(Component.text(request.donationUsername))
        }
    }

    private fun spawnTamedCat(request: ExecuteRequest) {
        request.announce(
            "подарил тебе котейку", "подарил котейку", true
        )
        (request.player.world.spawnEntity(request.player.location, EntityType.CAT) as Cat).apply {
            isTamed = true
            owner = request.player
            removeWhenFarAway = false
            customName(Component.text(request.donationUsername))
        }
    }

    private fun healPlayer(request: ExecuteRequest) {
        request.announce(
            "полностью вас вылечил", "полностью вылечил", true
        )
        request.player.health = request.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
    }

    private fun spawnAgresiveWolf(request: ExecuteRequest) {
        request.announce(
            "ЗА ОРДУ!", "ЛОК'ТАР ОГАР!", true
        )
        (0 until Random.nextInt(10, 20)).forEach {
            (request.player.world.spawnEntity(request.player.location, EntityType.WOLF) as Wolf).apply {
                target = request.player
                getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 20.0
                customName(Component.text(request.donationUsername))
            }
        }
    }

    private fun donateScreamer(request: ExecuteRequest) {
        donationExecutor.runTaskLater(Random.nextLong(10 * 20, 50 * 20)) {
            request.announce(
                "отправил страшилку:3", "отправил страшилку:3", true
            )
        }
    }
}