/**
 * 
 */
package cryptic.network.game.cmdframework.cmds;

import cryptic.network.game.cmdframework.Command;
import cryptic.network.game.cmdframework.CommandArgs;
import cryptic.network.game.cmdframework.CommandListener;
import cryptic.network.game.util.CUtil;

/**
 * @author 598Johnn897
 *
 */
public class LennyFace implements CommandListener
{

	@Command(
			name = "lenny",
				description = "( ͡° ͜ʖ ͡°)",
				usage = "( ͡° ͜ʖ ͡°)",
				aliases =
				{ "lennyface" })
	public void lenny(final CommandArgs info)
	{
		CUtil.send(info.getSender(),
				"<gold>What are you doing tonight? ( ͡° ͜ʖ ͡°)");
	}
}
