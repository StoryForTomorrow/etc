/**
 * 
 */
package cryptic.chat;

import static cryptic.network.util.CUtil.f;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cryptic.network.util.TagUtil;

/**
 * @author 598Johnn897
 *
 */
public class ChatHandler implements Listener
{

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		if (!TagUtil.getPlayerPrefix(player).equals(""))
		{
			event.setFormat(f("%s %s<reset>: %s",
					TagUtil.getPlayerPrefix(player).trim(), player.getName(),
					event.getMessage()));
		}
		else 
		{
			event.setFormat(f("<gray>%s: %s", player.getName(), event.getMessage()));
		}
	}
}
