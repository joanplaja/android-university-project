package org.udg.pds.todoandroid.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id", scope = Equipment.class)
public class Equipment {
    public Long id;
    public String name;
    public String description;
    public String imageUrl;
    public String shopUrl;
}
