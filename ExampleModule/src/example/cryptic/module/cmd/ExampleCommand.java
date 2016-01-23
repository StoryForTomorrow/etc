package example.cryptic.module.cmd;

import cryptic.network.cmdframework.Command;
import cryptic.network.cmdframework.CommandArgs;
import cryptic.network.cmdframework.CommandListener;

public class ExampleCommand implements CommandListener {

	@Command(name = "example")
	public void example(CommandArgs info)
	{
		info.getSender().sendMessage("TODO");
	}
}
