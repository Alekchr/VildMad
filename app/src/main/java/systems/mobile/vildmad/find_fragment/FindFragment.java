package systems.mobile.vildmad.find_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import systems.mobile.vildmad.DatabaseHandler;
import systems.mobile.vildmad.R;

public class FindFragment extends Fragment{

    private static FindFragment fragment;
    BaseAdapter adapter;
    ArrayList<Object> plants;
    private ListView mListView;
    private DatabaseHandler db;

    public FindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FindFragment.
     */
    public static FindFragment getInstance() {
        if(fragment == null)
            fragment = new FindFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("New instance", "A new instance was created");
        plants = new ArrayList<>();
        db = DatabaseHandler.getInstance();
        String[] planttypes = getResources().getStringArray(R.array.planttypes);
        for (String type:planttypes
             ) { plants.add(type);

             switch(type){
            case "Svampe":
                for (String svamp:getResources().getStringArray(R.array.svampe)
                     ) {plants.add(new PlantItem(svamp, "x"));
                }
                break;

            case "Frugter":
                for (String frugt:getResources().getStringArray(R.array.frugter)
                        ) {plants.add(new PlantItem(frugt, ""));
                }
                break;

            case "Krydderurter":
                for (String krydderurter:getResources().getStringArray(R.array.krydderurter)
                        ) {plants.add(new PlantItem(krydderurter, ""));
                }
                break;

            case "Bær":
                for (String bær:getResources().getStringArray(R.array.baer)
                        ) {plants.add(new PlantItem(bær, ""));
                }
                break;

            case "Nødder":
                for (String nødder:getResources().getStringArray(R.array.nodder)
                        ) {plants.add(new PlantItem(nødder, ""));
                }
                break;

            }
        }

        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_find, container, false);
        mListView = view.findViewById(R.id.plantListView);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new PlantListAdapter(getActivity(), plants);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> AV, View v, int pos,
                                    long id) {

                // Get user selected item.
                Object itemObject = AV.getAdapter().getItem(pos);

                // Translate the selected item to DTO object.
                PlantItem item = (PlantItem) itemObject;

                // Get the checkbox.
                CheckBox itemCheckbox = (CheckBox) v.findViewById(R.id.itemCheckbox);

                // Reverse the checkbox and clicked item check state.
                if(item.isChecked())
                {
                    itemCheckbox.setChecked(false);
                    item.setChecked(false);
                    db.removeMarkerByPlant(((PlantItem)plants.get(pos)).getplantName());
                }else
                {
                    itemCheckbox.setChecked(true);
                    item.setChecked(true);
                    db.returnMarkerByPlant(((PlantItem)plants.get(pos)).getplantName());
                }

            }

        });


        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onPause() {
        super.onPause();

    }

}