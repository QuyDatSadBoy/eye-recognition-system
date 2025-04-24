package com.example.eyerecognitionsystem.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "tblEyeRecognitionSample")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EyeRecognitionSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "eyeImageLink", length = 255, nullable = false)
    private String eyeImageLink;

    @Column(name = "isActive")
    private Integer isActive = 1;

    @Column(name = "captureDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date captureDate = new Date();

    @Column(name = "EyeRecognitionSampleTrainId")
    private Integer eyeRecognitionSampleTrainId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tblMemberId")
    private Member member;
}