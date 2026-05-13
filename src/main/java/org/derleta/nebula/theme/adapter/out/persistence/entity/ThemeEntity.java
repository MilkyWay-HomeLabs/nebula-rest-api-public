package org.derleta.nebula.theme.adapter.out.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "themes")
public class ThemeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Size(max = 20)
    @Column(name = "name", length = 20)
    private String name;

}
