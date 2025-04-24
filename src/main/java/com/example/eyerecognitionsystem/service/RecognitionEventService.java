package com.example.eyerecognitionsystem.service;


import com.example.eyerecognitionsystem.entity.Member;
import com.example.eyerecognitionsystem.entity.RecognitionEvent;
import com.example.eyerecognitionsystem.repository.MemberRepository;
import com.example.eyerecognitionsystem.repository.RecognitionEventRepository;
import com.example.eyerecognitionsystem.util.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RecognitionEventService {

    @Autowired
    private RecognitionEventRepository recognitionEventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<RecognitionEvent> getAllEvents() {
        return recognitionEventRepository.findAll();
    }

    public RecognitionEvent getEventById(Integer id) {
        return recognitionEventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sự kiện nhận dạng với ID: " + id));
    }

    public List<RecognitionEvent> getEventsByMemberId(Integer memberId) {
        return recognitionEventRepository.findByMemberId(memberId);
    }

    public List<RecognitionEvent> getSuccessfulEventsByMemberId(Integer memberId) {
        return recognitionEventRepository.findSuccessfulRecognitionByMemberId(memberId);
    }

    public List<RecognitionEvent> getEventsBetweenDates(Date startDate, Date endDate) {
        return recognitionEventRepository.findByTimeVerifyBetween(startDate, endDate);
    }

    public List<RecognitionEvent> getSuccessfulEventsByMemberIdAndDateRange(Integer memberId, Date startDate, Date endDate) {
        return recognitionEventRepository.findSuccessfulRecognitionByMemberIdAndTimeVerifyBetween(memberId, startDate, endDate);
    }

    public Long countSuccessfulEventsByMemberIdAndDateRange(Integer memberId, Date startDate, Date endDate) {
        return recognitionEventRepository.countSuccessfulRecognitionByMemberIdAndTimeVerifyBetween(memberId, startDate, endDate);
    }

    @Transactional
    public RecognitionEvent createEvent(RecognitionEvent event) {
        // Kiểm tra Member nếu có
        if (event.getMember() != null && event.getMember().getId() != null) {
            Member member = memberRepository.findById(event.getMember().getId())
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + event.getMember().getId()));
            event.setMember(member);
        }

        // Đặt thời gian mặc định là hiện tại nếu không được cung cấp
        if (event.getTimeVerify() == null) {
            event.setTimeVerify(new Date());
        }

        return recognitionEventRepository.save(event);
    }

    @Transactional
    public RecognitionEvent createEventWithImage(Integer memberId, MultipartFile image, String cameraName,
                                                 Integer recognitionModelId, Integer eyeDetectionModelId,
                                                 Boolean isSuccessful, Float accuracy) {
        // Lưu ảnh
        String imageLink = fileStorageService.storeFile(image, "events");

        // Tìm Member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + memberId));

        // Tạo sự kiện nhận dạng mới
        RecognitionEvent event = new RecognitionEvent();
        event.setImageLink(imageLink);
        event.setMember(member);
        event.setCameraName(cameraName);
        event.setRecognitionModelId(recognitionModelId);
        event.setEyeDetectionModelId(eyeDetectionModelId);
        event.setIsSuccessful(isSuccessful ? 1 : 0);
        event.setAccuracy(accuracy);
        event.setTimeVerify(new Date());

        return recognitionEventRepository.save(event);
    }

    @Transactional
    public RecognitionEvent updateEvent(Integer id, RecognitionEvent eventDetails) {
        RecognitionEvent existingEvent = recognitionEventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sự kiện nhận dạng với ID: " + id));

        if (eventDetails.getImageLink() != null) {
            existingEvent.setImageLink(eventDetails.getImageLink());
        }

        if (eventDetails.getCameraName() != null) {
            existingEvent.setCameraName(eventDetails.getCameraName());
        }

        if (eventDetails.getRecognitionModelId() != null) {
            existingEvent.setRecognitionModelId(eventDetails.getRecognitionModelId());
        }

        if (eventDetails.getEyeDetectionModelId() != null) {
            existingEvent.setEyeDetectionModelId(eventDetails.getEyeDetectionModelId());
        }

        if (eventDetails.getIsSuccessful() != null) {
            existingEvent.setIsSuccessful(eventDetails.getIsSuccessful());
        }

        if (eventDetails.getAccuracy() != null) {
            existingEvent.setAccuracy(eventDetails.getAccuracy());
        }

        if (eventDetails.getTimeVerify() != null) {
            existingEvent.setTimeVerify(eventDetails.getTimeVerify());
        }

        if (eventDetails.getMember() != null && eventDetails.getMember().getId() != null) {
            Member member = memberRepository.findById(eventDetails.getMember().getId())
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + eventDetails.getMember().getId()));
            existingEvent.setMember(member);
        }

        return recognitionEventRepository.save(existingEvent);
    }

    @Transactional
    public void deleteEvent(Integer id) {
        RecognitionEvent event = recognitionEventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sự kiện nhận dạng với ID: " + id));

        recognitionEventRepository.delete(event);
    }

    public Map<Integer, Long> getRecognitionStatsByDateRange(Date startDate, Date endDate) {
        List<RecognitionEvent> events = recognitionEventRepository.findByTimeVerifyBetween(startDate, endDate);

        // Nhóm theo memberId và đếm số lần nhận dạng thành công
        Map<Integer, Long> stats = events.stream()
                .filter(e -> e.getIsSuccessful() != null && e.getIsSuccessful() == 1)
                .filter(e -> e.getMember() != null && e.getMember().getId() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getMember().getId(),
                        Collectors.counting()
                ));

        return stats;
    }

    public Map<String, Object> getMemberRecognitionStatsByDateRange(Integer memberId, Date startDate, Date endDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + memberId));

        Long successCount = recognitionEventRepository.countSuccessfulRecognitionByMemberIdAndTimeVerifyBetween(
                memberId, startDate, endDate);

        List<RecognitionEvent> events = recognitionEventRepository.findSuccessfulRecognitionByMemberIdAndTimeVerifyBetween(
                memberId, startDate, endDate);

        Map<String, Object> stats = new HashMap<>();
        stats.put("memberId", memberId);
        stats.put("memberName", member.getFullName() != null ?
                member.getFullName().getFirstName() + " " + member.getFullName().getLastName() : member.getUsername());
        stats.put("department", member.getDepartment());
        stats.put("successCount", successCount);
        stats.put("events", events);

        return stats;
    }
}