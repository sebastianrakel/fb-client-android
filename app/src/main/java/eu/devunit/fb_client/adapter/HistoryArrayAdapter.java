package eu.devunit.fb_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import eu.devunit.fb_client.R;
import eu.devunit.fb_client.filebin.History.FlatHistoryItem;
import eu.devunit.fb_client.filebin.History.HistoryItem;

/**
 * Created by sebastian on 2/26/15.
 */
public class HistoryArrayAdapter extends ArrayAdapter<FlatHistoryItem> {
    private final Context context;
    private final List<FlatHistoryItem> objects;

    public HistoryArrayAdapter(Context context, int resource, List<FlatHistoryItem> objects) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.history_listview_item, parent, false);

        TextView id_textView = (TextView) rowView.findViewById(R.id.id);
        TextView filename_textView = (TextView) rowView.findViewById(R.id.filename);
        TextView type_textView = (TextView) rowView.findViewById(R.id.mimetype);
        TextView date_textView = (TextView) rowView.findViewById(R.id.date);
        TextView size_textView = (TextView) rowView.findViewById(R.id.filesize);

        FlatHistoryItem historyItem = (FlatHistoryItem) objects.get(position);

        id_textView.setText(historyItem.getId());
        filename_textView.setText(historyItem.getFilename());
        type_textView.setText(historyItem.getMimetype());
        size_textView.setText(historyItem.getHumanReadableFilesize());
        date_textView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(historyItem.getHumanReadableDate()));

        return rowView;
    }
}
