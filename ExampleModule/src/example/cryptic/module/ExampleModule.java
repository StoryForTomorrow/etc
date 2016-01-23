/**
 * 
 */
package example.cryptic.module;

import java.util.logging.Level;

import cryptic.network.module.Module;
import cryptic.network.module.ModuleException;
import cryptic.network.module.ModuleInfo;

/**
 * @author 598Johnn897
 *
 */
@ModuleInfo(id = "example", name = "ExampleModule", version = "0.13.37")
public class ExampleModule extends Module
{

	public ExampleModule() throws ModuleException {
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
