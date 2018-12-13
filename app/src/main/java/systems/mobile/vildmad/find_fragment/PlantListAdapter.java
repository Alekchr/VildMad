package systems.mobile.vildmad.find_fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import systems.mobile.vildmad.R;


public class PlantListAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;
    private List<String> plants;


    public PlantListAdapter(@NonNull Context context, int resource, List<String> plants) {
        super(context, resource, plants);
        this.plants = plants;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String plantName;
        plantName = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(this.resource, parent, false);

        TextView twPlantName = convertView.findViewById(R.id.plantName);

        ImageView iwPlantImg = convertView.findViewById(R.id.imagePlant);


        twPlantName.setText(plantName);

        return convertView;
    }

    public static boolean isNegative(double d) {
        return Double.compare(d, 0.0) < 0;
    }
}
