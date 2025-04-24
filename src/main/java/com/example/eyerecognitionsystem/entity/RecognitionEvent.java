package com.example.eyerecognitionsystem.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "tblRecognitionEvent")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecognitionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "imageLink", length = 255, nullable = false)
    private String imageLink;

    @Column(name = "recognitionModelId")
    private Integer recognitionModelId;

    @Column(name = "eyeDetectionModelId")
    private Integer eyeDetectionModelId;

    @Column(name = "cameraName", length = 100)
    private String cameraName;

    @Column(name = "timeVerify")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeVerify = new Date();

    @Column(name = "isSuccessful")
    private Integer isSuccessful;

    @Column(name = "accuracy")
    private Float accuracy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tblMemberId")
    private Member member;
}