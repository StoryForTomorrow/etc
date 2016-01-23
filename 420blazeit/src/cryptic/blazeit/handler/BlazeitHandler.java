package cryptic.blazeit.handler;

import java.util.HashMap;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import cryptic.network.CrypticMain;
import cryptic.network.cmdframework.Command;
import cryptic.network.cmdframework.CommandArgs;
import cryptic.network.cmdframework.CommandListener;
import cryptic.network.util.CUtil;
import cryptic.network.util.Colorize.Stylize;

public class BlazeitHandler implements Listener, CommandListener
{
	public static HashMap<String, Integer> level = new HashMap<String, Integer>();

	@Getter @Setter public static Boolean blazeit = true;
	
	// HashMap<String, Location> spawn = new HashMap<String, Location>();

	@EventHandler
	public void death(PlayerDeathEvent event)
	{
		Bukkit.broadcastMessage(Boolean.toString(getBlazeit()));

		if (!getBlazeit()) return;

		if (!event.getEntity().isDead()) return;

		if (event.getEntity() == null && !(event.getEntity() instanceof Player)) return;

		Player player = event.getEntity();

		if (player.getKiller() == null
				&& !(player.getKiller() instanceof Player)) return;

		Player killer = player.getKiller();

		if (!level.containsKey(killer.getName()))
		{
			level.put(killer.getName(), 1);
			setLevel(killer, level.get(killer.getName()));
		}
		else
		{
			level.put(killer.getName(), level.get(killer.getName()) + 1);
			setLevel(killer, level.get(killer.getName()));
		}

		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(CUtil.f("<red><bold>[BLAZEIT] LEVELS"));

		for (Player online : Bukkit.getOnlinePlayers())
		{
			@SuppressWarnings("deprecation")
			Score score = objective.getScore(online);
			if (level.containsKey(online.getName())) score.setScore(level
					.get(online.getName()));
			else score.setScore(0);

		}

		for (Player online : Bukkit.getOnlinePlayers())
		{
			online.setScoreboard(board);
		}

		// NO killer.setHealth(20.0F);

		// for (Player p : Bukkit.getOnlinePlayers())
		// if (this.spawn.containsKey(p.getName()))
		// p.teleport(this.spawn.get(p.getName()));
		// if (!level.containsKey(player.getName()))
		// {
		// level.put(player.getName(), 1);
		// setLevel(player, level.get(player.getName()));
		// }
		// else
		// {
		// level.put(player.getName(), level.get(player.getName()) - 1);
		// setLevel(player, level.get(player.getName()));
		// }

	}

	public void setLevel(Player player, int level)
	{
		player.getInventory().clear();

		if (level <= 0) return;

		if (level == 11)
		{
			endGame(player, player.getKiller() != null ? player.getKiller()
					: null);
			return;
		}

		ItemStack GOLD_SHARP = new ItemStack(Material.GOLD_AXE, 1);
		GOLD_SHARP.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		ItemStack STONE_SHARP = new ItemStack(Material.STONE_AXE, 1);
		STONE_SHARP.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		ItemStack IRON_SHARP = new ItemStack(Material.IRON_AXE, 1);
		IRON_SHARP.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
		ItemStack DIAMOND_SHARP = new ItemStack(Material.DIAMOND_AXE, 1);
		DIAMOND_SHARP.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

		ItemStack FLINT = new ItemStack(Material.FLINT_AND_STEEL, 1, (short) 2);
		FLINT.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

		ItemStack FLINT2 = new ItemStack(Material.FLINT_AND_STEEL, 1, (short) 2);
		FLINT2.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

		ItemStack BOW_POWER = new ItemStack(Material.BOW, 1);
		BOW_POWER.addEnchantment(Enchantment.ARROW_DAMAGE, 1);

		switch (level)
		{
		case 0:
			player.getInventory().clear();
			break;
		case 1:
			player.setItemInHand(new ItemStack(Material.GOLD_AXE));
			break;
		case 2:
			player.setItemInHand(GOLD_SHARP);
			break;
		case 3:
			player.setItemInHand(new ItemStack(Material.STONE_AXE));
			break;
		case 4:
			player.setItemInHand(STONE_SHARP);
			break;
		case 5:
			player.setItemInHand(new ItemStack(Material.IRON_AXE));
			break;
		case 6:
			player.setItemInHand(IRON_SHARP);
			break;
		case 7:
			player.setItemInHand(new ItemStack(Material.DIAMOND_AXE));
			break;
		case 8:
			player.setItemInHand(DIAMOND_SHARP);
			break;
		case 9:
			player.getInventory().addItem(
					new ItemStack[]
					{ new ItemStack(Material.BOW, 1),
							new ItemStack(Material.ARROW, 64), FLINT });
			break;
		case 10:
			player.getInventory().addItem(
					new ItemStack[]
					{ BOW_POWER, new ItemStack(Material.ARROW, 64), FLINT2,
							new ItemStack(Material.MONSTER_EGG) });
			break;
		default:
			player.getInventory().clear();
			break;
		}

		CUtil.send(
				player,
				"<green><magic>cc <reset><green><bold>Level up: <gold><bold>%d<green><bold>! <magic>cc",
				level);
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
	}

	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getNewScoreboard();

	Objective objective = board.registerNewObjective("lvl", "dummy");

