package boilermake.swipemart2;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
class Item
{
    double itemId;
    String name;
    String categoryPath;
    String shortDescription;
    String msrp;
    String thumbnailImage;
    String addToCartUrl;
    float customerRating;
    String brand;
    public Item()
    {
    }
    @Override
    public String toString()
    {
        return (itemId + " " +name);
    }
}
class APICall
{
    public APICall()
    {}
    String query;
    int categoryId;
    String relevance;
    Item items[];
}
/**
 * Created by an5ra on 10/17/2015.
 */
public class JsonParser {
    public static final String ENDPOINT = "http://api.walmartlabs.com/v1/search?query=";
    public static final String suffix = "&facet=on&apiKey=afxazgkdvur2tcmxf72b2s3k";
    public static String callMethod(String query, String data) throws IOException {
        URL url = new URL(ENDPOINT + query + suffix);
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();
        BufferedReader res = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuffer sBuffer = new StringBuffer();
        String inputLine;
        while ((inputLine = res.readLine()) != null)
            sBuffer.append(inputLine);
        res.close();
        return sBuffer.toString();
    }
    public Item[] getNextTenItems(String query)
    {
        try {
            String JString  = callMethod(query, "check");
            Gson gson = new Gson();
            //convert the json string back to object
            APICall obj = gson.fromJson(JString, APICall.class);
            return obj.items;
        }
        catch(Exception e)
        {
            System.out.println("Hello");
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {
        try {
//            JSONParser parser=new JSONParser();
            String JString  = callMethod("POST", "check");
            Gson gson = new Gson();
            //convert the json string back to object
            APICall obj = gson.fromJson(JString, APICall.class);
            for(Item item : obj.items)
            {
                System.out.println(item);
            }
//            System.out.println(upc);
        }
        catch(Exception e)
        {
            System.out.println("Hello");
            e.printStackTrace();
        }
    }
}