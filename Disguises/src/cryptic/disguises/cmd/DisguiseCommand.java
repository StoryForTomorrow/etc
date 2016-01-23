package cryptic.disguises.cmd;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cryptic.network.cmdframework.Command;
import cryptic.network.cmdframework.CommandArgs;
import cryptic.network.cmdframework.CommandListener;
import cryptic.network.disguise.Disguise;
import cryptic.network.disguise.EntityDisguise;
import cryptic.network.util.CUtil;

public class DisguiseCommand implements CommandListener
{
	@Command(name = "disguise")
	public void disguise(CommandArgs info) throws Exception
	{
		if (info.isPlayer())
		{
			EntityDisguise disguise = null;

			if (info.getArgs().length == 0)
			{
				CUtil.send(info.getSender(),
						"<red>You did not tell me which disguise to put you as!");
				return;
			}
			else
			{
				try
				{
					disguise = EntityDisguise.valueOf(info.getArgs(0)
							.toUpperCase());
				}
				catch (Exception e)
				{
					CUtil.send(info.getSender(),
							"<red>The disguise \"%s\" does not exist!",
							info.getArgs(0));
					return;
				}
			}

			if (disguise == null) CUtil.send(info.getSender(),
					"<red>Error has occurred while disguising!");
			
			Player player = info.getPlayer();
			for (Player p : Bukkit.getOnlinePlayers())
			{
				if (p.getName().equals(player.getName())) continue;

				new Disguise(player, disguise, player.getScoreboard()
						.getTeam(player.getName()).getPrefix()
						+ player.getName()
						+ player.getScoreboard().getTeam(player.getName())
								.getSuffix(), new ItemStack(
						Material.DIAMOND_AXE), null, null, null, null)
						.sendDisguise(p);
			}

			CUtil.sendop(Level.INFO, player, "<gold>Disguise set!");
		}
		else CUtil.send(info.getSender(),
				"<red>Only players can disguises themselves!");
	}
}