	public void endGame(final Player winner, Player loser)
	{
		if (loser != null) Bukkit.broadcastMessage(CUtil.f(
				"<red><bold>[BLAZEIT] %s HAS WON THE GAME, GET R3KT %s!!1!1",
				winner.getName(), loser.getName()));
		else Bukkit.broadcastMessage(CUtil.f(
				"<red><bold>[BLAZEIT] %s HAS WON THE GAME!1!!1",
				winner.getName()));

		for (Player p : Bukkit.getOnlinePlayers())
		{
			p.getInventory().clear();
			p.setGameMode(GameMode.SPECTATOR);
		}
		setBlazeit(false);

		BukkitRunnable fireworks = new BukkitRunnable()
		{

			public void run()
			{
				// Spawn the Firework, get the FireworkMeta.
				Firework fw = (Firework) winner.getWorld().spawnEntity(
						winner.getLocation(), EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();

				// Our random generator
				Random r = new Random();

				// Get the type
				int rt = r.nextInt(4) + 1;
				Type type = Type.BALL;
				if (rt == 1) type = Type.BALL;
				if (rt == 2) type = Type.BALL_LARGE;
				if (rt == 3) type = Type.BURST;
				if (rt == 4) type = Type.CREEPER;
				if (rt == 5) type = Type.STAR;

				// Get our random colours
				int r1i = r.nextInt(17) + 1;
				int r2i = r.nextInt(17) + 1;
				Color c1 = getColor(r1i);
				Color c2 = getColor(r2i);

				// Create our effect with this
				FireworkEffect effect = FireworkEffect.builder()
						.flicker(r.nextBoolean()).withColor(c1).withFade(c2)
						.with(type).trail(r.nextBoolean()).build();

				// Then apply the effect to the meta
				fwm.addEffect(effect);

				// Generate some random power and set it
				int rp = r.nextInt(2) + 1;
				fwm.setPower(rp);

				// Then apply this to our rocket
				fw.setFireworkMeta(fwm);
			}

		};

		final BukkitTask taskname = fireworks.runTaskTimer(CrypticMain.get(),
				20, 20);
		CrypticMain.get().getServer().getScheduler()
				.scheduleSyncDelayedTask(CrypticMain.get(), new Runnable()
				{

					public void run()
					{
						taskname.cancel();
					}

				}, 5 * 20);
	}

	private Color getColor(int i)
	{
		Color c = null;
		if (i == 1)
		{
			c = Color.AQUA;
		}
		if (i == 2)
		{
			c = Color.BLACK;
		}
		if (i == 3)
		{
			c = Color.BLUE;
		}
		if (i == 4)
		{
			c = Color.FUCHSIA;
		}
		if (i == 5)
		{
			c = Color.GRAY;
		}
		if (i == 6)
		{
			c = Color.GREEN;
		}
		if (i == 7)
		{
			c = Color.LIME;
		}
		if (i == 8)
		{
			c = Color.MAROON;
		}
		if (i == 9)
		{
			c = Color.NAVY;
		}
		if (i == 10)
		{
			c = Color.OLIVE;
		}
		if (i == 11)
		{
			c = Color.ORANGE;
		}
		if (i == 12)
		{
			c = Color.PURPLE;
		}
		if (i == 13)
		{
			c = Color.RED;
		}
		if (i == 14)
		{
			c = Color.SILVER;
		}
		if (i == 15)
		{
			c = Color.TEAL;
		}
		if (i == 16)
		{
			c = Color.WHITE;
		}
		if (i == 17)
		{
			c = Color.YELLOW;
		}

		return c;
	}

	// Location[] locations = new Location[]
	// { new Location(Bukkit.getWorld("world"), -312.5, 68.5, -279.5),
	// new Location(Bukkit.getWorld("world"), -273.5, 68.5, -318.5) };

	@Command(name = "blazeit")
	public void blazeit(CommandArgs info)
	{
		if (!getBlazeit())
		{
			setBlazeit(true);
			// int i = 0;

			for (Player p : Bukkit.getOnlinePlayers())
			{
				BlazeitHandler.level.put(p.getName(), 0);
				p.setGameMode(GameMode.SURVIVAL);
				p.getInventory().clear();
				for (PotionEffect effect : p.getActivePotionEffects())
					p.removePotionEffect(effect.getType());
				// this.spawn.put(p.getName(), locations[i]);
				// i++;
				//
				// if (this.spawn.containsKey(p.getName()))
				// p.teleport(this.spawn.get(p.getName()));
			}
			Stylize msg = new Stylize("GAME STARTED!!1!1");

			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			objective.setDisplayName(CUtil
					.f("<red><bold>[BLAZEIT] LEVELS"));

			for (Player online : Bukkit.getOnlinePlayers())
			{
				@SuppressWarnings("deprecation")
				Score score = objective.getScore(online);
				if (level.containsKey(online.getName())) score.setScore(level
						.get(online.getName()));
				else score.setScore(0);

			}

			for (Player online : Bukkit.getOnlinePlayers())
			{
				online.setScoreboard(board);
			}

			Bukkit.broadcastMessage(CUtil.f("<red><bold>[BLAZEIT] %s",
					msg.toStripe(ChatColor.GREEN, ChatColor.DARK_GREEN)));

			for (Player p : Bukkit.getOnlinePlayers())
				p.setItemInHand(new ItemStack(Material.WOOD_AXE));

		}
		else
		{
			Stylize msg = new Stylize("GAME ENDED!11!1!");
			Bukkit.broadcastMessage(CUtil.f("<red><bold>[BLAZEIT] %s",
					msg.toStripe(ChatColor.RED, ChatColor.DARK_RED)));
			setBlazeit(false);
		}
		
		Bukkit.broadcastMessage(Boolean.toString(getBlazeit()));

	}
}
