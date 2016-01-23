/**
 * 
 */
package cryptic.network.lib;

import java.io.File;

import org.json.JSONObject;

/**
 * @author 598Johnn897
 *
 */
public class References
{
	public static String CRYPTIC_JSON_URL = "https://raw.githubusercontent.com/CrypticDev/CrypticAPI/master/cryptic.json";
	public static String CRYPTIC_JSON_FILE = "/cryptic.json";
	public static JSONObject CRYPTIC_JSON;

	public static String WORKING_DIRECTORY = new File(".").getAbsolutePath();

	public static String MODULE_DIRECTORY;
}
