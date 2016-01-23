/**
 * 
 */
package cryptic.network.cmdframework.cmds;

import cryptic.network.cmdframework.Command;
import cryptic.network.cmdframework.CommandArgs;
import cryptic.network.cmdframework.CommandListener;
import cryptic.network.util.CUtil;

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
