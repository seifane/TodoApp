package ch.idoucha.todolist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import ch.idoucha.todolist.model.Item;

/**
 * Created by Seïfane Idouchach on 1/29/2018.
 */

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    private List<Item> mData;
    private Context mContext;

    public HomeListAdapter(Context mContext) {
        mData = new ArrayList<>();
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_home, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = mData.get(position);
        holder.mTitle.setText(item.title);
        holder.mContent.setText(item.content);
        holder.mDate.setText(item.getDateString());
        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AddActivity.class);
                i.putExtra("ID", item.getId());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setList(List<Item> data) {
        mData = data;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCard;
        public TextView mTitle;
        public TextView mContent;
        public TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title_text);
            mContent = (TextView) itemView.findViewById(R.id.content_text);
            mDate = (TextView) itemView.findViewById(R.id.date_text);
            mCard = (CardView) itemView.findViewById(R.id.card_view);
        }
    }



}
