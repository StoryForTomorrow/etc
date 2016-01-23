package cryptic.network.music.cmd;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.bukkit.Bukkit;

import cryptic.network.cmdframework.Command;
import cryptic.network.cmdframework.CommandArgs;
import cryptic.network.cmdframework.CommandListener;
import cryptic.network.lib.References;
import cryptic.network.midi.MidiUtil;
import cryptic.network.util.CUtil;

public class MusicCommand implements CommandListener
{
	@Command(name = "music")
	public void music(CommandArgs info)
	{
		if (info.getArgs().length > 0 && info.getArgs().length < 2)
		{
			File music = new File(References.WORKING_DIRECTORY + "/music/"
					+ info.getArgs(0) + ".mid");
			
			if (!music.exists())
			{ 
				CUtil.send(info.getSender(), "<red>Could not find song!", music.getName());
				return;
			}
			try
			{
				MidiUtil.playMidi(music, 1.0F,
						new HashSet<>(Bukkit.getOnlinePlayers()));
			}
			catch (InvalidMidiDataException | IOException
					| MidiUnavailableException e)
			{
				CUtil.send(info.getSender(), "<red>Could not find song!",
						music.getName());
				return;
			}
			finally
			{
				CUtil.send(info.getSender(),
						"<green>Now playing <gold>%s<green>!", music.getName());
			}
		}
		else
		{
			CUtil.send(info.getSender(),
					"<gold>You did not tell me which music to play!");
		}
	}

	@Command(name = "music.list")
	public void musiclist(CommandArgs info)
	{

	}
}
