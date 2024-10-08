package com.example.demo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.demo.model.EndUser;
import com.example.demo.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EventRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public List<Event> findByClubEmail(String clubEmail) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":clubEmail", new AttributeValue().withS(clubEmail));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("clubEmail = :clubEmail")
                .withExpressionAttributeValues(expressionAttributeValues);

        return dynamoDBMapper.scan(Event.class, scanExpression);
    }


    public Event findEventByEventId(String eventId) {

        return dynamoDBMapper.load(Event.class, eventId);
    }

    public List<Event> findPendingApprovalEvents() {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":approved", new AttributeValue().withS("pending"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("approved = :approved")
                .withExpressionAttributeValues(expressionAttributeValues);

        return dynamoDBMapper.scan(Event.class, scanExpression);
    }

    public List<Event> findApprovedEvents() {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":approved", new AttributeValue().withS("approved"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("approved = :approved")
                .withExpressionAttributeValues(expressionAttributeValues);

        return dynamoDBMapper.scan(Event.class, scanExpression);
    }

    public void deleteEvent(Event event) {
        dynamoDBMapper.delete(event);
    }

    public List<Event> findRegisteredEvent(String endUserEmail) {
//        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
//        expressionAttributeValues.put(":endUserEmail", new AttributeValue().withS(endUserEmail));
//
//        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
//                .withFilterExpression("contains(registeredUser, :endUserEmail)")
//                .withExpressionAttributeValues(expressionAttributeValues);
//
//        return dynamoDBMapper.scan(Event.class, scanExpression);

        // change due to storing event id in user registered events list
        EndUser endUser = dynamoDBMapper.load(EndUser.class, endUserEmail);

        List<String> registeredEventIds = endUser.getRegisteredEvent();

        List<Event> registeredEvents = new ArrayList<>();
        if (registeredEventIds != null && !registeredEventIds.isEmpty()) {
            for (String eventId : registeredEventIds) {
                Event event = dynamoDBMapper.load(Event.class, eventId);
                if (event != null) {
                    registeredEvents.add(event);
                }
            }
        }

        return registeredEvents;
    }



    public Event save(Event event) {
        dynamoDBMapper.save(event);
        return event;
    }
}
