import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataBaseLinker {
	static Connection conn = null;
	static Statement s = null;
	static PreparedStatement ps = null;
	static ResultSet r = null;

	static String getTagValue(String tag, Element eElement) {
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

	public static void startConnection() {
		try {
			System.out.println("Connecting PostgreSQL database");
			String pgurl = "jdbc:postgresql:postgres";
			String user = "postgres";
			String password = "5432";

			conn = DriverManager.getConnection(pgurl, user, password);
			s = conn.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createTables() {
		try {
			s.executeUpdate(
					"create table Location" + "(locationID int, locationName char(30), locX char(20), locY char(20), "
							+ "primary key(locationID))");

			s.executeUpdate("create table TransferRoute "
					+ "(trID int, pathID int, distance char(10), time char(4), routeID int, routeName char(10), "
					+ "fid int, fname char(30), fx char(20), fy char(20),"
					+ "tid int, tname char(30), tx char(20), ty char(20)," + "primary key(trID, pathID))");

			s.executeUpdate(
					"create table BusData (BusID int, BusNum varchar(20), bstopName varchar(20), busArrivalTime varchar(20))");

			s.executeUpdate("CREATE TABLE Academy" + "(sigun_name char(20), sigun_code char(20), emd_name char(20),"
					+ "industry_type char(20), a_name char(20), ceo_name char(20),"
					+ "tel char(20), a_class char(20), address char(100), "
					+ "latitude char(20), longitude char(20), REFINE_ZIP_CD char(20))");
		
			s.executeUpdate("create table AcademyStation (as_id char(20), as_name char(100), as_num char(20))");

		} catch (Exception e) {
			dropTables();
			createTables();
		}
	}

	static void dropTables() {
		try {
			s.executeUpdate("drop table TransferRoute");
			s.executeUpdate("drop table Location");
			s.executeUpdate("drop table BusData");
			s.executeUpdate("drop table Academy");
			s.executeUpdate("drop table AcademyStation");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteLocationTable() {
		try {
			s.executeUpdate("delete from Location");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteTransferRoutetTable() { // ��� ���̺� ���
		try {
			s.executeUpdate("delete from TransferRoute");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteBusDataTable() { // ��� ���̺� ���
		try {
			s.executeUpdate("delete from BusData");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void deleteAcademyTable() {
		try {
			s.executeUpdate("delete from Academy");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void endConnection() {
		try {
			// Close connection
			if (s != null) {
				s.close();
			}
			if (conn != null) {
				conn.close();
			}
			if (r != null) {
				r.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
