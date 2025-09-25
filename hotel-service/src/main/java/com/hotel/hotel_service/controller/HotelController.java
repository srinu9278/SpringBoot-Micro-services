package com.hotel.hotel_service.controller;

import com.hotel.hotel_service.dto.HotelListResponseDto;
import com.hotel.hotel_service.dto.HotelRequestDto;
import com.hotel.hotel_service.dto.HotelResponseDto;
import com.hotel.hotel_service.dto.UpdateHotelRequest;
import com.hotel.hotel_service.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/hotel")
@CrossOrigin("http://localhost:5173/")
public class HotelController {
    
    @Autowired
    private HotelService hotelService;

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HotelResponseDto> createHotel(
            @RequestBody @Validated HotelRequestDto hotelRequestDto) {
        return ResponseEntity.ok(hotelService.createHotel(hotelRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDto> getHotelById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<HotelListResponseDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<HotelListResponseDto>> getHotelsByCity(@PathVariable("city") String city) {
        return ResponseEntity.ok(hotelService.getHotelsByCity(city));
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<HotelListResponseDto>> getHotelsByCountry(@PathVariable("country") String country) {
        return ResponseEntity.ok(hotelService.getHotelsByCountry(country));
    }

    @GetMapping("/star-rating/{starRating}")
    public ResponseEntity<List<HotelListResponseDto>> getHotelsByStarRating(@PathVariable("starRating") Integer starRating) {
        return ResponseEntity.ok(hotelService.getHotelsByStarRating(starRating));
    }

    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<HotelListResponseDto>> getHotelsByRating(@PathVariable("minRating") Double minRating) {
        return ResponseEntity.ok(hotelService.getHotelsByRating(minRating));
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelListResponseDto>> searchHotelsByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(hotelService.searchHotelsByName(name));
    }

    @PutMapping(value = "/update/{id}",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HotelResponseDto> updateHotel(@PathVariable("id") Long id, @RequestBody @Validated UpdateHotelRequest updateRequest) {
        return ResponseEntity.ok(hotelService.updateHotel(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHotel(@PathVariable("id") Long id) {
        return ResponseEntity.ok(hotelService.deleteHotel(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateHotelStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        return ResponseEntity.ok(hotelService.updateHotelStatus(id, status));
    }

    @GetMapping("/count/active")
    public ResponseEntity<Map<String, Long>> getTotalActiveHotels() {
        Long count = hotelService.getTotalActiveHotels();
        Map<String, Long> response = Map.of("totalActiveHotels", count);
        return ResponseEntity.ok(response);
    }
}
