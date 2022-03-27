package com.booking.recruitment.hotel.service.impl;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.recruitment.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {"Hotel"})
class DefaultHotelService implements HotelService {
    private static final String CACHE_NAME = "Hotel";
  private final HotelRepository hotelRepository;

  @Autowired
  DefaultHotelService(HotelRepository hotelRepository) {
    this.hotelRepository = hotelRepository;
  }

  @Override
  public List<Hotel> getAllHotels() {
    return hotelRepository.findAll();
  }

  @Override
  public List<Hotel> getHotelsByCity(Long cityId) {
    return hotelRepository.findAll().stream()
        .filter((hotel) -> cityId.equals(hotel.getCity().getId()))
        .collect(Collectors.toList());
  }
  
  @Override
  @Cacheable(value=CACHE_NAME, key="#hotelId")
  public Hotel getHotelsById(Long hotelId) {
    System.out.println("Getting from the DB");
    List<Hotel> hotels = hotelRepository.findAll();
     if (hotels !=null) {
         for (Hotel h : hotels) {
            if (h.getId ().equals (hotelId)) {
              return h;
            }
         }
     }
    return  null;
  }
  
  @Override
  @CacheEvict(value=CACHE_NAME, key="#id")
  public boolean removeHotelsById (final Long id)
  {
    System.out.println("Deleting from the DB");
    if (id == null) {
      throw new BadRequestException("The ID must not be provided when deleting the Hotel");
    }
     try {
       hotelRepository.deleteById (id);
     } catch ( Exception e) {
        throw new ElementNotFoundException ("The given hotel is not prevent for deletion");
     }
     return true;
  }
  
  @Override
  @CachePut(value=CACHE_NAME, key="#hotel.getId()")
  public Hotel createNewHotel(Hotel hotel) {
      System.out.println("Putting into the DB");
    if (hotel.getId() != null) {
      throw new BadRequestException("The ID must not be provided when creating a new Hotel");
    }
    return hotelRepository.save(hotel);
  }
}
