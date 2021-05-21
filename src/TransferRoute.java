import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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

public class TransferRoute extends DataBaseLinker{


    public void getTransferRouteList(String startX,String startY,String endX,String endY) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder;
            Document doc = null;
            BufferedReader br = null;

            StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoByBusNSub"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8")
                    + "=clTCybhCPqf%2Bj5H5Tr%2B9sCQf8y3mVsdBeyn5NWVVCki14dqPP9hJ617v3nZTepkyBFBP1gjeX0k6%2FxcEOSsWiw%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("startX", "UTF-8") + "=" + URLEncoder.encode(startX, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("startY", "UTF-8") + "=" + URLEncoder.encode(startY, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("endX", "UTF-8") + "=" + URLEncoder.encode(endX, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("endY", "UTF-8") + "=" + URLEncoder.encode(endY, "UTF-8"));

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
            XPathExpression rexpr = xpath.compile("//itemList");
            NodeList routeList = (NodeList) rexpr.evaluate(doc, XPathConstants.NODESET);


            s.executeUpdate("truncate TransferRoute");
            String locationFormat = "insert into TransferRoute values (%d,%d, %s, %s, %s, \'%s\'," +
                    " %s, \'%s\', \'%s\', \'%s\', %s, \'%s\', \'%s\', \'%s\')";
            String queryStr;
            
            for (int i = 0; i < routeList.getLength(); i++) {
                Element routeE = (Element) routeList.item(i);
                String routeDistance = getTagValue("distance", routeE);
                String routeTime = getTagValue("time", routeE);

                NodeList routeInfo = routeList.item(i).getChildNodes();
                for (int j = 0; j < routeInfo.getLength()-2; j++) {
                    Node nPath = routeInfo.item(j+1);
                    if (nPath.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nPath;
                        queryStr = String.format(locationFormat, i+1, j+1,routeDistance, routeTime,
                                getTagValue("routeId", eElement), getTagValue("routeNm", eElement),
                                getTagValue("fid", eElement), getTagValue("fname", eElement),
                                getTagValue("fx", eElement), getTagValue("fy", eElement),
                                getTagValue("tid", eElement), getTagValue("tname", eElement),
                                getTagValue("tx", eElement), getTagValue("ty", eElement));
                        s.executeUpdate(queryStr);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showRouteList(){
        try{
            int routeN = 0;
            int pathN = 1;
            System.out.println("주요 경로");
            r = s.executeQuery(" select * from TransferRoute");
            int showlimit = 5;
            while (r.next()) {
                if(r.getInt(1) == routeN){
                    pathN += 1;
                }else {
                    routeN += 1;
                    pathN = 1;
                    if(routeN > showlimit) break;
                    System.out.print("\n[경로 "+routeN+"] ");
                    System.out.println("(약 "+r.getInt(4)+"분)");
                }
                System.out.println("    ("+pathN+") "+r.getString(8).trim()+" ---> "
                        +r.getString(6).trim()+" >--- "+r.getString(12).trim());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	
}
