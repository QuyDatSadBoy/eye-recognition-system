package com.example.eyerecognitionsystem.controller;


import com.example.eyerecognitionsystem.entity.EyeRecognitionSample;
import com.example.eyerecognitionsystem.service.EyeRecognitionSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/samples")
public class EyeRecognitionSampleController {

    @Autowired
    private EyeRecognitionSampleService eyeRecognitionSampleService;

    @GetMapping
    public ResponseEntity<List<EyeRecognitionSample>> getAllSamples() {
        List<EyeRecognitionSample> samples = eyeRecognitionSampleService.getAllSamples();
        return new ResponseEntity<>(samples, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EyeRecognitionSample> getSampleById(@PathVariable Integer id) {
        try {
            EyeRecognitionSample sample = eyeRecognitionSampleService.getSampleById(id);
            return new ResponseEntity<>(sample, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<EyeRecognitionSample>> getSamplesByMemberId(@PathVariable Integer memberId) {
        List<EyeRecognitionSample> samples = eyeRecognitionSampleService.getSamplesByMemberId(memberId);
        return new ResponseEntity<>(samples, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createSample(@RequestBody EyeRecognitionSample sample, @RequestParam Integer memberId) {
        try {
            EyeRecognitionSample createdSample = eyeRecognitionSampleService.createSample(memberId, sample.getEyeImageLink());
            return new ResponseEntity<>(createdSample, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFaceImage(@RequestParam("file") MultipartFile file, @RequestParam Integer memberId) {
        try {
            EyeRecognitionSample createdSample = eyeRecognitionSampleService.createSampleFromFaceImage(memberId, file);
            return new ResponseEntity<>(createdSample, HttpStatus.CREATED);
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
    public ResponseEntity<?> updateSample(@PathVariable Integer id, @RequestBody EyeRecognitionSample sampleDetails) {
        try {
            EyeRecognitionSample updatedSample = eyeRecognitionSampleService.updateSample(id, sampleDetails);
            return new ResponseEntity<>(updatedSample, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSample(@PathVariable Integer id) {
        try {
            eyeRecognitionSampleService.deleteSample(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xóa mẫu nhận dạng thành công");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateSample(@PathVariable Integer id) {
        try {
            eyeRecognitionSampleService.deactivateSample(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã vô hiệu hóa mẫu nhận dạng thành công");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}