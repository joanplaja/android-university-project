package org.udg.pds.todoandroid.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Date;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "messageId", scope = Message.class)
public class Message {

    public Long messageId;
    public Date date;
    public String text;
    public Long userId;
    public String participantName;
    public String participantImageUrl;
}
