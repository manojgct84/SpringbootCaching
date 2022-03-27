package com.booking.recruitment.hotel.controller;

import com.booking.recruitment.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping (value = "/hotel", method = RequestMethod.DELETE)
public class DeleteHotelController
{
    
    private final HotelService hotelService;
    
    @Autowired
    public DeleteHotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }
    
    @RequestMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean getAllHotels(@PathVariable ("id") Long id) {
        return hotelService.removeHotelsById (id);
    }
}
