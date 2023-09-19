package NotBox2D;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.*;
import org.dom4j.tree.DefaultDocument;
import org.json.JSONObject;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
public class XmlMessage {
    Document document;
    Element root;
    public XmlMessage(){
        document= DocumentHelper.createDocument();
        root = document.addElement("root");
    }

}
