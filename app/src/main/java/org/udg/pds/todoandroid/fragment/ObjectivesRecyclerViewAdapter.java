package org.udg.pds.todoandroid.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.DictionaryImages;
import org.udg.pds.todoandroid.entity.Objective;
import org.udg.pds.todoandroid.entity.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Objective}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ObjectivesRecyclerViewAdapter extends RecyclerView.Adapter<ObjectivesRecyclerViewAdapter.ObjectiveViewHolder> {

    private List<Objective> mValues = new ArrayList<>();
    private OnObjectiveListener mOnObjectiveListener;
    private DictionaryImages dictionaryImages = new DictionaryImages();

    public ObjectivesRecyclerViewAdapter(ObjectivesRecyclerViewAdapter.OnObjectiveListener onObjectiveListener) {
        this.mOnObjectiveListener = onObjectiveListener;
    }


    @Override
    public ObjectiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_objectives, parent, false);
        return new ObjectiveViewHolder(view, mOnObjectiveListener);
    }

    @Override
    public void onBindViewHolder(final ObjectivesRecyclerViewAdapter.ObjectiveViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mId.setText(mValues.get(position).id.toString());
        holder.mType.setText(mValues.get(position).type.toUpperCase());
        String type = mValues.get(position).type;
        //holder.mIcon.setImageResource(dictionaryImages.images.get(type));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /*public void setObjectives(List<Objective> objectives) {
        mValues = objectives;
        notifyDataSetChanged();
    }*/

    public class ObjectiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mId;
        public final TextView mType;
        public final ImageView mIcon;
        public Objective mItem;
        ObjectivesRecyclerViewAdapter.OnObjectiveListener onObjectiveListener;

        public ObjectiveViewHolder(View view, ObjectivesRecyclerViewAdapter.OnObjectiveListener onObjectiveListener) {
            super(view);
            mView = view;
            mId = (TextView) view.findViewById(R.id.id);
            mType = (TextView) view.findViewById(R.id.type);
            mIcon = (ImageView) view.findViewById(R.id.icon);
            this.onObjectiveListener = onObjectiveListener;

            view.setOnClickListener(this);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mType.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            onObjectiveListener.onObjectiveClick(getAdapterPosition());
        }
    }

    public interface OnObjectiveListener {
        void onObjectiveClick(int position);
    }
}
