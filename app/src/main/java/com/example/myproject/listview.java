package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class listview extends AppCompatActivity {
    private ListView lv;
    private ImageView listImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        lv = findViewById(R.id.lv);

        ArrayList<String> elementList = new ArrayList<>();
        elementList.add("1: Pythagoras Theorem");
        elementList.add("2: Volume Formula ");
        elementList.add("3: Distance Formula");
        elementList.add("4: Slope of a line");

        ArrayList<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.pythagoras);
        imageList.add(R.drawable.volume);
        imageList.add(R.drawable.distance);
        imageList.add(R.drawable.slope);

        ArrayAdapter elList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elementList);
        lv.setAdapter(elList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"formula " + (i+1) + " was clicked", Toast.LENGTH_SHORT).show();
                listImage.setImageResource(imageList.get(i));
            }

        });
    };
}
