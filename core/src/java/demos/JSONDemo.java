package demos;

import tools.JsonReader;
import org.json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

//https://www.tutorialspoint.com/org_json/org_json_quick_guide.htm#
public class JSONDemo {
    public static void main(String[] args) {
        new JSONDemo().rara();
    }
    public void rara(){
        try {
            String content = new String(Files.readAllBytes(Paths.get("test_json.json")));
            System.out.println(content);
            JSONObject jo=JsonReader.read_str_json(content);
            System.out.println("aaa\n"+jo.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void fafa(){
        String string = "{\"name\": \"Sam Smith\", \"technology\": \"Python\", \"value\": 100}";
        JSONObject json = new JSONObject(string);
//        System.out.println(json.toString());
//        String technology = json.getString("technology");
//        System.out.println(technology);
        int value = json.getInt("value");
        System.out.println(value);
    }
    public void zaza(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "Robert");
        jsonObject.put("ID", 1);
        jsonObject.put("Fees", 10.2);
        jsonObject.put("Active", true);
        jsonObject.put("Details", JSONObject.NULL);

        //Convert a JSONObject to XML
        String xmlText = XML.toString(jsonObject);
        System.out.println(xmlText);
        //Convert an XML to JSONObject
        System.out.println(XML.toJSONObject(xmlText));
    }
    public void nana(){
        Properties properties = new Properties();
        properties.put("title", "This is a title text");
        properties.put("subtitle", "This is a subtitle text");

        System.out.println("Properties to JSON");
        JSONObject jsonObject = Property.toJSONObject(properties);
        System.out.println(jsonObject);

        System.out.println("JSON to properties");
        System.out.println(Property.toProperties(jsonObject));
    }
    public void sasa(){
        String jsonText = new JSONStringer()
                .object()
                .key("Name")
                .value("Robert")
                .endObject()
                .toString();
        System.out.println(jsonText);

        jsonText = new JSONStringer()
                .array()
                .value("Robert")
                .value("Julia")
                .value("Dan")
                .endArray()
                .toString();
        System.out.println(jsonText);

        jsonText = new JSONStringer()
                .array()
                .value("Robert")
                .value("Julia")
                .value("Dan")
                .object()
                .key("Name")
                .value("Robert")
                .endObject()
                .endArray()
                .toString();
        System.out.println(jsonText);
    }

    public void yaya(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "Robert");
        jsonObject.put("ID", 1);
        jsonObject.put("Fees", 10.2);
        jsonObject.put("Active", true);
        jsonObject.put("Other Details", JSONObject.NULL);

        JSONArray list = new JSONArray();
        list.put("foo");
        list.put(100);
        jsonObject.put("list",list);
        System.out.println(jsonObject);
    }
    public void gaga(){
        JSONArray list = new JSONArray();
        list.put("name");
        list.put("Robert");

        System.out.println("XML from a JSONArray: ");
        String xml = JSONML.toString(list);
        System.out.println(xml);

        System.out.println("JSONArray from a XML: ");
        list = JSONML.toJSONArray(xml);
        System.out.println(list);

        System.out.println("JSONObject from a XML: ");
        JSONObject object = JSONML.toJSONObject(xml);
        System.out.println(object);

        System.out.println("XML from a JSONObject: ");
        xml = JSONML.toString(object);
        System.out.println(xml);
    }
    public void kaka(){
        String cookie = "username = Mark Den; xxx = 100; expires = Thu, 15 Jun 2020 12:00:00 UTC; path = /";

        //Case 1: Converts Cookie String to JSONObject
        JSONObject cookieJSONObject = Cookie.toJSONObject(cookie);
        cookieJSONObject.getInt("xxx");
        //System.out.println(""+(int));
        JSONObject cookielistJSONObject = new JSONObject();

        cookielistJSONObject.put(cookieJSONObject.getString("name"),
                cookieJSONObject.getString("value"));

        String cookieList = CookieList.toString(cookielistJSONObject);
        System.out.println(cookieList);
        System.out.println(CookieList.toJSONObject(cookieList));
    }
    public void lala(){
        String cookie = "username = Mark Den; expires = Thu, 15 Jun 2020 12:00:00 UTC; path = /";

        //Case 1: Converts Cookie String to JSONObject
        JSONObject jsonObject = Cookie.toJSONObject(cookie);
        System.out.println(jsonObject);

        //Case 2: Converts JSONObject to Cookie String
        System.out.println(Cookie.toString(jsonObject));
    }
    public void wawa() {
        String csvData = "INDIA, UK, USA";

        //Case 1: CSV to JSON Array
        JSONArray jsonArray = CDL.rowToJSONArray(new JSONTokener(csvData));
        System.out.println(jsonArray);

        //Case 2: JSONArray to CSV
        System.out.println(CDL.rowToString(jsonArray));

        //Case 3: CSV to JSONArray of Objects
        csvData = "empId, name, age \n" +
                "1, Mark, 22 \n" +
                "2, Robert, 35 \n" +
                "3, Julia, 18";
        System.out.println(CDL.toJSONArray(csvData));

        //Case 4: CSV without header
        jsonArray = new JSONArray();
        jsonArray.put("empId");
        jsonArray.put("name");
        jsonArray.put("age");
        csvData = "1, Mark, 22 \n" + "2, Robert, 35 \n" + "3, Julia, 18";
        System.out.println(CDL.toJSONArray(jsonArray, csvData));
    }

    public void baba() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name", "Robert");
        jsonObject.put("ID", 1);
        jsonObject.put("Fees", 10.2);
        jsonObject.put("Active", true);
        jsonObject.put("Other Details", JSONObject.NULL);

        JSONArray list = new JSONArray();
        list.put("foo");
        list.put(100);
        jsonObject.put("list", list);
        System.out.println(jsonObject);
    }
}