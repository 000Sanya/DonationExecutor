package igorlink.donationexecutor;

import igorlink.donationexecutor.executionsstaff.ExecUtils;
import igorlink.service.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static igorlink.service.Utils.*;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static org.bukkit.Bukkit.getPlayerExact;

public class Executor {
    public static String nameOfStreamerPlayer;
    public static String nameOfSecondStreamerPlayer;
    public static List<String> executionsNamesList = new ArrayList<>(Arrays.asList("ShitToInventory", "Lesch", "DropActiveItem",
            "PowerKick", "ClearLastDeathDrop", "SpawnCreeper", "GiveDiamonds", "GiveStackOfDiamonds", "GiveBread",
            "CallNKVD", "CallStalin", "RandomChange", "TamedBecomesEnemies", "HalfHeart", "BigBoom", "Nekoglai", "SetNight", "SetDay", "GiveIronSet",
            "GiveIronSword", "GiveDiamondSet", "GiveDiamondSword", "SpawnTamedDog", "SpawnTamedCat", "HealPlayer", "GiveIronKirka", "GiveDiamondKirka",
            "KillStalins", "TakeOffBlock"));


    private List<DonationAction> donationActions = new ArrayList<>();

    public Executor(DonationExecutor donationExecutor) {

        donationActions.add(new DonationAction("Lesch") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе леща", "дал леща", player, donationAmount, true);
                Vector direction = player.getLocation().getDirection();
                direction.setY(0);
                direction.normalize();
                direction.setY(0.3);
                player.setVelocity(direction.multiply(0.8));
                if (player.getHealth()>2.0D) {
                    player.setHealth(player.getHealth()-2);
                } else {
                    player.setHealth(0);
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            }
        });

