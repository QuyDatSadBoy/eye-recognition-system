package com.example.eyerecognitionsystem.service;


import com.example.eyerecognitionsystem.entity.EyeRecognitionSample;
import com.example.eyerecognitionsystem.entity.Member;
import com.example.eyerecognitionsystem.repository.EyeRecognitionSampleRepository;
import com.example.eyerecognitionsystem.repository.MemberRepository;
import com.example.eyerecognitionsystem.util.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EyeRecognitionSampleService {

    @Autowired
    private EyeRecognitionSampleRepository eyeRecognitionSampleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<EyeRecognitionSample> getAllSamples() {
        return eyeRecognitionSampleRepository.findAll();
    }

    public EyeRecognitionSample getSampleById(Integer id) {
        return eyeRecognitionSampleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy mẫu nhận dạng với ID: " + id));
    }

    public List<EyeRecognitionSample> getSamplesByMemberId(Integer memberId) {
        return eyeRecognitionSampleRepository.findByMemberId(memberId);
    }

    @Transactional
    public EyeRecognitionSample createSample(Integer memberId, String eyeImageLink) {
        // Tìm Member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + memberId));

        // Tạo mẫu nhận dạng mới
        EyeRecognitionSample sample = new EyeRecognitionSample();
        sample.setEyeImageLink(eyeImageLink);
        sample.setMember(member);
        sample.setIsActive(1);
        sample.setCaptureDate(new Date());

        return eyeRecognitionSampleRepository.save(sample);
    }

    @Transactional
    public EyeRecognitionSample createSampleFromFaceImage(Integer memberId, MultipartFile faceImage) {
        // Gọi đến API xử lý ảnh để cắt mống mắt (mock)
        // Trong thực tế, đây sẽ là một HTTP request đến service xử lý ảnh

        // Lưu ảnh khuôn mặt
        String faceImagePath = fileStorageService.storeFile(faceImage, "faces");

        // Giả lập việc đã xử lý và trích xuất ảnh mống mắt
        // Trong thực tế, đây sẽ là kết quả từ service xử lý ảnh
        String eyeImageLink = "/uploads/eyes/sample_" + System.currentTimeMillis() + ".jpg";

        return createSample(memberId, eyeImageLink);
    }

    @Transactional
    public EyeRecognitionSample updateSample(Integer id, EyeRecognitionSample sampleDetails) {
        EyeRecognitionSample existingSample = eyeRecognitionSampleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy mẫu nhận dạng với ID: " + id));

        if (sampleDetails.getEyeImageLink() != null) {
            existingSample.setEyeImageLink(sampleDetails.getEyeImageLink());
        }

        if (sampleDetails.getIsActive() != null) {
            existingSample.setIsActive(sampleDetails.getIsActive());
        }

        return eyeRecognitionSampleRepository.save(existingSample);
    }

    @Transactional
    public void deleteSample(Integer id) {
        EyeRecognitionSample sample = eyeRecognitionSampleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy mẫu nhận dạng với ID: " + id));

        eyeRecognitionSampleRepository.delete(sample);
    }

    @Transactional
    public void deactivateSample(Integer id) {
        EyeRecognitionSample sample = eyeRecognitionSampleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy mẫu nhận dạng với ID: " + id));

        sample.setIsActive(0);
        eyeRecognitionSampleRepository.save(sample);
    }
}