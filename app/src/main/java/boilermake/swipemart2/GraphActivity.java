package boilermake.swipemart2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    ItemSingular[] items;
    ItemSingular[] likedItems;
    MatchAnalysis ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ArrayList<String> arr = intent.getStringArrayListExtra("MAP_ARRAY_LIST");

        ma = new MatchAnalysis(arr);


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {


                try {
                    items = ma.getItems(true);
                    likedItems = ma.getItems(false);

                } catch (Exception e) {
                    // log error
                }
                return null;
            }
            @Override
        protected void  onPostExecute(Void dfg)
            {
                double lowest_price = 99999;
                double highest_price = 0;
                for(ItemSingular item: items)
                {
                    if(Double.parseDouble(item.salePrice)<lowest_price)
                        lowest_price = Double.parseDouble(item.salePrice);
                    if(Double.parseDouble(item.salePrice)>highest_price)
                        highest_price = Double.parseDouble(item.salePrice);

                }
                double interval_length = (highest_price-lowest_price)/6;
                int values[] = new int[6];

                for(ItemSingular item: likedItems)
                {   double price = Double.parseDouble(item.salePrice);
                    int interval = (int)Math.ceil((price - lowest_price)/interval_length)-1;
                    if(interval<0)
                        interval=0;
                    values[interval]+=2;
                }

                for(ItemSingular item: items)
                {   double price = Double.parseDouble(item.salePrice);
                    int interval = (int)Math.ceil((price - lowest_price)/interval_length)-1;
                    if(interval<0){interval = 0;}
                    values[interval]-=1;
                }

                GraphView graph = (GraphView) findViewById(R.id.graph);
                BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(lowest_price,values[0]),
                        new DataPoint(lowest_price+interval_length, values[1]),
                        new DataPoint(lowest_price+2*interval_length, values[2]),
                        new DataPoint(lowest_price+3*interval_length, values[3]),
                        new DataPoint(lowest_price+4*interval_length, values[4]),
                        new DataPoint(lowest_price+5*interval_length, values[5])

                });
                graph.addSeries(series);

            }


        }.execute();








    }

}
