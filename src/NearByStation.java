import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class NearByStation extends DataBaseLinker {

	public static JSONObject loadNearByStation(String x, String y) throws Exception {
		StringBuilder urlBuilder = new StringBuilder(
				"http://openapi.gbis.go.kr/ws/rest/busstationservice/searcharound"); /* URL */

		urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8")
				+ "=HskpD5EkVS6dhetlfB7qU3r0C%2Fed%2FNUoLag28jRIl7Bg11b97fGoEq88Ir6nC2r4PnyNJlME%2F3vnh3Ifdv2CGg%3D%3D"); 
		
		urlBuilder
				.append("&" + URLEncoder.encode("x", "UTF-8") + "=" + URLEncoder.encode(x, "UTF-8")); /* X 좌표(WGS84) */
		urlBuilder
				.append("&" + URLEncoder.encode("y", "UTF-8") + "=" + URLEncoder.encode(y, "UTF-8")); /* X 좌표(WGS84) */

		URL url = new URL(urlBuilder.toString());

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

		BufferedReader rd;

		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}

		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}

		rd.close();
		conn.disconnect();

		JSONObject object = XML.toJSONObject(sb.toString());
		return object;
	}

	public static void print(JSONObject object) throws Exception {
		JSONArray array = object.getJSONObject("response").getJSONObject("msgBody")
				.getJSONArray("busStationAroundList");

		System.out.println("정류소 아이디 | 정류소명 | 정류소 번호");

		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			String stationId = obj.getString("stationId");
			String stationName = obj.getString("stationName");
			String mobileNo = null;

			if (obj.has("mobileNo"))
				mobileNo = obj.getString("mobileNo");

			System.out.println(stationId + " | " + stationName + " | " + mobileNo);
			s.executeUpdate("insert into AcademyStation values ('" + stationId + "', '" + stationName + "', '" + mobileNo + "');");
		}
	}
}
