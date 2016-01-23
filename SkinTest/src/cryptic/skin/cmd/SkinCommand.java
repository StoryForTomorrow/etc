package cryptic.skin.cmd;

import cryptic.network.cmdframework.Command;
import cryptic.network.cmdframework.CommandArgs;
import cryptic.network.cmdframework.CommandListener;

public class SkinCommand implements CommandListener {

	@Command(name = "example")
	public void example(CommandArgs info)
	{
		info.getSender().sendMessage("TODO");
	}
}
