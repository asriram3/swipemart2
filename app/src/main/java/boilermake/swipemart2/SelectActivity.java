package boilermake.swipemart2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity {

    ItemSingular[] items;
    MatchAnalysis ma;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ArrayList<String> arr = intent.getStringArrayListExtra("MAP_ARRAY_LIST");

        System.out.println("This is arr:");
        System.out.println(arr);

        ma = new MatchAnalysis(arr);


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {


                    try {
                        items = ma.getItems(false);

                    } catch (Exception e) {
                        // log error
                    }
                return null;
            }

        }.execute();


//    for(ItemSingular item: items)
//        {
//            System.out.println(item);
//        }

        final Intent intent1 = new Intent(this, GraphActivity.class);
        intent1.putStringArrayListExtra("MAP_ARRAY_LIST", arr);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(intent1);
            }
        });



    }


}

