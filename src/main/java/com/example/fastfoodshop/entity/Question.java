package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseAuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class Question extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    String content;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "image_public_id")
    String imagePublicId;

    @Column(name = "audio_url")
    String audioUrl;

    @Column(name = "audio_public_id")
    String audioPublicId;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_difficulty_id", nullable = false)
    TopicDifficulty topicDifficulty;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<QuizQuestion> quizQuestions = new ArrayList<>();
}
