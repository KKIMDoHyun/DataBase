import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;;

public class mainInterface
{


	public static void main(String[] args) throws Exception
    {
		Location l = new Location();
		TransferRoute tr = new TransferRoute();
		BusData bus = new BusData();
	    String input;
	    String buffer;
	    
    	DataBaseLinker.startConnection();
 
        Scanner scan = new Scanner(System.in);
        
    	DataBaseLinker.createTables(); //createTable : 이미 table이 create된 상태라면 drop하고 create해줌
    	l.loadLocationList();
    	Educate.InsertEducation();

        int n = 1;
        while(n!=0) {
        	if(n==1) {
            	AcademyWithBus(scan);
            	n = 2;
        	}
        	if(n==2) {
            	searchTransferRouteInterface(l, tr, bus); 
        	}
            System.out.println("\n>> 0입력 : 종료 / 1입력 : 학원 다시 검색 / 2입력 : 경로 다시 검색");
            n = scan.nextInt();
            buffer = scan.nextLine();
        }        
        
        scan.close();
    	DataBaseLinker.dropTables();
        DataBaseLinker.endConnection();
        System.out.println("Program End...");

    }

	private static void AcademyWithBus(Scanner scan) throws Exception, JSONException {
		int n = 1;
		int m = 1;
		String buffer;
		while(m == 1) {
			try {
				while (n == 1)
				{
					System.out.println("검색을 원하시는 지역을 입력하세요. ");
			    	System.out.print("시/군 >> ");
			    	String sigun = scan.nextLine();
			    	System.out.print("구/읍면동 >> ");
			    	String emd_name = scan.nextLine();
			    	System.out.print("출력할 결과 수 >> ");
			    	String count = scan.nextLine();
			    	
			    	System.out.println("[" + sigun + " " + emd_name + "]" + " 지역의 학원 목록을 검색합니다. ");
			    	JSONArray array = Educate.showEducation(sigun, emd_name, count);
			    	System.out.println("\n>> 1입력: 다시 검색  / 2입력 : 이 목록에서 선택하기");
			    	n = scan.nextInt();
			    	buffer = scan.nextLine();
			    	if (n == 1) continue;
		    	
			    	n = 1;
		        	System.out.print("버스 정류장 정보를 얻을 학원을 입력하세요. (번호 입력) >> ");
		        	int index = scan.nextInt();
		        	JSONObject obj = (JSONObject) array.get(index);
		        	String latitude = obj.get("latitude2").toString().substring(0, 9);
		        	String longitude = obj.get("longitude2").toString().substring(0, 9);
		        	
		        	JSONObject jobj = NearByStation.loadNearByStation(longitude, latitude);
		
		        	NearByStation.print(jobj);
		        	System.out.println("\n>> 1입력: 다시 검색  / 2입력 : 경로 검색하기");
			    	n = scan.nextInt();
			    	buffer = scan.nextLine();
		        	if (n == 1) continue;
				}
			}catch (Exception e) {
	        	System.out.println("(!) 다시 입력해 주세요");
	        	continue;
			} break;
		}
	}

	static void searchTransferRouteInterface(Location l, TransferRoute tr, BusData bus) throws SQLException, IOException{
		 int n = 1;
		 Scanner scan = new Scanner(System.in);
		 String input;
		 String buffer;
		 DataBaseLinker.deleteBusDataTable();
		 DataBaseLinker.deleteTransferRoutetTable();
		 while(n == 1) {
	        System.out.println(">> 출발지 검색");
	        input = scan.nextLine();
	        l.searchLocationList(input);
	        System.out.println("\n>> 1입력: 다시 검색  / 2입력 : 이 목록에서 선택하기");
	        n = scan.nextInt();
	        buffer = scan.nextLine();
		}
		n = 1;
		while(n == 1) {
			int idn;
	        System.out.println(">> 출발지 선택 (번호 입력)");
	        idn = scan.nextInt();
	        buffer = scan.nextLine();
	        try {
	        	l.setStartingPoint(idn);
	        	n = 2;
	        }catch (Exception e) {
	        	System.out.println("(!) 다시 입력해 주세요");
	        	n = 1;
	        }			
		}

		n = 1;
		while(n == 1) {
			System.out.println(">> 도착지 검색");
	        input = scan.nextLine();
	        l.searchLocationList(input);
	        System.out.println("\n>> 1입력: 다시 검색  / 2입력 : 이 목록에서 선택하기");
	        n = scan.nextInt();
	        buffer = scan.nextLine();
		}
		n = 1;
		while(n == 1) {
			int idn;
			System.out.println(">> 도착지 선택 (번호 입력)");
			idn = scan.nextInt();
	        buffer = scan.nextLine();
	        try {
	        	l.setDestination(idn);
	        	n = 2;
	        }catch (Exception e) {
	        	System.out.println("(!) 다시 입력해 주세요");
	        	n = 1;
	        }			
		}
        System.out.println(">> 경로를 검색합니다");
        l.searchTransferRoute();
        tr.showRouteList();
        
        System.out.println("\n>> 경로를 선택하십시오");
        input = scan.nextLine();
        bus.getBusData(input);

	}
}
