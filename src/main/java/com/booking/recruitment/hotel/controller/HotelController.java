package com.booking.recruitment.hotel.controller;

import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/hotel", method = RequestMethod.GET)
public class HotelController {
    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Hotel createHotel(@RequestBody Hotel hotel) {
        return hotelService.createNewHotel(hotel);
    }

    @RequestMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Hotel getHotelDetails(@PathVariable("id") Long id) {
        return hotelService.getHotelsById(id);
    }
}
