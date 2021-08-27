package database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fit5120_project.R;

import java.util.List;

public class SendAdapter extends BaseAdapter {

    List<contact> list;
    private LayoutInflater inflater;

    public SendAdapter(List<contact> list, Context context) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null)
            return list.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendAdapter.ViewHolder viewHolder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.send_item, parent, false);




            viewHolder = new SendAdapter.ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.sname);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (SendAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(list.get(position).getName());





        return convertView;
    }

    class ViewHolder {
        TextView name;




    }
}
