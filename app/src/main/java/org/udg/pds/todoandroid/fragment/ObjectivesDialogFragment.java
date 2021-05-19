package org.udg.pds.todoandroid.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.rest.TodoApi;



public class ObjectivesDialogFragment extends DialogFragment {

    public interface OnSetTitleListener{
        void setTitle(String title);
    }

    OnSetTitleListener listener;

    String [] objectiveTypes = new String[] {"Duration", "Distance", "Workouts"};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Objective Type")
            .setItems(objectiveTypes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    listener.setTitle((String) objectiveTypes[which]);
                }
            });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnSetTitleListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                activity.toString() +
                    " no implementó OnSetTitleListener");

        }
    }
}
