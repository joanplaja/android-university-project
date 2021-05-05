package org.udg.pds.todoandroid.fragment;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.DictionaryImages;
import org.udg.pds.todoandroid.entity.Equipment;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.fragment.dummy.DummyContent.DummyItem;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EquipmentRecyclerViewAdapter extends RecyclerView.Adapter<EquipmentRecyclerViewAdapter.EquipmentViewHolder> {

    private final Context context;
    private List<Equipment> mValues = new ArrayList<>();
    //private DictionaryImages dictionaryImages = new DictionaryImages();
    private OnEquipmentListener mOnEquipmentListener;

    /*public EquipmentRecyclerViewAdapter(List<Equipment> items) {
        mValues = items;
    }*/

    public EquipmentRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setEquipments(List<Equipment> equipments) {
        mValues = equipments;
        notifyDataSetChanged();
    }

    @Override
    public EquipmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_equipment, parent, false);
        return new EquipmentViewHolder(view,mOnEquipmentListener);
    }

    @Override
    public void onBindViewHolder(final EquipmentViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mName.setText(mValues.get(position).name.toUpperCase());
        holder.mDescription.setText(mValues.get(position).description);
        String imageUrl = mValues.get(position).imageUrl;
        if(imageUrl!=null)
            //holder.mImage.setImageURI(Uri.parse(imageUrl));
            Picasso.get().load(imageUrl).fit().centerCrop().into(holder.mImage);
        else
            holder.mImage.setImageResource(R.drawable.profile_photo);

        holder.mImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Uri uri = Uri.parse(mValues.get(position).shopUrl);
                 context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
             }
         }
        );
        //String description = mValues.get(position).description;
        //holder.mContentView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class EquipmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        //public final TextView mId;
        public final TextView mName;
        public final TextView mDescription;
        //public String mImageUrl;
        public ImageView mImage; //String amb una URL, TextView com en update
        public Equipment mItem;
        OnEquipmentListener onEquipmentListener;
        public EquipmentViewHolder(View view, OnEquipmentListener onEquipmentListener) {
            super(view);
            mView = view;
            //mId = (TextView) view.findViewById(R.id.id);
            mName = (TextView) view.findViewById(R.id.name);
            mDescription = (TextView) view.findViewById(R.id.description);
            mImage = view.findViewById(R.id.icon);
            this.onEquipmentListener = onEquipmentListener;

            view.setOnClickListener(this);
            }

            /**/
            //mContentView = (TextView) view.findViewById(R.id.content);

            @Override
            public String toString() {
                return super.toString() + " '" + mName.getText() + "'"+ " '" + mDescription.getText() + "'";
            }

            @Override
            public void onClick(View v) { onEquipmentListener.onEquipmentClick(getAdapterPosition()); }
        }


    public interface OnEquipmentListener {
        void onEquipmentClick(int position);
    }


}
