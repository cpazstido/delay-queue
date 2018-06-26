package com.meipian.queues.xmlsax;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class XmlParseUtils {

    public static List<User> getUsers() throws ParserConfigurationException, SAXException, IOException {
        // 加载文件返回文件的输入流
        InputStream is = XmlParseUtils.class.getClassLoader().getResourceAsStream("users.xml");
        XmlParseHandler handler = new XmlParseHandler();
        // 1. 得到SAX解析工厂
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        // 2. 让工厂生产一个sax解析器
        SAXParser newSAXParser = saxParserFactory.newSAXParser();
        // 3. 传入输入流和handler，解析
        newSAXParser.parse(is, handler);
        is.close();
        return handler.getUsers();
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(System.getProperty("java.home"));
        List<User> userList = getUsers();
    }
}
