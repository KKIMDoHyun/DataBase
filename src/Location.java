
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
public class Location extends DataBaseLinker {
    String sX, sY;
    String dX, dY;
    String[] idArr;

    public void loadLocationList() {
        try {
            //DocumentBuilderFactory 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder;
            Document doc = null;
            BufferedReader br = null;

            StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/pathinfo/getLocationInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=clTCybhCPqf%2Bj5H5Tr%2B9sCQf8y3mVsdBeyn5NWVVCki14dqPP9hJ617v3nZTepkyBFBP1gjeX0k6%2FxcEOSsWiw%3D%3D"); /*Service Key*/

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Content-type", "application/json");
            BufferedReader rd;
            if (httpConn.getResponseCode() >= 200 && httpConn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
            }
            br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String result = "";
            String line;
            while ((line = br.readLine()) != null) {
                result = result + line.trim();// result = URL로 XML을 읽은 값
            }

            httpConn.disconnect();

            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//itemList");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            String locationFormat = "insert into Location values (%s, \'%s\', %s, %s)";
            String queryStr;
            System.out.println("위치 불러오는 중...");
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    queryStr = String.format(locationFormat, getTagValue("poiId", eElement), getTagValue("poiNm", eElement), getTagValue("gpsX", eElement), getTagValue("gpsY", eElement));
                    s.executeUpdate(queryStr);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void searchLocationList(String name) {
    	idArr = new String[100];
        try{
            System.out.println(name+" 검색 결과");
            r = s.executeQuery(" select * from Location where locationName like \'%"+name+"%\' limit 100");
            System.out.println("[번호] locationID : locationName ");
            System.out.println("----------------------------------------------------------");
            int n = 0;
            while (r.next()) {
            	System.out.printf("[%3d] %10d : %s\n",n+1, r.getInt(1), r.getString(2).trim());
                idArr[n] = r.getString(1);
                n+=1;
            }
        } catch (SQLException ex) {
           ex.printStackTrace();
        }

    }

    public void setDestination(int idn) throws SQLException{
        try{
        	
            r = s.executeQuery(" select locX, locY, locationName from Location where locationID = "+idArr[idn-1]);
            r.next();
            dX = r.getString(1);
            dY = r.getString(2);
            System.out.println(">> 도착지를 ["+r.getString(3).trim()+"](으)로 설정합니다");
        } catch (SQLException ex) {
            throw ex;
        }
    }
    public void setStartingPoint(int idn) throws SQLException{
        try{
            r = s.executeQuery(" select locX, locY, locationName from Location where locationID = " +idArr[idn-1]);
            r.next();
            sX = r.getString(1);
            sY = r.getString(2);
            System.out.println(">> 출발지를 ["+r.getString(3).trim()+"](으)로 설정합니다");
        }catch (Exception ex) {  
        	throw ex;
        }
    }


    public void searchTransferRoute(){	
    	TransferRoute tr = new TransferRoute(); //db 저장용 TransferRoute 인스턴스
        tr.getTransferRouteList(sX.trim(), sY.trim(), dX.trim(), dY.trim());
        
    }

}


