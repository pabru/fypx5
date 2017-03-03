package com.pandruszkow.fypx5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pandruszkow.fypx5.protocol.message.ChatMessage;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

public class NoticeArrayAdapter extends ArrayAdapter<ChatMessage> {
    private final Context context;
    private final List<ChatMessage> values;
    // private static final Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Format dateFormatter = new SimpleDateFormat("dd/MM/yyyy");


    public NoticeArrayAdapter(Context context, List<ChatMessage> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notice_row_layout, parent, false);

        ChatMessage item = values.get(position);

        TextView metadata_text = (TextView) rowView.findViewById(R.id.noticeMetadata_text);
        TextView body_text = (TextView) rowView.findViewById(R.id.noticeBody_text);

        metadata_text.setText("Author: "+item.author + ", Date: "+dateFormatter.format(item.created));
        body_text.setText(item.body);

        return rowView;
    }
}