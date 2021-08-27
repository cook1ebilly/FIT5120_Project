package database;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fit5120_project.R;

import java.util.List;

public class ContactAdapter extends BaseAdapter {
    List<contact> list;
    private LayoutInflater inflater;

    public ContactAdapter(List<contact> list, Context context) {
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
        ContactAdapter.ViewHolder viewHolder;
        if (convertView == null) {

                convertView = inflater.inflate(R.layout.listview_item2, parent, false);




                viewHolder = new ContactAdapter.ViewHolder();
                 viewHolder.phoneNumber=convertView.findViewById(R.id.tv_item_phoneNumber);
                 viewHolder.delete=convertView.findViewById(R.id.btn_item_delete);

            viewHolder.name = convertView.findViewById(R.id.tv_item_name);
                convertView.setTag(viewHolder);
            } else{
                viewHolder = (ContactAdapter.ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(list.get(position).getName());
        viewHolder.phoneNumber.setText(list.get(position).getPhoneNumber());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });




        return convertView;
        }

    class ViewHolder {
        TextView name;
        Button delete;
        TextView phoneNumber;




    }
}
