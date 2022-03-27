package com.booking.recruitment.hotel.service.impl;

import com.booking.recruitment.hotel.model.City;
import com.grum.geocalc.Point;

public class PointsPair
{
    Point points;
    City city;
    
    public PointsPair (Point points , City city)
    {
        this.points = points;
        this.city = city;
    }
    
  
}
