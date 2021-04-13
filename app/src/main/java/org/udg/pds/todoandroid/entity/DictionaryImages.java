package org.udg.pds.todoandroid.entity;

import org.udg.pds.todoandroid.R;

import java.util.HashMap;

public class DictionaryImages {
    public HashMap<String, Integer> images = new HashMap<>();

    public DictionaryImages() {
        images.put("running", R.drawable.running);
        images.put("cycling", R.drawable.cycling);
        images.put("hiking", R.drawable.hiking);
        images.put("walking", R.drawable.walking);
    }
}
