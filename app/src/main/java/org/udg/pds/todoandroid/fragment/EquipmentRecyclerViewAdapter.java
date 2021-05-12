package org.udg.pds.todoandroid.fragment;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.DictionaryImages;
import org.udg.pds.todoandroid.entity.Equipment;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.fragment.dummy.DummyContent.DummyItem;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EquipmentRecyclerViewAdapter extends RecyclerView.Adapter<EquipmentRecyclerViewAdapter.EquipmentViewHolder> {

    private final Context context;
    private List<Equipment> mValues = new ArrayList<>();
    TodoApi mTodoService;
    //private DictionaryImages dictionaryImages = new DictionaryImages();
    private OnEquipmentListener mOnEquipmentListener;

    /*public EquipmentRecyclerViewAdapter(List<Equipment> items) {
        mValues = items;
    }*/


    public EquipmentRecyclerViewAdapter(Context context, TodoApi todoService) {
        this.context = context;
        mTodoService = todoService;
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
        //holder.mId.setText(mValues.get(position).id.toString());
        String imageUrl = mValues.get(position).imageUrl;
        if(imageUrl!=null) {
            Picasso.get().load(imageUrl).fit().centerCrop().into(holder.mImage);
        }
        else {
            holder.mImage.setImageResource(R.drawable.profile_photo);
        }

        holder.mImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Uri uri = Uri.parse(mValues.get(position).shopUrl);
                 context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
             }
         }
        );


        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(mValues.get(position).id, position);
                /*
                if(onEquipmentListener != null){
                    if(position != RecyclerView.NO_POSITION){

                    }
                }*/
            }
        });
        //String description = mValues.get(position).description;
        //holder.mContentView.setText(mValues.get(position).content);
    }

    public void onDeleteClick(Long id, int position){

        Call<String> call = mTodoService.deleteEquipment(id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Equipment Deleted!", Toast.LENGTH_LONG).show();
                    removeAt(position);
                    Log.i(TAG, "onResponse: be");
                } else {
                    //Toast.makeText(EquipmentFragment.this.getContext(), "Error deleting the equipment", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onResponse: malament");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //Toast.makeText(EquipmentFragment.this.getContext(), "Error making call to delete the equipment", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure: malament");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class EquipmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mName;
        public final TextView mDescription;
        public TextView mId;
        public ImageView mDelete;
        public ImageView mImage;
        public Equipment mItem;
        OnEquipmentListener onEquipmentListener;
        public EquipmentViewHolder(View view, OnEquipmentListener onEquipmentListener) {
            super(view);
            mView = view;
            mDelete = (ImageView) view.findViewById(R.id.icon_delete);
            mName = (TextView) view.findViewById(R.id.name);
            mDescription = (TextView) view.findViewById(R.id.description);
            mId = (TextView) view.findViewById(R.id.id);
            mImage = view.findViewById(R.id.icon);
            this.onEquipmentListener = onEquipmentListener;

            view.setOnClickListener(this);


        }

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

    public void removeAt(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mValues.size());
    }

}
