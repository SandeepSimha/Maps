package com.bbva.my.bbvacompasaplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bbva.my.bbvacompasaplication.model.BbvaModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sandeep Cherukuri on 10/27/17.
 */

public class MapListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps_list);

        List<String> stringList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        ArrayList<BbvaModel> bundleList = bundle.getParcelableArrayList("bundleList");
        assert bundleList != null;
        for (BbvaModel bbvaModel : bundleList) {
            stringList.add(bbvaModel.getName());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_layout, stringList);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

    }
}
