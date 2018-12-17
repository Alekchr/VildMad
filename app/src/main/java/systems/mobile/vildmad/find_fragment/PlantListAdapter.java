package systems.mobile.vildmad.find_fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import systems.mobile.vildmad.R;


public class PlantListAdapter extends BaseAdapter {

    private static final int PLANT_ITEM = 0;
    private static final int HEADER = 1;
    private Context context;
    private ArrayList<Object> plants;

    public PlantListAdapter() {
        super();
    }


    public PlantListAdapter(@NonNull Context context, ArrayList<Object> plants) {
        this.plants = plants;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position){
        if(plants.get(position) instanceof PlantItem){
            return PLANT_ITEM;

        }
        else{
            return HEADER;
        }
    }

    @Override
    public int getCount() {
        return plants.size();
    }

    @Override
    public Object getItem(int position) {
        return plants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position){
        return (plants.get(position) instanceof PlantItem);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);

        switch(getItemViewType(position)){
            case PLANT_ITEM:
                convertView = inflater.inflate(R.layout.layout_plant_view, parent, false);
                TextView twPlantName = convertView.findViewById(R.id.plantName);
                CheckBox checkbox = convertView.findViewById(R.id.itemCheckbox);
                twPlantName.setText(((PlantItem)plants.get(position)).getplantName());
                checkbox.setChecked(((PlantItem)plants.get(position)).isChecked());

                break;
            case HEADER:
                convertView = inflater.inflate(R.layout.layout_plant_section, null, false);
                TextView twSectionName = convertView.findViewById(R.id.sectionName);
                twSectionName.setText(plants.get(position).toString());
                break;
        }

        ImageView iwPlantImg = convertView.findViewById(R.id.imagePlant);




        return convertView;
    }

}
