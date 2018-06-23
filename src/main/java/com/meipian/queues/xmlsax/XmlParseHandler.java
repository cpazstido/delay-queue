package com.meipian.queues.xmlsax;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.List;

public class XmlParseHandler extends DefaultHandler {
    protected Logger logger = Logger.getLogger(XmlParseHandler.class);
    private List<User> users;
    private String currentTag; // 记录当前解析到的节点名称
    private User user; // 记录当前的user

    /**
     * 文档解析结束后调用
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        System.out.println("endDocument"+">>>"+  "-endDocument()");
    }

    /**
     * 节点解析结束后调用
     * @param uri : 命名空间的uri
     * @param localName : 标签的名称
     * @param qName : 带命名空间的标签名称
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
        System.out.println("endElement"+">>>"+ localName + "-endElement()");
        if("user".equals(localName)){
            users.add(user);
            user = null;
        }
        currentTag = null;
    }

    /**
     * 文档解析开始调用
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        System.out.println("startDocument"+">>>"+ "startDocument()");
        users = new ArrayList<User>();
    }

    /**
     * 节点解析开始调用
     * @param uri : 命名空间的uri
     * @param localName : 标签的名称
     * @param qName : 带命名空间的标签名称
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        System.out.println("startElement"+">>>"+ localName +" <"+qName+ "> -startElement()");
        if ("user".equals(localName)) { // 是一个用户
            for (int i = 0; i < attributes.getLength(); i++) {
                System.out.println("attributes"+">>>"+ "attribute_name：" + attributes.getLocalName(i)
                        + "  attribute_value：" + attributes.getValue(i));
                user = new User();
                if("id".equals(attributes.getLocalName(i))){
                    user.setId(Long.parseLong(attributes.getValue(i)));
                }
            }
        }
        currentTag = localName; // 把当前标签记录下来
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        String value = new String(ch,start,length); // 将当前TextNode转换为String
        System.out.println("characters"+">>>"+ value+"");
        if("name".equals(currentTag)){  // 当前标签为name标签，该标签无子标签，直接将上面获取到的标签的值封装到当前User对象中
            // 该节点为name节点
            user.setName(value);
        }else if("password".equals(currentTag)){  // 当前标签为password标签，该标签无子标签，直接将上面获取到的标签的值封装到当前User对象中
            // 该节点为password节点
            user.setPassword(value);
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
