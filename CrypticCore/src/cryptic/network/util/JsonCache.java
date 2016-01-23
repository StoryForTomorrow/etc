/**
 * 
 */
package cryptic.network.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author 598Johnn897
 *
 */
public class JsonCache
{

	private File file = null;
	private JSONObject jsonfile = null;

	public JsonCache(File file, URL url, int minutes) throws IOException,
			JSONException
	{
		long cacheTime = System.currentTimeMillis() - (minutes * 60 * 1000);
		if (file.exists())
		{
			if (file.lastModified() < cacheTime)
			{
				file.delete();

				file.createNewFile();

				FileWriter fileInstance = new FileWriter(file.getAbsolutePath());

				JSONObject json = null;
				try
				{
					json = readJsonFromUrl(url.toString());
				}
				catch (JSONException e1)
				{
					e1.printStackTrace();
				}
				try
				{
					fileInstance.write(json.toString());
				}
				catch (IOException e)
				{
					e.printStackTrace();

				}
				finally
				{
					fileInstance.flush();
					fileInstance.close();
				}

				this.file = file;

			}
			else
			{
				this.file = file;
			}
		}
		else
		{
			file.createNewFile();

			FileWriter fileInstance = new FileWriter(file.getAbsolutePath());

			JSONObject json = null;
			try
			{
				json = readJsonFromUrl(url.toString());
			}
			catch (JSONException e1)
			{
				e1.printStackTrace();
			}
			try
			{
				fileInstance.write(json.toString());
			}
			catch (IOException e)
			{
				e.printStackTrace();

			}
			finally
			{
				fileInstance.flush();
				fileInstance.close();
			}

			this.file = file;
		}

		JSONParser parser = new JSONParser();
		try
		{
			org.json.simple.JSONObject obj = (org.json.simple.JSONObject) parser
					.parse(new FileReader(file));
			JSONObject jsonObj = new JSONObject(obj.toJSONString());
			this.jsonfile = jsonObj;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			jsonfile = new JSONObject().put("error", e.toString());
		}
	}

	public File get()
	{
		return file;
	}

	public JSONObject getJson()
	{
		return jsonfile;
	}

	private String readAll(Reader rd) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1)
		{
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException
	{
		InputStream is = new URL(url).openStream();
		try
		{
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		}
		finally
		{
			is.close();
		}
	}
}
