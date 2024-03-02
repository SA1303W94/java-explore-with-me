package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "endpoints")
@Getter
@Setter
public class Endpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "endpoint_id")
    private Long id;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "uri")
    private String uri;
}