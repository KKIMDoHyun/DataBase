import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;

public class BusData extends DataBaseLinker {
	
	public void getArrTime() throws SQLException {
		String result = null;
		String busn = null;
		r = s.executeQuery("select * from BusData;");
		while (r.next()) {
			busn = r.getString("busnum");
			result = r.getString("busArrivalTime");

			if (result.equals("운행종료")) {
				System.out.println(busn + "(은)는 현재 " + result + "입니다.");
			} else if (result.equals("출발대기")) {
				System.out.println(busn + "(은)는 현재 " + result + "중 입니다.");
			}

			else {
				System.out.println(busn + "(은)는 " + result + "도착예정 입니다.");
			}
		}
	}

	public void getBusData(String num) throws IOException, SQLException {
		String[] routeID = new String[6];
		String[] fid = new String[6];
		String[] RouteName = new String[6];
		String pars1, pars2, pars3;
		int start, end;
		r = s.executeQuery("Select * from TransferRoute where trid = " + num + ";");
		int ind = 0, fidd = 0;
		while (r.next()) {
			ind++;
			routeID[ind] = r.getString("RouteID");
			fid[ind] = r.getString("fID");
			RouteName[ind] = r.getString("RouteName");
		}

		for (int i = 1; i <= ind; i++) {
			fidd = Integer.parseInt(fid[i]);
			if (fidd < 99999999) {
				continue;

			}
			StringBuilder urlBuilder = new StringBuilder(
					"http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute"); /* URL */
			urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8")
					+ "=%2BReBcRCfhmrTznYwzS8wg5PxEs7xqYl0KFkKGzxJr%2B2a690G%2Bm2IpTjqDI6BwC28%2FnP3C%2BnCR8nAp4oB1zskdQ%3D%3D"); /*
																																	 * Service
																																	 * Key
																																	 */
			urlBuilder.append(
					"&" + URLEncoder.encode("busRouteId", "UTF-8") + "=" + URLEncoder.encode(routeID[i], "UTF-8")); /**/
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

			String xml0 = sb.toString();
			String seq;

			start = xml0.indexOf("<headerMsg>") + 11;
			end = xml0.indexOf("</headerMsg>");
			seq = xml0.substring(start, end);
			if (seq.equals("결과가 없습니다.")) {
				System.out.println(seq + " 서울지역 버스만 조회 가능합니다.");
				continue;
			}
			int back = xml0.indexOf(fid[i]) - 27;
			start = xml0.indexOf("<seq>", back) + 5;
			end = xml0.indexOf("</seq>", back);
			seq = xml0.substring(start, end);

			urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute"); /* URL */
			urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8")
					+ "=%2BReBcRCfhmrTznYwzS8wg5PxEs7xqYl0KFkKGzxJr%2B2a690G%2Bm2IpTjqDI6BwC28%2FnP3C%2BnCR8nAp4oB1zskdQ%3D%3D"); /*
																																	 * Service
																																	 * Key
																																	 */
			urlBuilder.append("&" + URLEncoder.encode("stId", "UTF-8") + "=" + URLEncoder.encode(fid[i], "UTF-8")); /**/
			urlBuilder.append(
					"&" + URLEncoder.encode("busRouteId", "UTF-8") + "=" + URLEncoder.encode(routeID[i], "UTF-8")); /**/
			urlBuilder.append("&" + URLEncoder.encode("ord", "UTF-8") + "=" + URLEncoder.encode(seq, "UTF-8")); /**/
			url = new URL(urlBuilder.toString());
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");

			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			String xml = sb.toString();

			start = xml.indexOf("<rtNm>") + 6; // 버스 번호,
			end = xml.indexOf("</rtNm>");
			pars1 = xml.substring(start, end);
			// System.out.println("\n" + pars1);

			start = xml.indexOf("<stNm>") + 6; // 정류소 이름
			end = xml.indexOf("</stNm>");
			pars2 = xml.substring(start, end);
			// System.out.println("\n" + pars2);

			start = xml.indexOf("<arrmsg1>") + 9; // 도착 시간 메시지
			end = xml.indexOf("</arrmsg1>");
			pars3 = xml.substring(start, end);

			s.execute("insert into BusData values (" + routeID[i] + ", '" + pars1 + "', '" + pars2 + "', '" + pars3
					+ "'" + ");");

		}

		try {

			getArrTime();

		} catch (SQLException ex) {
			throw ex;

		}
	}
}