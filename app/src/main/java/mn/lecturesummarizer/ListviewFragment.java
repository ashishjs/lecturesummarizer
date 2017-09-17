package mn.lecturesummarizer;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static mn.lecturesummarizer.R.id.ListViewSumm;

/**
 * Created by ashishjayan on 9/16/2017.
 */

public class ListviewFragment extends Fragment {
    Toolbar toolbar;
    ListView listview;



    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

      View view =inflater.inflate(R.layout.fragment_listview,container,false);
       // toolbar=(Toolbar)view.findViewById()
        ArrayList<String> data=new ArrayList<>();
            data.add("Hello");
            data.add("door");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,data);
        listview =(ListView)view.findViewById(ListViewSumm);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getActivity(),ViewSummary.class).putExtra("Data",listview.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
        return view;





    }


}
