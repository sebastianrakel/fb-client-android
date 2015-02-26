package eu.devunit.fb_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import eu.devunit.fb_client.filebin.HistoryItem;

/**
 * Created by sebastian on 2/26/15.
 */
public class HistoryArrayAdapter extends ArrayAdapter<HistoryItem> {
    private final Context context;
    private final List<HistoryItem> objects;

    public HistoryArrayAdapter(Context context, int resource, List<HistoryItem> objects) {
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

        HistoryItem historyItem = (HistoryItem) objects.get(position);

        id_textView.setText("id: " + historyItem.getId());
        filename_textView.setText(historyItem.getFilename());
        type_textView.setText("type: " + historyItem.getMimetype());
        size_textView.setText("size: " + historyItem.getHumanReadableFilesize());
        date_textView.setText("date: " +  new SimpleDateFormat("yyyy-MM-dd HH:mm").format(historyItem.getHumanReadableDate()));

        return rowView;
    }
}
