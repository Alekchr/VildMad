package systems.mobile.vildmad.find_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import systems.mobile.vildmad.R;

public class FindFragment extends Fragment{


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    BaseAdapter adapter;
    ArrayList<Object> plants = new ArrayList<>();
    private ListView mListView;

    public FindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        adapter = new PlantListAdapter(getActivity(), plants);
        mListView.setAdapter(adapter);
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