package com.schooltech.sms.controller.circulars;


import com.schooltech.sms.entity.client.circulars.BusCircular;
import com.schooltech.sms.service.circular.BusCircularService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BusCircularController {
    @Autowired
    private BusCircularService busCircularService;

    @GetMapping("/getBusCirculars")
    public ResponseEntity<List<BusCircular>> getBusCirculars() {
        return busCircularService.getBusCirculars();
    }

    @GetMapping("/getBusCircularsBySession")
    public ResponseEntity<List<BusCircular>> getBusCircularsBySession(@RequestParam String session) {
        return busCircularService.getBusCircularsBySession(session);
    }

    @GetMapping("/getBusCirculars/{route}")
    public ResponseEntity<BusCircular> getBusCircularsByClassName(@PathVariable String route) {
        return busCircularService.getBusCircularsByClassName(route);
    }

    @PostMapping("/saveBusCirculars")
    @Transactional
    public ResponseEntity<String> saveBusCirculars(@RequestBody List<BusCircular> busCirculars) {
        return busCircularService.saveBusCirculars(busCirculars);
    }

    @PutMapping("/updateBusCirculars")
    @Transactional
    public ResponseEntity<String> updateBusCirculars(@RequestBody List<BusCircular> busCirculars) {
        ResponseEntity<String> response = busCircularService.updateBusCirculars(busCirculars);
        busCircularService.updateAssociatedFees(busCirculars);
        return response;
    }

    @DeleteMapping("/deleteBusCirculars/{busRoute}")
    public ResponseEntity<String> deleteBusCirculars(@PathVariable String busRoute) {
        return busCircularService.deleteBusCirculars(busRoute);
    }
}
