/*
 * Author: 598Johnn897
 * 
 * Date: Aug 3, 2014
 * Package: com.jkush321.autowalls.util
 *
 */
package cryptic.network.util;

import java.util.ArrayList;
import java.util.UUID;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * 
 * @since 0.0.1-SNAPSHOT
 * @author 598Johnn897
 */
public class TagUtil
{

	/**
	 * These are the players with either a prefix or a suffix in their
	 * nameplate. If they do not have a prefix or suffix they will not be in the
	 * list.
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * @author 598Johnn897
	 * 
	 * @see ArrayList
	 * @see UUID
	 */
	@Getter
	private static ArrayList<UUID> playersWithTags = new ArrayList<UUID>();

	/**
	 * These are the players with prefixes in their nametags. If they do not
	 * have a prefix their UUID will not be in this list.
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * @author 598Johnn897
	 * 
	 * @see ArrayList
	 * @see UUID
	 */
	@Getter
	private static ArrayList<UUID> playersWithPrefixes = new ArrayList<UUID>();

	/**
	 * Theses are the players with suffixes in their nametags. If they do not
	 * have a suffix their UUID will not be in this list.
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * @author 598Johnn897
	 * 
	 * @see ArrayList
	 * @see UUID
	 */
	@Getter
	private static ArrayList<UUID> playersWithSuffixes = new ArrayList<UUID>();

	/**
	 * This returns true or false if the players has a prefix or suffix in their
	 * nametag.
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * @author 598Johnn897
	 * 
	 * @see UUID
	 * @see ArrayList
	 * @see #playersWithPrefixes
	 * @see #playersWithSuffixes
	 * @see #playersWithTags
	 * 
	 * @return true if the players has a prefix or suffix in their nametag
	 */
	public static boolean playerHasTags(Player player)
	{
		if (playersWithSuffixes.contains(player.getUniqueId())
				|| playersWithPrefixes.contains(player.getUniqueId())
				|| playersWithTags.contains(player.getUniqueId()))
			return true;
		else
			return false;
	}

	/**
	 * This sets a player's prefix on his/her nameplate. This creates a team for
	 * the player using the player's username for the team name then adds the
	 * player to the team and sets the defined prefix. The prefix supports color
	 * codes but can also use a string defined color.
	 * <p>
	 * <b>Note: Prefix can only be 16 characters long! (Color codes count as
	 * two!)</b>
	 * <p>
	 * If the String given is over 16 characters in length, it will be shortened
	 * to 16 character. Again, color codes count as two characters. If prefix
	 * given is equal to null or is left blank it will not change the prefix.
	 * <p>
	 * Example: <code> <ul>   
	 * Player player = event.getPlayer(); <br>
	 * TagUtil.setTagPrefix(player, "Swagmaster"); <br>
	 * </ul> </code>
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * @see ColorUtil#formatColors(String)
	 * @see ChatColor#translateAlternateColorCodes(char, String)
	 * @see Team#setPrefix(String)
	 * 
	 * @author md_5 <br>
	 *         598Johnn897
	 * 
	 * @param player
	 *            The player to add the tag prefix to
	 * @param prefix
	 *            The prefix to set to player's nameplate
	 */
	public static void setTagPrefix(Player player, String prefix)
	{
		Team team = player.getScoreboard().getTeam(player.getName().toString());
		if (team == null)
			team = player.getScoreboard().registerNewTeam(
					player.getName().toString());

		if (prefix != null) 
		{
			// Format the colors with the util
			prefix = CUtil.formatColors(prefix);
			// Format the normal color codes
			prefix = ChatColor.translateAlternateColorCodes('&', prefix);
			// Replace all the underscores with spaces
			prefix = prefix.replaceAll("_", " ");
			// Cut it down to only 16 characters
			prefix = prefix.substring(0, Math.min(prefix.length(), 16));
			
			team.setPrefix(prefix);
		}
		
		team.addPlayer(player);

		playersWithTags.add(player.getUniqueId());

		playersWithPrefixes.add(player.getUniqueId());
	}

	/**
	 * This removes the prefix on a player's nameplate if they have a prefix. To
	 * remove the prefix, this tests if the player has a team registered with
	 * his/her UUID using {@link #setTagPrefix(Player, String)} then sets the
	 * prefix to null. If the team is null it does nothing.
	 * <p>
	 * Example: <br>
	 * <code> <ul>   
	 * Player player = event.getPlayer(); <br>
	 * TagUtil.removeTagPrefix(player); <br>
	 * </ul> </code>
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * 
	 * @see Team#setPrefix(String)
	 * 
	 * @author md_5 <br>
	 *         598Johnn897
	 * 
	 * @param player
	 *            The player to remove the nameplate prefix from
	 */
	public static void removeTagPrefix(Player player)
	{
		Team team = player.getScoreboard().getTeam(player.getName().toString());
		if (team != null)
		{
			team.setPrefix("");
		}

		if (playersWithPrefixes.contains(player.getUniqueId()))
			playersWithPrefixes.remove(player.getUniqueId());

		if (!playersWithSuffixes.contains(player.getUniqueId())
				&& playersWithTags.contains(player.getUniqueId()))
			playersWithTags.remove(player.getUniqueId());
	}

