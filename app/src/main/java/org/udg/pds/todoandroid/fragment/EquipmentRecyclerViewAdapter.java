package org.udg.pds.todoandroid.fragment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.DictionaryImages;
import org.udg.pds.todoandroid.entity.Equipment;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.fragment.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EquipmentRecyclerViewAdapter extends RecyclerView.Adapter<EquipmentRecyclerViewAdapter.EquipmentViewHolder> {

    private List<Equipment> mValues;
    private DictionaryImages dictionaryImages = new DictionaryImages();

    public EquipmentRecyclerViewAdapter(List<Equipment> items) {
        mValues = items;
    }

    public EquipmentRecyclerViewAdapter() {

    }

    @Override
    public EquipmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_equipment, parent, false);
        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EquipmentViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mId.setText(mValues.get(position).id.toString());
        holder.mName.setText(mValues.get(position).name.toUpperCase());
        String name = mValues.get(position).name;
        holder.mImage.setImageResource(dictionaryImages.images.get(name));
        //holder.mContentView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        mValues = new ArrayList<>();
        return mValues.size();
    }

    public class EquipmentViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mId;
        public final TextView mName;
        public final ImageView mImage;
        public Equipment mItem;

        public EquipmentViewHolder(View view) {
            super(view);
            mView = view;
            mId = (TextView) view.findViewById(R.id.id);
            mName = (TextView) view.findViewById(R.id.name);
            mImage = (ImageView) view.findViewById(R.id.icon);
            //mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
