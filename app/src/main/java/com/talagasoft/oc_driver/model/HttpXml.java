package com.talagasoft.oc_driver.model;

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by compaq on 12/06/2016.
 */

public class HttpXml {

    StringBuilder stringBuilder;
    Document mDoc;
    String _url;
    private ArrayList<Node> _arNode;

    public HttpXml(String mUrl) {
        _url=mUrl;
        mDoc=GetUrl(_url);
    }
    public HttpXml(){

    }

    public Document GetUrl(String myUrl){
        stringBuilder = GetUrlData(myUrl);
        Document doc = null;
        try {
            doc=loadXMLFromString(String.valueOf(stringBuilder));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return doc;

    }

    public StringBuilder GetUrlData(String myUrl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            DefaultHttpClient client = new DefaultHttpClient();
            URL url = new URL(myUrl);
            URI website = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
                    url.getPath(), url.getQuery(), url.getRef());

            HttpGet request = new HttpGet();
            request.setURI(website);

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            stringBuilder = new StringBuilder();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Log.d("GetUrlData","url="+myUrl+", result="+stringBuilder);
        return stringBuilder;
    }

    public static Document loadXMLFromString(String xml) throws IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is = new InputSource(new StringReader(xml));

        return builder.parse(is);
    }
    public int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    public float getKeyFloat(String vKeyName){
        String v=getKey(vKeyName);
        if(v==""){
            v="0";
        }
        return Float.parseFloat(v);
    }
    public String getKey(String vKeyName) {
        String vResult="";
        if(stringBuilder==null){
            Log.d("HttpXml","stringBuilder is null");
            return vResult;
        }
        try {
            if(mDoc==null) {
                mDoc = loadXMLFromString(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        if(mDoc != null) {
            NodeList nl1;
            nl1 = mDoc.getElementsByTagName(vKeyName);
            if (nl1 != null) {
                Node node1 = nl1.item(0);
                if(node1 != null) {
                    vResult = node1.getTextContent();
                }
            }
        }
        return vResult;
    }
    public String getKeyIndex(int position, String vKeyName) {
        String vResult="";
        if(_arNode != null ) {
            NodeList nodeList = _arNode.get(position).getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node node = nodeList.item(j);
                String vKey=node.getNodeName();
                if (vKey.equals(vKeyName)) {
                    vResult = node.getTextContent();
                }
            }
        }
        return vResult;
    }
    public float getKeyIndexFloat(int position, String vKeyName){
        String s=getKeyIndex(position,vKeyName);
        if(s.equals(""))s="0";
        return Float.parseFloat(s);
    }
    public int getKeyIndexInt(int position, String vKeyName){
        String s=getKeyIndex(position,vKeyName);
        if(s.equals(""))s="0";
        return Integer.parseInt(s);
    }

    public void getGroup(String vToken) {
        if(mDoc != null) {
            if(_arNode == null){
                _arNode=new ArrayList<>();
            }
            NodeList nl;
            nl = mDoc.getElementsByTagName(vToken);
            if(nl != null){
                for(int i=0;i<nl.getLength();i++){
                    _arNode.add(nl.item(i));
                }
            }
        }
    }

    public int getCount() {
        if(_arNode==null){
            return 0;
        }
        return _arNode.size();
    }

    public int getKeyInt(String vKey) {
        String s=getKey(vKey);
        if(s.isEmpty())s="0";
        return Integer.parseInt(s);
    }
}