        donationActions.add(new DonationAction("DropActiveItem") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                if (player.getEquipment().getItemInMainHand().getType() == Material.AIR) {
                    announce(donationUsername, "безуспешно пытался выбить у тебя предмет из рук", "безуспешно пытался выбить предмет из рук", player, donationAmount, true);
                } else {
                    announce(donationUsername, "выбил у тебя предмет из рук", "выбил предмет из рук", player, donationAmount, true);
                    player.dropItem(true);
                    player.updateInventory();
                }
            }
        });


        donationActions.add(new DonationAction("PowerKick") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе смачного пинка под зад", "дал смачного пинка под зад", player, donationAmount, true);
                Vector direction = player.getLocation().getDirection();
                direction.setY(0);
                direction.normalize();
                direction.setY(0.5);
                player.setVelocity(direction.multiply(1.66));
                if (player.getHealth()>3.0D) {
                    player.setHealth(player.getHealth()-3);
                } else {
                    player.setHealth(0);
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            }
        });

        donationActions.add(new DonationAction("ClearLastDeathDrop") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                //Remove Last Death Dropped Items
                if (donationExecutor.streamerPlayersManager.getStreamerPlayer(player.getName()).removeDeathDrop()) {
                    announce(donationUsername, "уничтожил твой посмертный дроп", "уничтожил посмертный дроп", player, donationAmount, true);
                } else {
                    announce(donationUsername, "безуспешно пытался уничтожить твой посмертный дроп...", "безуспешно пытался уничтожить посмертный дроп", player, donationAmount, true);
                }
            }
        });

        donationActions.add(new DonationAction("SpawnCreeper") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                //Spawn Creepers
                Vector direction = player.getLocation().getDirection();
                announce(donationUsername, "прислал тебе в подарок крипера", "прислал крипера в подарок", player, donationAmount, true);
                direction.setY(0);
                direction.normalize();
                player.getWorld().spawnEntity(player.getLocation().clone().subtract(direction.multiply(1)), EntityType.CREEPER);
            }
        });

        donationActions.add(new DonationAction("GiveDiamonds") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "насыпал тебе §bАЛМАЗОВ", "насыпал §bАлмазов§f", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.DIAMOND, donationExecutor.getMainConfig().getDiamondsAmount(), donationUsername, "§bАлмазы");
            }
        });

        donationActions.add(new DonationAction("GiveStackOfDiamonds") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "насыпал тебе КУЧУ §bАЛМАЗОВ!", "насыпал §bАлмазов§f", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.DIAMOND, 64, donationUsername, "§bАлмазы");
            }
        });

        donationActions.add(new DonationAction("GiveBread") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе §6Советского Хлеба", "дал §6Советского §6Хлеба§f", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.BREAD, donationExecutor.getMainConfig().getBreadAmount(), donationUsername, "§6Советский Хлеб");
            }
        });

        donationActions.add(new DonationAction("RandomChange") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "подменил тебе кое-что на камни", "призвал Сталина разобраться с", player, donationAmount, true);
                int[] randoms = new int[5];
                for (int i = 0; i <= 4; i++) {

                    int temp = 0;
                    boolean isUnique = false;
                    while (!isUnique) {
                        temp = (int) (round(random() * 35));
                        isUnique = true;
                        int n;
                        for (n = 0; n < i; n++) {
                            if (randoms[n] == temp) {
                                isUnique = false;
                                break;
                            }
                        }
                    }
                    randoms[i] = temp;

                }

                StringBuilder replacedItems = new StringBuilder();
                int replacedCounter = 0;
                for (int i = 0; i <= 4; i++) {
                    if (!(player.getInventory().getItem(randoms[i]) == null)) {
                        replacedCounter++;
                        if (replacedCounter > 1) {
                            replacedItems.append("§f, ");
                        }
                        replacedItems.append("§b").append(Objects.requireNonNull(player.getInventory().getItem(randoms[i])).getAmount()).append(" §f").append(Objects.requireNonNull(player.getInventory().getItem(randoms[i])).getI18NDisplayName());
                    }
                    player.getInventory().setItem(randoms[i], new ItemStack(Material.STONE, 1));
                }

                if (replacedCounter == 0) {
                    sendSysMsgToPlayer(player,"§cТебе повезло: все камни попали в пустые слоты!");
                } else {
                    sendSysMsgToPlayer(player,"§cБыли заменены следующие предметусы: §f" + replacedItems);
                }
            }
        });

        donationActions.add(new DonationAction("TamedBecomesEnemies") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "настроил твоих питомцев против тебя", "настроил прирученных питомцев против", player, donationAmount, true);
                for (Entity e : player.getWorld().getEntitiesByClasses(Wolf.class, Cat.class)) {
                    if (((Tameable) e).isTamed() && Objects.equals(Objects.requireNonNull(((Tameable) e).getOwner()).getName(), player.getName())) {
                        if (e instanceof Cat) {
                            ((Tameable) e).setOwner(null);
                            ((Cat) e).setSitting(false);
                            ((Cat) e).setTarget(player);
                            player.sendMessage("+");
                        } else {
                            ((Wolf) e).setSitting(false);
                            ((Tameable) e).setOwner(null);
                            ((Wolf) e).setTarget(player);
                        }
                    }
                }
            }
        });

        donationActions.add(new DonationAction("HalfHeart") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                player.setHealth(1);
                announce(donationUsername, "оставил тебе лишь полсердечка", "оставил лишь полсердечка", player, donationAmount, true);
            }
        });

        donationActions.add(new DonationAction("BigBoom") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "сейчас тебя РАЗНЕСЕТ В КЛОЧЬЯ", "сейчас РАЗНЕСЕТ В КЛОЧЬЯ", player, donationAmount, true);
                player.getWorld().createExplosion(player.getLocation(), donationExecutor.getMainConfig().getBigBoomRadius(), true);
            }
        });

        donationActions.add(new DonationAction("SetNight") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "включил на сервере ночь", "включил ночь ради", player, donationAmount, true);
                player.getWorld().setTime(18000);
            }
        });

        donationActions.add(new DonationAction("SetDay") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "включил на сервере день", "включил день ради", player, donationAmount, true);
                player.getWorld().setTime(6000);
            }
        });

        donationActions.add(new DonationAction("GiveIronSet") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе железную броню", "дал железную броню", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.IRON_HELMET, 1, donationUsername);
                ExecUtils.giveToPlayer(player, Material.IRON_BOOTS, 1, donationUsername);
                ExecUtils.giveToPlayer(player, Material.IRON_CHESTPLATE, 1, donationUsername);
                ExecUtils.giveToPlayer(player, Material.IRON_LEGGINGS, 1, donationUsername);
            }
        });

        donationActions.add(new DonationAction("GiveIronSword") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе железный меч", "дал железный меч", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.IRON_SWORD, 1, donationUsername);
            }
        });

        donationActions.add(new DonationAction("GiveIronKirka") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе железную кирку", "дал железную кирку", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.IRON_PICKAXE, 1, donationUsername);
            }
        });

        donationActions.add(new DonationAction("GiveDiamondKirka") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе алмазную кирку", "дал алмазную кирку", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.DIAMOND_PICKAXE, 1, donationUsername);
            }
        });

        donationActions.add(new DonationAction("TakeOffBlock") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "убрал блок у тебя из-пол ног", "убрал блок из-под ног", player, donationAmount, true);
                player.getWorld().getBlockAt(player.getLocation().clone().subtract(0,1,0)).setType(Material.AIR);
                player.getWorld().getBlockAt(player.getLocation().clone().subtract(1,1,0)).setType(Material.AIR);
                player.getWorld().getBlockAt(player.getLocation().clone().subtract(0,1,1)).setType(Material.AIR);
                player.getWorld().getBlockAt(player.getLocation().clone().subtract(-1,1,0)).setType(Material.AIR);
                player.getWorld().getBlockAt(player.getLocation().clone().subtract(0,1,-1)).setType(Material.AIR);
            }
        });

        donationActions.add(new DonationAction("GiveDiamondSet") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе алмазную броню", "дал алмазную броню", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.DIAMOND_HELMET, 1, donationUsername);
                ExecUtils.giveToPlayer(player, Material.DIAMOND_BOOTS, 1, donationUsername);
                ExecUtils.giveToPlayer(player, Material.DIAMOND_CHESTPLATE, 1, donationUsername);
                ExecUtils.giveToPlayer(player, Material.DIAMOND_LEGGINGS, 1, donationUsername);
            }
        });

        donationActions.add(new DonationAction("GiveDiamondSword") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "дал тебе алмазный меч", "дал алмазный меч", player, donationAmount, true);
                ExecUtils.giveToPlayer(player, Material.DIAMOND_SWORD, 1, donationUsername);
            }
        });

        donationActions.add(new DonationAction("SpawnTamedDog") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "подарил тебе дружка", "подарил щенка", player, donationAmount, true);
                Entity wolf = player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
                ((Wolf) wolf).setTamed(true);
                ((Wolf) wolf).setOwner(player);
                ((Wolf) wolf).setRemoveWhenFarAway(false);
                wolf.setCustomName(donationUsername);
            }
        });

        donationActions.add(new DonationAction("SpawnTamedCat") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "подарил тебе котейку", "подарил котейку", player, donationAmount, true);
                Entity cat = player.getWorld().spawnEntity(player.getLocation(), EntityType.CAT);
                ((Cat) cat).setTamed(true);
                ((Cat) cat).setOwner(player);
                ((Cat) cat).setRemoveWhenFarAway(false);
                cat.setCustomName(donationUsername);
            }
        });

        donationActions.add(new DonationAction("HealPlayer") {
            @Override
            public void onAction(Player player, String donationUsername, String donationAmount) {
                announce(donationUsername, "полностью вас вылечил", "полностью вылечил", player, donationAmount, true);
                player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            }
        });
    }


    public void doExecute(String streamerName, String donationUsername, String fullDonationAmount, String executionName) {

        Player streamerPlayer = getPlayerExact(streamerName);
        boolean canContinue = true;
        //Определяем игрока (если он оффлайн - не выполняем донат и пишем об этом в консоль), а также определяем мир, местоположение и направление игрока
        if (streamerPlayer == null) {
            canContinue = false;
        } else if (streamerPlayer.isDead()) {
            canContinue = false;
        }

        //Если имя донатера не указано - устанавливаем в качестве имени "Кто-то"
        String validDonationUsername;
        if (donationUsername.equals("")) {
            validDonationUsername = "Аноним";
        } else if (!isBlackListed(donationUsername)){
            validDonationUsername = donationUsername;
        } else {
            validDonationUsername = "Донатер";
            assert streamerPlayer != null;
            Utils.logToConsole("§eникнейм донатера §f" + donationUsername + "§e был скрыт, как подозрительный");
            streamerPlayer.sendActionBar("НИКНЕЙМ ДОНАТЕРА БЫЛ СКРЫТ");
        }


        if (!canContinue) {
            logToConsole("Донат от §b" + donationUsername + " §f в размере §b" + fullDonationAmount + "§f выполнен из-за того, что целевой стример был недоступен.");
            return;
        }

        var action = donationActions.stream().filter(a -> a.getExecutionName().equals(executionName)).findFirst();

        action.ifPresent(donationAction -> donationAction.onAction(streamerPlayer, validDonationUsername, fullDonationAmount));
    }

}
