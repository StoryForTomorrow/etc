package de.janmm14.customskins.proxyapis.com.yasakvar;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import de.janmm14.customskins.CustomSkins;
import de.janmm14.customskins.data.Proxy;
import de.janmm14.customskins.util.Network;

import com.google.common.collect.Sets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import lombok.NonNull;

public class YasakvarApi {

	private final CustomSkins plugin;

	public YasakvarApi(@NonNull CustomSkins plugin) {
		this.plugin = plugin;
	}

	/**
	 * consumes much time
	 */
	@NonNull
	public Set<Proxy> getProxies(@NonNull Set<String> countries) {
		CloseableHttpClient client = HttpClients.createDefault();
		Set<Proxy> set = Sets.newHashSetWithExpectedSize(countries.size() * 15);
		for (String country : countries) {
			country = country.toUpperCase(Locale.ENGLISH);
			HttpGet get = new HttpGet("http://www.yasakvar.com/apiv1/?type=json&proxyspeed=FAST&connectiontime=FAST&protocol=HTTPS&country=" + country);
			try {
				CloseableHttpResponse res = client.execute(get);
				if (res.getStatusLine().getStatusCode() != 200) {
					plugin.getLogger().warning("[YasakvarAPI] Could not get proxies of country " + country + ": Response code not 200");
					continue;
				}
				String jsonResponse = EntityUtils.toString(res.getEntity());
				JSONObject parsedResponse = (JSONObject) Network.JSON_PARSER.parse(jsonResponse);
				if (!Boolean.parseBoolean(String.valueOf(parsedResponse.get("success")))) {
					plugin.getLogger().warning("[YasakvarAPI] Could not get proxies of country " + country + ": " + parsedResponse.get("error"));
					continue;
				}
				JSONObject proxylist = (JSONObject) parsedResponse.get("proxylist");
				Collection<JSONObject> proxiesJson = proxylist.values();
				for (JSONObject proxyEntry : proxiesJson) {
					String ip = (String) proxyEntry.get("ip");
					Object portObj = proxyEntry.get("port");
					int port = portObj instanceof Integer ? (int) portObj : Integer.valueOf(String.valueOf(portObj));
					set.add(Proxy.create("yasakvar.com-" + ip, ip, port));
				}
			} catch (IOException | ParseException e) {
				plugin.getLogger().warning("[YasakvarAPI] Could not get proxies of country " + country + ". The following error occurred:");
				e.printStackTrace();
			}
		}
		plugin.getLogger().info("[YasakvarAPI] Loaded " + set.size() + " proxies from the api.");
		return set;
	}
}