	/**
	 * This sets a player's suffix on his/her nameplate. This creates a team for
	 * the player using the player's UUID for the team name then adds the player
	 * to the team and sets the defined suffix. The suffix supports color codes
	 * but can also use a string defined color. If the player already has a team
	 * defined with his/her UUID then it will just change the suffix.
	 * <p>
	 * <b>Note: suffix can only be 16 characters long! (Color codes count as
	 * two!)</b>
	 * <p>
	 * If the String given is over 16 characters in length, it will be shortened
	 * to 16 character. Again, color codes count as two characters. If suffix
	 * given is equal to null or is left blank it will not change the suffix.
	 * <p>
	 * Example:<br>
	 * <code>
	 * <ul>   
	 *        Player player = event.getPlayer();<br>
	 *        TagUtil.setTagSuffix(player, "is Awesome");<br>
	 * </ul>
	 * </code>
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * 
	 * @see ColorUtil#formatColors(String)
	 * @see ChatColor#translateAlternateColorCodes(char, String)
	 * @see Team#setSuffix(String)
	 * 
	 * @author md_5 <br>
	 *         598Johnn897
	 * 
	 * @param player
	 *            The player to add the tag suffix to
	 * @param suffix
	 *            The suffix to set to player's nameplate
	 */
	public static void setTagSuffix(Player player, String suffix)
	{
		Team team = player.getScoreboard().getTeam(player.getName().toString());
		if (team == null)
			team = player.getScoreboard().registerNewTeam(
					player.getName().toString());

		if (suffix != null) 
		{
			// Format the colors with the util
			suffix = CUtil.formatColors(suffix);
			// Format the normal color codes
			suffix = ChatColor.translateAlternateColorCodes('&', suffix);
			// Replace all the underscores with spaces
			suffix = suffix.replaceAll("_", " ");
			// Cut it down to only 16 characters
			suffix = suffix.substring(0, Math.min(suffix.length(), 16));
			
			team.setSuffix(suffix);
		}
		
		team.addPlayer(player);

		playersWithTags.add(player.getUniqueId());

		playersWithSuffixes.add(player.getUniqueId());
	}

	/**
	 * This removes the suffix on a player's nameplate if they have a suffix. To
	 * remove the suffix, this tests if the player has a team registered with
	 * his/her UUID using {@link #setTagSuffix(Player, String)} then sets the
	 * suffix to null. If the team is null it does nothing.
	 * <p>
	 * Example:
	 * <ul>
	 * <code>
	 * Player player = event.getPlayer();<br>
	 * TagUtil.removeTagSuffix(player);<br>
	 * </ul>
	 * </code>
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * 
	 * @see Team#setSuffix(String)
	 * 
	 * @author md_5 <br>
	 *         598Johnn897
	 * 
	 * @param player
	 *            The player to remove the nameplate prefix from
	 */
	public static void removeTagSuffix(Player player)
	{
		Team team = player.getScoreboard().getTeam(player.getName().toString());
		if (team != null)
		{
			team.setSuffix("");
		}

		if (playersWithSuffixes.contains(player.getUniqueId()))
			playersWithSuffixes.remove(player.getUniqueId());

		if (!playersWithPrefixes.contains(player.getUniqueId())
				&& playersWithTags.contains(player.getUniqueId()))
			playersWithTags.remove(player.getUniqueId());
	}

	/**
	 * To remove all tags (prefix and suffix) from the defined player, this
	 * removes player from their unique team then unregisters the team for later
	 * use and to prevent conflict. If the player's team is null nothing will
	 * happen.
	 * <p>
	 * Example:
	 * <ul>
	 * <code>
	 * Player player = event.getPlayer();<br>
	 * TagUtil.removeAllTags(player)<br>
	 * </ul>
	 * </code>
	 * 
	 * @since 0.0.1-SNAPSHOT
	 * 
	 * @see Team#removePlayer(org.bukkit.OfflinePlayer)
	 * @see Team#unregister()
	 * 
	 * @author 598Johnn897
	 * 
	 * @param player
	 *            Player to remove all prefixes and suffixes from
	 */
	public static void removeAllTags(Player player)
	{
		Team team = player.getScoreboard().getTeam(player.getName().toString());
		if (team != null)
		{
			team.setPrefix("");
			team.setSuffix("");

			team.removePlayer(player);
			team.unregister();

			if (playersWithTags.contains(player.getUniqueId()))
				playersWithTags.remove(player.getUniqueId());

			if (playersWithPrefixes.contains(player.getUniqueId()))
				playersWithPrefixes.remove(player.getUniqueId());

			if (playersWithSuffixes.contains(player.getUniqueId()))
				playersWithSuffixes.remove(player.getUniqueId());
		}
	}
	
	/**
	 * TODO write javadoc
	 */
	public static void setPlayerColor(Player player, ChatColor color)
	{
		player.setDisplayName(color + player.getName());
	}
	
	/**
	 * TODO write javadoc
	 */
	public static String getPlayerColor(Player player)
	{
		return player.getDisplayName().substring(0, 2);
	}
	
	/**
	 * TODO write javadoc
	 */
	public static void resetPlayerColor(Player player)
	{
		player.setDisplayName(player.getName());
	}
	
	/**
	 * TODO write javadoc
	 */
	public static String getPlayerPrefix(Player player)
	{
		if (player.getScoreboard().getTeam(player.getName()) != null)
			return player.getScoreboard().getTeam(player.getName()).getPrefix();
		else
			return "";
	}
	
	/**
	 * TODO write javadoc
	 */
	public static String getPlayerSuffix(Player player)
	{
		if (player.getScoreboard().getTeam(player.getName()) != null)
			return player.getScoreboard().getTeam(player.getName()).getSuffix();
		else
			return "";	
	}
	
	/**
	 * TODO
	 */
	public static void refresh()
	{
		
	}

}