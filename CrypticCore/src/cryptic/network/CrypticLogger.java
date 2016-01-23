/**
 * 
 */
package cryptic.network;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

/**
 * @author Johnn
 *
 */
public class CrypticLogger extends Logger
{
	private String name;

	/**
	 * 
	 * @param context
	 */
	public CrypticLogger(Plugin context)
	{
		super(context.getClass().getCanonicalName(), null);

		String prefix = context.getDescription().getPrefix();
		name = prefix != null ? new StringBuilder().append("[").append(prefix)
				.append("] ").toString() : "["
				+ context.getDescription().getName() + "] ";

		setParent(context.getServer().getLogger());
		setLevel(Level.ALL);
	}

	@Override
	public void log(LogRecord logRecord)
	{
		logRecord.setMessage(name + logRecord.getMessage());
		super.log(logRecord);
	}
}
