package com.booking.recruitment.hotel.service.impl;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.City;
import com.booking.recruitment.hotel.repository.CityRepository;
import com.booking.recruitment.hotel.service.CityService;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Service
@CacheConfig(cacheNames = {"City"})
class DefaultCityService implements CityService
{
    private static final String CACHE_NAME = "City";

    private final CityRepository cityRepository;
    
    @Autowired
    DefaultCityService (CityRepository cityRepository)
    {
        this.cityRepository = cityRepository;
    }
    
    @Override
    @Cacheable(value=CACHE_NAME, key="#id")
    public City getCityById (Long id)
    {
        System.out.println("Getting from the DB");
        return cityRepository.findById (id).orElseThrow (() -> new ElementNotFoundException ("Could not find city with ID provided"));
    }
    
    @Override
    public List <City> getAllCities ()
    {
        return cityRepository.findAll ();
    }
    
    @Override
    @CachePut(value=CACHE_NAME, key="#city.getId()")
    public City createCity (City city)
    {
        System.out.println("Putting into the DB");
        if (city.getId () != null) {
            throw new BadRequestException ("The ID must not be provided when creating a new City");
        }
        
        return cityRepository.save (city);
    }

    @CacheEvict(value=CACHE_NAME, key="#id")
    public void removeCity(Long id) {
        System.out.println("Deleting from the DB");
        City city = getCityById (id);
        if (city != null) {
            cityRepository.deleteById(id);
        }
    }

    @Override
    public List <City> getNearestCities (final Long cityId , final String sortBy)
    {
        if (!sortBy.equals ("distance")) {
            return null;
        }
        List <City> citys = cityRepository.findAll ();
        City nearToThisCity = getCityById (cityId);
        
        PriorityQueue <CityPair> minHeap = new PriorityQueue <> (Comparator.comparingDouble (o -> (int) o.dist));
        //Haversine formula
        
        List <PointsPair> points = new ArrayList <> ();
        //Get all the point for given city
        for (City city : citys) {
            Coordinate lat = Coordinate.fromDegrees (city.getCityCentreLatitude ());
            Coordinate lng = Coordinate.fromDegrees (city.getCityCentreLatitude ());
            points.add (new PointsPair (Point.at (lat , lng) , city));
        }
        
        //Get the points for input city
        Coordinate lat = Coordinate.fromDegrees (nearToThisCity.getCityCentreLatitude ());
        Coordinate lng = Coordinate.fromDegrees (nearToThisCity.getCityCentreLatitude ());
        
        Point point = Point.at (lat , lng);
        for (PointsPair pointsPair : points) {
            double distance = EarthCalc.haversine.distance (pointsPair.points , point);
            minHeap.add (new CityPair (distance , pointsPair.city));
        }
        
        List <City> res = new ArrayList <> ();
        int i = 0;
        while (!minHeap.isEmpty ()) {
            if (i > 3) {
                break;
            }
            res.add (minHeap.poll ().city);
            i++;
        }
        return res;
    }
}
