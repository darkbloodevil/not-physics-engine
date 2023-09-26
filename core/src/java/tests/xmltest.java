package tests;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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
//            Document document = reader.read(new InputSource( new StringReader( """
//                    <?xml version="1.0" encoding="UTF-8"?>
//                    <note>
//                      <force>
//                      </force>
//                      <from>Jani</from>
//                      <heading>Reminder</heading>
//                      <body>Don't forget me this weekend!</body>
//                    </note>""")));
//
//            Document document1= DocumentHelper.createDocument();
//            Element root = document1.addElement("root");
            Document document = reader.read(new File("test.xml"));
            Element root_element = document.getRootElement();
            Stack<Iterator<Element>> iterator_stack = new Stack<>();
            iterator_stack.add(root_element.elementIterator());
            int tabs = 0;
            while (!iterator_stack.empty()) {
                Iterator<Element> iterator = iterator_stack.peek();
                if (!iterator.hasNext()) {
                    iterator_stack.pop();
                    tabs--;
                    continue;
                }
                Element element = iterator.next();
                if (element.elementIterator().hasNext()) {
                    iterator_stack.push(element.elementIterator());
                    System.out.println(new String(new char[tabs]).replace("\0", "\t") + element.getName() + "\t");
                    tabs++;
                } else {
                    System.out.println(new String(new char[tabs]).replace("\0", "\t") + element.getName() + "\t" + element.getText());
                }

            }


        } catch (DocumentException e) {

            e.printStackTrace();
        }
//        bookList.forEach(i->System.out.println(i.toString()));

    }

}
