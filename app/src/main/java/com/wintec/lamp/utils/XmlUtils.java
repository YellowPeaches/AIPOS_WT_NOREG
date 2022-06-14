package com.wintec.lamp.utils;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XmlUtils {

    private ParseListener listener;

    public interface ParseListener {
        public void doWork(String result);

        public void doWorkList(String result, HashMap<String, List<Integer>> map);
    }

    public XmlUtils(ParseListener listener) {
        this.listener = listener;
    }

    public void domParse(String xml) {
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        //2.获取SAXparser实例
        SAXParser saxParser = null;
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException mE) {
            mE.printStackTrace();
        } catch (SAXException mE) {
            mE.printStackTrace();
        }
        //创建Handel对象,解析出目标
        SAXDemoHandel handel = new SAXDemoHandel();
        try {
            saxParser.parse(is, handel);
        } catch (IOException mE) {
            mE.printStackTrace();
        } catch (SAXException mE) {
            mE.printStackTrace();
        }
    }

    class SAXDemoHandel extends DefaultHandler {
        private boolean isName = false;
        private HashMap<String, List<Integer>> Skumap;

        //遍历xml文件开始标签
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        //遍历xml文件结束标签
        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (qName.equals("result")) {
                isName = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (qName.equals("result")) {
                isName = false;
            }
        }


        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if (!isName) {
                return;
            }
            String value = new String(ch, start, length).trim();
            listener.doWork(value);

        }


    }

    public void domParseXml(String xml) {
        DocumentBuilder newDocumentBuilder = null;
        HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        String result = "";
        try {
            newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
            //获取所有result节点
            NodeList resultList = parse.getElementsByTagName("result");
            result = resultList.item(0).getFirstChild().getNodeValue();
            NodeList xyList = parse.getElementsByTagName("object");
            for (int i = 0; i < xyList.getLength(); i++) {
                Element element = (Element) xyList.item(i);
                NodeList nameList = element.getElementsByTagName("name");
                String name = nameList.item(0).getFirstChild().getNodeValue();
                NodeList box = element.getElementsByTagName("bndbox");
                NodeList childNodes = box.item(0).getChildNodes();
                List<Integer> boxList = new ArrayList<Integer>();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node node = (Node) childNodes.item(j);
                    if ("xmin".equals(node.getNodeName())) {
                        Integer xmin = Integer.valueOf(node.getFirstChild().getNodeValue());
                        boxList.add(xmin);
                    }
                    if ("ymin".equals(node.getNodeName())) {
                        Integer xmin = Integer.valueOf(node.getFirstChild().getNodeValue());
                        boxList.add(xmin);
                    }
                    if ("xmax".equals(node.getNodeName())) {
                        Integer xmin = Integer.valueOf(node.getFirstChild().getNodeValue());
                        boxList.add(xmin);
                    }
                    if ("ymax".equals(node.getNodeName())) {
                        Integer xmin = Integer.valueOf(node.getFirstChild().getNodeValue());
                        boxList.add(xmin);
                    }
                }
                map.put(name, boxList);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listener.doWorkList(result, map);
    }

}
