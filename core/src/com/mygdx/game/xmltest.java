package com.mygdx.game;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.*;
import org.dom4j.tree.DefaultDocument;
import org.json.JSONObject;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

public class xmltest {
    public static void main(String[] args) {
        ArrayList<JSONObject> bookList = new ArrayList<JSONObject>();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new InputSource( new StringReader( """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <note>
                      <to>Tove</to>
                      <from>Jani</from>
                      <heading>Reminder</heading>
                      <body>Don't forget me this weekend!</body>
                    </note>""")));

            Document document1= DocumentHelper.createDocument();
            Element root = document1.addElement("root");


        } catch (DocumentException e) {

            e.printStackTrace();
        }
//        bookList.forEach(i->System.out.println(i.toString()));

    }

}
