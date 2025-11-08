package com.example.fastfoodshop.entity;

import com.example.fastfoodshop.entity.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "topic_difficulties")
public class TopicDifficulty extends BaseAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "question_count", nullable = false)
    private int questionCount;

    @Column(name = "min_correct_to_reward", nullable = false)
    private int minCorrectToReward;

    @Column(name = "is_activated", nullable = false)
    private boolean isActivated;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @OneToMany(mappedBy = "topicDifficulty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Quiz> quizzes = new ArrayList<>();

    @OneToMany(mappedBy = "topicDifficulty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Award> awards = new ArrayList<>();

    @OneToMany(mappedBy = "topicDifficulty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();
}
