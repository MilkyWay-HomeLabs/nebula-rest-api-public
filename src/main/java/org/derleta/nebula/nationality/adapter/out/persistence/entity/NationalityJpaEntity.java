package org.derleta.nebula.nationality.adapter.out.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "nationalities")
public class NationalityJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Size(max = 3)
    @NotNull
    @Column(name = "code", nullable = false, length = 3)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_id")
    private RegionJpaEntity region;
}

