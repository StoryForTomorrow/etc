package de.janmm14.customskins.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

import de.janmm14.customskins.data.Proxy;
import de.janmm14.customskins.data.Skin;

import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Network {

	public static final JSONParser JSON_PARSER = new JSONParser();

	public static boolean downloadSkin(@NonNull String url, @NonNull File file) {
		try {
			if (!url.toLowerCase().startsWith("http")) {
				url = "http://" + url;
			}
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);
			RequestConfig.Builder builder = RequestConfig.copy(httpget.getConfig() == null ? RequestConfig.DEFAULT : httpget.getConfig());
			httpget.setConfig(builder.setRedirectsEnabled(true).setRelativeRedirectsAllowed(true).build());
			CloseableHttpResponse response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return false;
			}
			FileOutputStream fileOut = new FileOutputStream(file);
			InputStream input = entity.getContent();
			file.delete();
			file.createNewFile();
			IOUtils.copy(input, fileOut);
			fileOut.close();
			input.close();
			response.close();
			client.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Nullable
	public static Skin getSkin(@NonNull UUID uuid, @Nullable Proxy proxy, @NonNull String skinName) {
		Skin skin = null;
		if (proxy != null && proxy.isNoProxy()) {
			proxy = null;
		}
		try {
			CloseableHttpClient client;
			if (proxy != null && proxy.getUsername() != null && !proxy.getUsername().trim().isEmpty()) {
				CredentialsProvider proxyAuth = new BasicCredentialsProvider();
				proxyAuth.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()), new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
				client = HttpClients.custom().setDefaultCredentialsProvider(proxyAuth).build();
			} else {
				client = HttpClients.createDefault();
			}
			HttpGet post = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", "") + "?unsigned=false");
			if (proxy != null) {
				RequestConfig.Builder builder = RequestConfig.copy(post.getConfig() == null ? RequestConfig.DEFAULT : post.getConfig());
				post.setConfig(builder.setProxy(new HttpHost(proxy.getHost(), proxy.getPort())).build());
			}
			post.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String res = EntityUtils.toString(entity);
			JSONObject json = (JSONObject) JSON_PARSER.parse(res);
			JSONArray obj = (JSONArray) json.get("properties");
			JSONObject properties = (JSONObject) obj.get(0);
			String value = (String) properties.get("value");
			String signature = (String) properties.get("signature");
			skin = new Skin(skinName, value, signature);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return skin;
	}

	private static final String NAME_FETCHER_URL = "https://api.mojang.com/profiles/minecraft";
	private static final int MAX_NAMES_PER_REQUEST = 100;
	private static final double MAX_NAMES_PER_REQUEST_D = MAX_NAMES_PER_REQUEST;
	private static boolean applyRateLimit = true;

	public static Map<String, UUID> getUuidsOfNames(@NonNull Set<String> names) {
		// no duplicates possible because of provided collection is a set
		return getUuidsOfNames0(new ArrayList<>(names));
	}

	public static Map<String, UUID> getUuidsOfNames(@NonNull String... names) {
		return getUuidsOfNames(Arrays.asList(names));
	}

	public static Map<String, UUID> getUuidsOfNames(@NonNull List<String> names) {
		// ensure no duplicates
		return getUuidsOfNames0(new ArrayList<>(new HashSet<>(names)));
	}

	@SuppressWarnings("unchecked")
	private static Map<String, UUID> getUuidsOfNames0(@NonNull List<String> names) {
		Map<String, UUID> results = Maps.newHashMapWithExpectedSize(names.size());
		int requests = (int) Math.ceil(names.size() / MAX_NAMES_PER_REQUEST_D);
		for (int i = 0; i < requests; i++) {
			if (applyRateLimit && i != 0) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				HttpURLConnection connection = createConnection(NAME_FETCHER_URL, RequestMethod.POST);
				String body = JSONArray.toJSONString(names.subList(i * MAX_NAMES_PER_REQUEST, Math.min((i + 1) * MAX_NAMES_PER_REQUEST, names.size())));
				writePost(connection, body.getBytes()).close();
				Iterable<Map<String, String>> array = (Iterable<Map<String, String>>) JSON_PARSER.parse(new InputStreamReader(connection.getInputStream()));
				for (Map<String, String> profile : array) {
					String dashFreeUuid = profile.get("id");
					String name = profile.get("name");
					UUID uuid = UUID.fromString(Util.insertDashUUID(dashFreeUuid));
					results.put(name, uuid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	private static HttpURLConnection createConnection(@NonNull String url, @NonNull RequestMethod requestMethod) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod(requestMethod.name());
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("User-Agent", "minecraft");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

	private static OutputStream writePost(URLConnection connection, byte[] data) throws IOException {
		OutputStream stream = connection.getOutputStream();
		stream.write(data);
		stream.flush();
		return stream;
	}

	private enum RequestMethod {
		GET, POST
	}

}
