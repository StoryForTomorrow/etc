/**
 * 
 */
package cryptic.network.music;

import java.io.File;

import cryptic.network.lib.References;
import cryptic.network.module.Module;
import cryptic.network.module.ModuleException;
import cryptic.network.module.ModuleInfo;

/**
 * @author 598Johnn897
 *
 */
@ModuleInfo(id = "music", name = "Music", version = "0.1-ALPHA")
public class Music extends Module
{
	public String MUSIC_DIRECTORY = References.WORKING_DIRECTORY + "/music";

	public Music() throws ModuleException
	{
		super();
	}

	@Override
	public void load()
	{
		if (!new File(MUSIC_DIRECTORY).exists()) new File(MUSIC_DIRECTORY)
				.mkdir();
	}

	@Override
	public void enable()
	{

	}

	@Override
	public void disable()
	{

	}

}
