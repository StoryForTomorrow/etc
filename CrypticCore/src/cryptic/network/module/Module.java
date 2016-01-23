/**
 * 
 */
package cryptic.network.module;

import cryptic.network.CrypticMain;

/**
 * @author 598Johnn897
 *
 */
public abstract class Module
{
	protected ModuleLogger logger = new ModuleLogger(this);
	protected CrypticMain plugin = CrypticMain.get();

	public Module() throws ModuleException
	{
		
	}

	public ModuleInfo getInfo() throws ModuleException
	{
		if (this.getClass().getAnnotation(ModuleInfo.class) == null)
		{
			throw new ModuleException(
					"ModuleInfo annotation is null in class: "
							+ this.getClass().getSimpleName() + "!");
		}
		else
		{
			return this.getClass().getAnnotation(ModuleInfo.class);
		}
	}

	public abstract void load();

	public abstract void enable();

	public abstract void disable();

}
