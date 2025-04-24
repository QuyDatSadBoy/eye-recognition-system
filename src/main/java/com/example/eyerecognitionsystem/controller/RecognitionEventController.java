package com.example.eyerecognitionsystem.controller;


import com.example.eyerecognitionsystem.entity.RecognitionEvent;
import com.example.eyerecognitionsystem.service.RecognitionEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/events")
public class RecognitionEventController {

    @Autowired
    private RecognitionEventService recognitionEventService;

    @GetMapping
    public ResponseEntity<List<RecognitionEvent>> getAllEvents() {
        List<RecognitionEvent> events = recognitionEventService.getAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecognitionEvent> getEventById(@PathVariable Integer id) {
        try {
            RecognitionEvent event = recognitionEventService.getEventById(id);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<RecognitionEvent>> getEventsByMemberId(@PathVariable Integer memberId) {
        List<RecognitionEvent> events = recognitionEventService.getEventsByMemberId(memberId);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/member/{memberId}/successful")
    public ResponseEntity<List<RecognitionEvent>> getSuccessfulEventsByMemberId(@PathVariable Integer memberId) {
        List<RecognitionEvent> events = recognitionEventService.getSuccessfulEventsByMemberId(memberId);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<RecognitionEvent>> getEventsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<RecognitionEvent> events = recognitionEventService.getEventsBetweenDates(startDate, endDate);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/member/{memberId}/date-range")
    public ResponseEntity<List<RecognitionEvent>> getSuccessfulEventsByMemberIdAndDateRange(
            @PathVariable Integer memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<RecognitionEvent> events = recognitionEventService.getSuccessfulEventsByMemberIdAndDateRange(
                memberId, startDate, endDate);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/member/{memberId}/count")
    public ResponseEntity<Map<String, Object>> countSuccessfulEventsByMemberIdAndDateRange(
            @PathVariable Integer memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        Long count = recognitionEventService.countSuccessfulEventsByMemberIdAndDateRange(
                memberId, startDate, endDate);
        Map<String, Object> response = new HashMap<>();
        response.put("memberId", memberId);
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("successCount", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stats/date-range")
    public ResponseEntity<Map<Integer, Long>> getRecognitionStatsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        Map<Integer, Long> stats = recognitionEventService.getRecognitionStatsByDateRange(startDate, endDate);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/stats/member/{memberId}")
    public ResponseEntity<Map<String, Object>> getMemberRecognitionStatsByDateRange(
            @PathVariable Integer memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        Map<String, Object> stats = recognitionEventService.getMemberRecognitionStatsByDateRange(
                memberId, startDate, endDate);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody RecognitionEvent event) {
        try {
            RecognitionEvent createdEvent = recognitionEventService.createEvent(event);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam Integer memberId,
            @RequestParam(required = false) String cameraName,
            @RequestParam(required = false) Integer recognitionModelId,
            @RequestParam(required = false) Integer eyeDetectionModelId,
            @RequestParam(required = false, defaultValue = "false") Boolean isSuccessful,
            @RequestParam(required = false, defaultValue = "0.0") Float accuracy) {
        try {
            RecognitionEvent createdEvent = recognitionEventService.createEventWithImage(
                    memberId, file, cameraName, recognitionModelId, eyeDetectionModelId, isSuccessful, accuracy);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Integer id, @RequestBody RecognitionEvent eventDetails) {
        try {
            RecognitionEvent updatedEvent = recognitionEventService.updateEvent(id, eventDetails);
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer id) {
        try {
            recognitionEventService.deleteEvent(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xóa sự kiện nhận dạng thành công");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}