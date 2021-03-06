package boilermake.swipemart2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

public class SwipeActivity extends AppCompatActivity {

    static HashMap<String, Integer> itemVotes;

    ImageButton butt_up, butt_down;
    Button butt_done;

    private String msg;

    private String remove_spaces(String message) {
        if(message==null)
            return null;
        String query = "";
        for(int i = 0; i<message.length(); i++)
        {
            char c = message.charAt(i);
            if(c==' ')
                query+="%20";
            else
                query+=c;
        }
        return query;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemVotes = new HashMap<String, Integer>();


        final Intent intent = getIntent();
        msg = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);
        Log.d("Received msg:", msg);


        final TextView item_text = (TextView)findViewById(R.id.item_name);
        item_text.setText(msg);

        msg = remove_spaces(msg);
        ImageView img = (ImageView)findViewById(R.id.product_pic);

        final FetchFromAPI task = new FetchFromAPI();
        task.execute(msg);

        butt_up =  (ImageButton)findViewById(R.id.img_up);
        butt_down = (ImageButton)findViewById(R.id.img_down);
        butt_done = (Button)findViewById(R.id.product_done);

        butt_done.setVisibility(View.VISIBLE);
        final Intent intent1 = new Intent(this, SelectActivity.class);

        butt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> a= new ArrayList<String>();
                for(Map.Entry<String, Integer> entry : itemVotes.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();

                    String obj = key + " " + value;
                    a.add(obj);
                }
                intent1.putStringArrayListExtra("MAP_ARRAY_LIST", a);

                startActivity(intent1);

            }
        });

        butt_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = task.current_ten_items[(task.count-2)%10].itemId;
                itemVotes.put(id, 1);
                System.out.println(itemVotes);
                task.onPostExecute(msg);
            }
        });
        butt_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = task.current_ten_items[(task.count-2)%10].itemId;
                itemVotes.put(id, 0);
                System.out.println(itemVotes);
                task.onPostExecute(msg);
            }
        });
    }

    public static void addToHashMap(String itemID, int isTrue)
    {
        itemVotes.put(itemID, isTrue);
    }


    /**
     * ---------------------------------------------------CLASS-------------------------------------------
     */
    public class FetchFromAPI extends AsyncTask<String, Void, String>{

        public int count=1;
        public Item current_ten_items[];
        public static final String ENDPOINT = "http://api.walmartlabs.com/v1/search?query=";
        public static final String suffix = "&facet=on&apiKey=afxazgkdvur2tcmxf72b2s3k";
        public static final String startstring = "&start=";

        /**
         *
         * @param query
         * @param start
         * @return
         * @throws IOException
         */
        public String makeCallToAPI(String query, int start) throws IOException {
            URL url = new URL(ENDPOINT + query + suffix + startstring + start);
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            BufferedReader res = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuffer sBuffer = new StringBuffer();
            String inputLine;
            while ((inputLine = res.readLine()) != null)
                sBuffer.append(inputLine);
            res.close();
            System.out.println("Made a call to the API!");
            return sBuffer.toString();
        }

        /**
         * returns a list of items
         * @param query what you are interested in
         * @param start which item you want  to start from
         * @return list of items
         */
        public Item[] getNextTenItems(String query, int start)
        {
            try {
                String JString  = makeCallToAPI(query, start);
                Gson gson = new Gson();
                //convert the json string back to object
                APICall obj = gson.fromJson(JString, APICall.class);
                current_ten_items =obj.items;

            }
            catch(Exception e)
            {
                System.out.println("Hello");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected String doInBackground(String... params) {
            if(params.length==0){
                return null;
            }


            if(count%10==1)
                try {
                    String JString  = makeCallToAPI(params[0], count);
                    Gson gson = new Gson();
                    System.out.println("count = " + count);

                    APICall obj = gson.fromJson(JString, APICall.class);
                    System.out.println("List updated");
                    current_ten_items = obj.items;


                }
                catch(Exception e)
                {
                    System.out.println("error");
                    e.printStackTrace();
                }

            return params[0];
        }

        @Override
        /**
         * does blah
         */

        protected void onPostExecute(final String query){


                update_product();
                count++;
//                count = count% 10;

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    if(count%10==0)
                    try {
                        String q = query;
                        String JString = makeCallToAPI(query, count);
                        Gson gson = new Gson();

                        APICall obj = gson.fromJson(JString, APICall.class);
//                        System.out.println("List updated");
//                        System.out.println("count: "+ count);
                        current_ten_items = obj.items;

                    } catch (Exception e) {
                        // log error
                    }
                    return null;
                }

            }.execute();}


        private Bitmap bmp;
        private ImageView img;
        public void update_product(){
            img = (ImageView)findViewById(R.id.product_pic);
            final Item i = current_ten_items[(count-1)%10];

            TextView name = (TextView)findViewById(R.id.product_name);
            if(i.name!=null)
            if(i.name.length()>25)
                i.name = i.name.substring(0,25) + "...";

            name.setText(i.name);
            TextView desc = (TextView)findViewById(R.id.product_desc);
            if(i.shortDescription!=null)
                if(i.shortDescription.length()>=100)
                desc.setText(i.shortDescription.substring(0, 100)+"...");
                else
                desc.setText(i.shortDescription);
            else
                desc.setText("No description");


            TextView price = (TextView)findViewById(R.id.product_price);
            if (i.salePrice!=null)
                price.setText(("$"+i.salePrice + "\n(Sale!)"));
            else
            price.setText("$"+i.msrp);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        InputStream in = new URL(i.thumbnailImage).openStream();
                        bmp = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        // log error
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (bmp != null)
                        img.setImageBitmap(bmp);
                }

            }.execute();
        }

    }



    class Item
    {
        String itemId;
        String name;
        String categoryPath;
        String shortDescription;
        String msrp;
        String salePrice;
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

    class MapClass
    {
        double ItemId;
        int likedOrNot;

        public MapClass(double itemId, int likedOrNot) {
            ItemId = itemId;
            this.likedOrNot = likedOrNot;
        }
    }

}
