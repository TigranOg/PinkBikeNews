package com.rss.pinkbike.util;

import android.util.Log;
import com.rss.pinkbike.entities.RssEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/3/13
 * Time: 1:55 AM
 */
public class RssManager {

    public static Map<String, RssEntity> getRssMap(int position) {
        return parseXml(getRss(), position);
    }

    public static String getRss() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse responseGet;
        String response = null;

        String query = "http://www.pinkbike.com/pinkbike_xml_feed.php";
        HttpGet httpGet = new HttpGet(query);

        try {
            responseGet = httpClient.execute(httpGet);
            HttpEntity resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {
                response = EntityUtils.toString(resEntityGet);
            }

        } catch (IOException e) {
            Log.e("pinkbike", "void getRss() IOException ERROR");
        }

        return response;
    }

    private static Map<String, RssEntity> parseXml(String response, int position) {
        Map<String, RssEntity> rssEntityMap = new HashMap<String, RssEntity>();

        if (response == null) {
            return rssEntityMap;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(response)));

            Element element = document.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("item");

            if (nodeList.getLength() > 0) {
                BitmapManager bitmapManager = new BitmapManager();

                for (int i = nodeList.getLength() - 1; i >= 0; i--) {
                    Element entry = (Element) nodeList.item(i);

                    Element titleElement = (Element) entry.getElementsByTagName("title").item(0);
                    Element descriptionElement = (Element) entry.getElementsByTagName("description").item(0);
                    Element pubDateElement = (Element) entry.getElementsByTagName("pubDate").item(0);
                    Element linkElement = (Element) entry.getElementsByTagName("link").item(0);

                    String title = titleElement.getFirstChild().getNodeValue();
                    String description = descriptionElement.getFirstChild().getNodeValue();
                    Date pubDate = new Date(pubDateElement.getFirstChild().getNodeValue());
                    String link = linkElement.getFirstChild().getNodeValue();

                    int startIndex = description.indexOf("src") + 5;
                    int endIndex = description.indexOf(".jpg") + 4;
                    String imgUrl = description.substring(startIndex, endIndex);

                    String imageName = bitmapManager.getFileName(imgUrl);
                    bitmapManager.getBitmapForListView(imgUrl);


                    RssEntity rssEntity = new RssEntity(title, description, pubDate, link, imageName, 0, position++);

                    rssEntityMap.put(link, rssEntity);
                }
            }

        } catch (ParserConfigurationException e) {
            Log.e("pinkbike", "void parseXml() ParserConfigurationException ERROR");
        } catch (SAXException e) {
            Log.e("pinkbike", "void parseXml() SAXException ERROR");
        } catch (IOException e) {
            Log.e("pinkbike", "void parseXml() IOException ERROR");
        }

        return rssEntityMap;
    }
}
