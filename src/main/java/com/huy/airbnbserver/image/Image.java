package com.huy.airbnbserver.image;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.huy.airbnbserver.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(indexes = {@Index(name = "imgName_index", columnList = "name")})
public class Image implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] imageData;

    @OneToOne(mappedBy = "avatar")
    @JsonBackReference
    private User user;
}
