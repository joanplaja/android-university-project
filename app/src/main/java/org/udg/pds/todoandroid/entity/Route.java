package org.udg.pds.todoandroid.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by imartin on 12/02/16.
 */
public class Route {
    public Double initialLatitude;
    public Double initialLongitude;
    public List<Point> points = new ArrayList<>();
}

