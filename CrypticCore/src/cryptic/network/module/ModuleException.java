/**
 * 
 */
package cryptic.network.module;

/**
 * @author 598Johnn897
 *
 */
public class ModuleException extends Exception
{

	private static final long serialVersionUID = 2029691209227203330L;

	public ModuleException()
	{
		super("An error has occurred with one of the modules!");
	}

	public ModuleException(String message)
	{
		super(message);
	}

}
