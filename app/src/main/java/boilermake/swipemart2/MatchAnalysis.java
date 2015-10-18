package boilermake.swipemart2;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by an5ra on 10/18/2015.
 */
public class MatchAnalysis {

    HashMap<String, Integer> data;

    //to parse JSON
    public static final String ENDPOINT = "http://api.walmartlabs.com/v1/items?ids=";
    public static final String suffix = "&format=json&apiKey=afxazgkdvur2tcmxf72b2s3k";
    public static String itemIds = "";

    public MatchAnalysis(ArrayList<String> data)
    {
        this.data = new HashMap<String, Integer>();
        for(String s: data)
        {
            int space = s.indexOf(' ');
            if(space==-1)
                continue;
            String id = s.substring(0,space);
            int isLiked = 0;
            if(s.charAt(s.length()-1)=='1')
                isLiked=1;
            this.data.put(id,isLiked);
        }

        System.out.println("This is from MatchAnalysis");
        System.out.println(this.data);

    }

    public String getItemsInformation(boolean getDislikes) throws IOException {
        int count = 0;
        itemIds = "";
        for(Map.Entry<String,Integer> entry: data.entrySet())
        {
            String key = entry.getKey();
            if(data.get(key)==1)
            {itemIds+=key+",";
                count++;}
            if(getDislikes && data.get(key)==0)
            {itemIds+=key+",";count++;}

            System.out.println("Going through the hashmap!");
            System.out.println(key + " " + data.get(key));
            if(count>20)
                break;
        }


        URL url = new URL(ENDPOINT + itemIds + suffix);
        System.out.println(url.toString());
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

    public ItemSingular[] getItems(boolean getDislikes)
    {
        try {


            String JString  = getItemsInformation(getDislikes);

//            System.out.println(JString);
            Gson gson = new Gson();
            //convert the json string back to object
            ItemPlural obj = gson.fromJson(JString, ItemPlural.class);

            for(ItemSingular item: obj.items)
            {
                System.out.println(item);
            }

            return obj.items;
        }
        catch(Exception e)
        {
            System.out.println("error");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String [] args)
    {
        ArrayList<String> arrayList =new ArrayList<String>();
        arrayList.add("26999374 1");
        arrayList.add("41424917 0");
        arrayList.add("45089317 1");
        MatchAnalysis ma = new MatchAnalysis(arrayList);
        ItemSingular[] items =  ma.getItems(false);
        for(ItemSingular item: items)
            System.out.println(item);

    }




}



class BarGraphData
{
    int intervals[];
    double lowestValue;
    double highestValue;


}

class ItemSingular
{
    String itemId;
    String name;
    String categoryPath;
    String shortDescription;
    String salePrice;
    String thumbnailImage;
    String addToCartUrl;
    float customerRating;
    String brand;



    public ItemSingular()
    {    }

    @Override
    public String toString()
    {
        return (itemId + " " +name);
    }

}

class ItemPlural
{
    ItemSingular items[];

}