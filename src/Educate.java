import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Educate extends DataBaseLinker {
	
	// Insert Data
	public static void InsertEducation() throws Exception {
		String url = "https://openapi.gg.go.kr/TninsttInstutM?KEY=2336d255cdc44074b5854ec9619784e6&pIndex=1&pSize=600";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("row");
		if (nList.getLength() == 0) {
			System.out.println("�˻� ����� �����ϴ�.");
		}

		String sigun_name;
		String sigun_code;
		String emd_name;
		String industry_type;
		String a_name;
		String ceo_name;
		String a_class;
		String address;
		String tel;
		String latitude;
		String longitude;
		String REFINE_ZIP_CD;
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				sigun_name = getTagValue("SIGUN_NM", eElement);
				sigun_code = getTagValue("SIGUN_CD", eElement);
				emd_name = getTagValue("EMD_NM", eElement);
				industry_type = getTagValue("INDUTYPE_DIV_NM", eElement);
				a_name = getTagValue("FACLT_NM", eElement);
				ceo_name = getTagValue("REPRSNTV_NM", eElement);
				a_class = getTagValue("CRSE_CLASS_NM", eElement);
				address = getTagValue("REFINE_LOTNO_ADDR", eElement);
				tel = getTagValue("TELNO", eElement);
				latitude = getTagValue("REFINE_WGS84_LAT", eElement);
				longitude = getTagValue("REFINE_WGS84_LOGT", eElement);
				REFINE_ZIP_CD = getTagValue("REFINE_ZIP_CD", eElement);
				
				s.executeUpdate("insert into Academy values ('" + sigun_name + "', '" + sigun_code + "', '" + emd_name
						+ "', '" + industry_type + "', '" + a_name + "', '" + ceo_name + "', '" + tel + "', '" + a_class
						+ "', '" + address + "', '" + latitude + "', '" + longitude + "', '" + REFINE_ZIP_CD + "');");
			}
		}
	}

	public static JSONArray showEducation(String sigun, String dong, String num) throws Exception {
		int count = 0;
		ResultSet res = null;
		res = s.executeQuery("select a_name, ceo_name, a_class, tel, address, latitude, longitude\n" + "from Academy\n"
				+ "where sigun_name = '" + sigun + "' and emd_name = '" + dong + "' LIMIT " + num + ";");

		JSONArray map = new JSONArray();
		JSONObject content = new JSONObject();
		
		while (res.next()) {
			count++;
			
			String a_name2 = res.getString(1);
			String ceo_name2 = res.getString(2);
			String a_class2 = res.getString(3);
			String tel2 = res.getString(4);
			String address2 = res.getString(5);
			String latitude2 = res.getString(6);
			String longitude2 = res.getString(7);
			
			if (a_name2 == null) a_name2 = "";
			if (ceo_name2 == null) ceo_name2 = "";
			if (a_class2 == null) a_class2 = "";
			if (tel2 == null) tel2 = "";
			if (address2 == null) address2 = "";
			if (latitude2 == null) latitude2 = "";
			if (longitude2 == null) longitude2 = "";

			
			content.put("a_name2", a_name2);
			content.put("ceo_name2", ceo_name2);
			content.put("a_class2", a_class2);
			content.put("tel2", tel2);
			content.put("address2", address2);
			content.put("latitude2", latitude2);
			content.put("longitude2", longitude2);
			
			map.put(content);
			System.out.println(
					count + " | " + a_name2 + " | " + ceo_name2 + " | " + a_class2 + " | " + tel2 + " | " + address2);
		}
		
		return map;
	}

	public static String getLatitude(ResultSet res, String a_name2) throws Exception {
		res = s.executeQuery("select latitude\n" + "from Academy\n" + "where a_name = '" + a_name2 + "limit 1");
		String latitude = null;
		while (res.next()) {
			String latitude1 = res.getString(1);
			latitude = latitude1;
		}
		System.out.println(latitude);
		return latitude;
	}

	public static String getLongitude(ResultSet res, String sigun, String dong, String num) throws Exception {
		res = s.executeQuery("select longitude\n" + "from Academy\n" + "where sigun_name = '" + sigun
				+ "' and emd_name = '" + dong + "' LIMIT " + num + ";");
		String longitude = null;
		while (res.next()) {
			String longitude1 = res.getString(1);
			longitude = longitude1;
		}
		System.out.println(longitude);
		return longitude;
	}

	public static String getTagValue(String tag, Element eElement) {
		try {

			NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
			Node nValue = (Node) nlList.item(0);
			if (nValue == null)
				return null;
			return nValue.getNodeValue();
		} catch (NullPointerException e) {
			return "null";
		}
	}
	
}
