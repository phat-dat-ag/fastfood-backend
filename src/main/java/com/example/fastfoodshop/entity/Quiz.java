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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quizzes")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class Quiz extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "started_at", nullable = false)
    LocalDateTime startedAt;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @Column(name = "feedback", columnDefinition = "TEXT")
    String feedback;

    @Column(name = "feedback_at")
    LocalDateTime feedbackAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_difficulty_id", nullable = false)
    TopicDifficulty topicDifficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    Promotion promotion;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<QuizQuestion> quizQuestions = new ArrayList<>();
}
