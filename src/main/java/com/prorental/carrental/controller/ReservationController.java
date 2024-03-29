package com.prorental.carrental.controller;



import com.prorental.carrental.domain.Car;
import com.prorental.carrental.domain.Reservation;
import com.prorental.carrental.dto.ReservationDTO;
import com.prorental.carrental.service.ReservationService;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Produces;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@RequestMapping("/reservations")
public class ReservationController {

    public ReservationService reservationService;

    //An example of RequestParam is /reservations/admin/auth/all?id=1&userId=123
    @GetMapping("/admin/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getAllUserReservations(@RequestParam(value = "id") Long id,
                                                                       @RequestParam(value = "userId") Long userId){
        List<ReservationDTO> reservations = reservationService.fetchUserReservationsById(id, userId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations(){
        List<ReservationDTO> reservationDTOS =  reservationService.fetchAllReservations();
        return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
    }


@GetMapping("{id}/admin")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id){
        ReservationDTO reservationDTO = reservationService.getReservationById(id);
        return ResponseEntity.ok().body(reservationDTO);
}

@GetMapping("/{id}/auth")
@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
public ResponseEntity<ReservationDTO> getUserReservationById(@PathVariable Long id,
                                                             HttpServletRequest request){// HTTP request made by a client to a server.
    Long userId = (Long) request.getAttribute("id");
    ReservationDTO reservation = reservationService.findByUserId(id, userId);
    return new ResponseEntity<>(reservation, HttpStatus.OK);
}

@GetMapping("/auth/all")
@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
public ResponseEntity<List<ReservationDTO>> getUserReservationsById(HttpServletRequest request){
        Long userId =(Long) request.getAttribute("id");
        List<ReservationDTO> reservationDTOList = reservationService.findAllByUserId(userId);
        return new ResponseEntity<>(reservationDTOList, HttpStatus.OK);
}

@PostMapping("/add")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public ResponseEntity<Map<String, Boolean>> makeReservation(HttpServletRequest request, @RequestParam(value="carId") Car carId, @Valid @RequestBody Reservation reservation){
    Long userId = (Long) request.getAttribute("id");
    reservationService.addReservation(reservation,userId, carId);
    Map<String, Boolean> map = new HashMap<>();
    map.put("Reservation Added Successfully", true);
    return new ResponseEntity<>(map, HttpStatus.CREATED);
}

@PostMapping("/add/auth")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Boolean>> addReservation(@RequestParam(value = "userId") Long userId,
                                                           @RequestParam(value = "carId") Car carId,
                                                           @Valid @RequestBody Reservation reservation
                                                           ){
        reservationService.addReservation(reservation, userId, carId);
    Map<String, Boolean> map = new HashMap<>();
    map.put("Reservation added successfully!", true);
    return new ResponseEntity<>(map, HttpStatus.CREATED);
}

@PutMapping("/admin/auth")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Boolean>> updateReservation(@RequestParam(value = "carId") Car carId,
                                                              @RequestParam(value = "reservationId") Long id,
                                                              @Valid @RequestBody Reservation reservation
                                                              ){
        reservationService.updateReservation(carId, id, reservation);
    Map<String, Boolean> map = new HashMap<>();
    map.put("success", true);
    return new ResponseEntity<>(map, HttpStatus.OK);
}

    @DeleteMapping("/admin/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteReservation(@PathVariable Long id){
        reservationService.removeById(id);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


//    TODO Request Params don't look good. Improve them
    @GetMapping("/auth")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkCarAvailability(
            @RequestParam (value = "carId") Long carId,
            @RequestParam (value = "pickUpDateTime")
            @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss")
                    LocalDateTime pickUpTime,
            @RequestParam (value = "dropOffDateTime")
            @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss")
                    LocalDateTime dropOffTime ){

        boolean availability = reservationService.carAvailability(carId, pickUpTime, dropOffTime);
        Double totalPrice = reservationService.totalPrice(pickUpTime, dropOffTime, carId);
        Map<String, Object> map = new HashMap<>();
        map.put("isAvailable", !availability);
        map.put("totalPrice", totalPrice);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }



}
