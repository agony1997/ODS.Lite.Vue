package com.example.mockodsvue.model.entity.sequence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_sequence")
@IdClass(DocumentSequenceId.class)
public class DocumentSequence {

    @Id
    @Column(name = "sequence_type", length = 10, nullable = false)
    private String sequenceType;

    @Id
    @Column(name = "sequence_date", nullable = false)
    private LocalDate sequenceDate;

    @Column(name = "current_no", nullable = false)
    private int currentNo;

    @Version
    private Long version;
}
