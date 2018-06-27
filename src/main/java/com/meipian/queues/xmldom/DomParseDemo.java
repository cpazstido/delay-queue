package com.meipian.queues.xmldom;

import com.meipian.queues.xmlsax.XmlParseUtils;
import org.junit.Test;
import org.springframework.util.StringUtils;
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

            //设置后方可获取NameSpaceURI
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            //2.create Document object
            Document doc = documentBuilder.parse(is);
//            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            //get root node
            System.out.println("Root element :"+ root.getNodeName());
            System.out.println("----------------------------");

            //3.get student list;
            NodeList nList = root.getChildNodes();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);

                if (node instanceof Element) {
                    Element eElement = (Element) node;
                    System.out.println("----------------------------");
                    System.out.println("NameSpaceURI---->"+node.getNamespaceURI());
                    System.out.println("node name---->"+ node.getNodeName());
                    System.out.println("local name---->"+eElement.getLocalName());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test(){
        String [] strs = StringUtils.tokenizeToStringArray("asdkwl,a;d;wekj",",;");
        for (String str:strs) {
            System.out.println(str);
        }
    }
}
