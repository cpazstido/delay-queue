package com.meipian.queues.xmldom;

import com.meipian.queues.xmlsax.XmlParseUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class DomParseDemo {
    public static void main(String[] args) {
        try {
            // 加载文件返回文件的输入流
            InputStream is = DomParseDemo.class.getClassLoader().getResourceAsStream("student.xml");
            //1.create DocumentBuilder object
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            //2.create Document object
            Document doc = documentBuilder.parse(is);
//            doc.getDocumentElement().normalize();

            //get root node
            System.out.println("Root element :"+ doc.getDocumentElement().getNodeName());

            Element root = doc.getDocumentElement();
            //3.get student list;
            NodeList nList = root.getChildNodes();
            System.out.println("----------------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :"+ nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("local name:"+eElement.getLocalName());
                    //get attribute
                    System.out.println("Student roll no : "+ eElement.getAttribute("rollno"));
                    //get element content
                    System.out.println("First Name : "+ eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Marks : "+ eElement.getElementsByTagName("marks").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
