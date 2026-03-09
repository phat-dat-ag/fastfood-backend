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
@Table(name = "topic_difficulties")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants(innerTypeName = "Field")
public class TopicDifficulty extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", length = 60, nullable = false)
    String name;

    @Column(name = "slug", nullable = false, unique = true)
    String slug;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    String description;

    @Column(name = "duration", nullable = false)
    int duration;

    @Column(name = "question_count", nullable = false)
    int questionCount;

    @Column(name = "min_correct_to_reward", nullable = false)
    int minCorrectToReward;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    Topic topic;

    @OneToMany(mappedBy = "topicDifficulty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Quiz> quizzes = new ArrayList<>();

    @OneToMany(mappedBy = "topicDifficulty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Award> awards = new ArrayList<>();

    @OneToMany(mappedBy = "topicDifficulty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Question> questions = new ArrayList<>();
}
