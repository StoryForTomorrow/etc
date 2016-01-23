/**
 * 
 */
package cryptic.chat;

import java.util.logging.Level;

import cryptic.network.module.Module;
import cryptic.network.module.ModuleException;
import cryptic.network.module.ModuleInfo;

/**
 * @author 598Johnn897
 *
 */
@ModuleInfo(id = "cryptic-chat", name = "ChatModule", version = "0.1")
public class ChatModule extends Module
{
	public ChatModule() throws ModuleException {
		super();
	}

	@Override
	public void load() {
		logger.log(Level.INFO, "lol");
	}

	@Override
	public void enable() {
		
	}

	@Override
	public void disable() {
		
	}

}
