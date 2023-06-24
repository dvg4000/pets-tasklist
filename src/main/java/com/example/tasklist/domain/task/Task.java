package com.example.tasklist.domain.task;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "tasks")
@Data
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    private LocalDateTime expirationDate;

    @Column(name = "image")
    @CollectionTable(name = "tasks_images")
    @ElementCollection
    private List<String> images;

    @Nullable
    public Long expirationDateEpochSecond() {
        return Optional.ofNullable(expirationDate)
                .map(date -> date.atZone(ZoneId.systemDefault()).toEpochSecond())
                .orElse(null);
    }
}
