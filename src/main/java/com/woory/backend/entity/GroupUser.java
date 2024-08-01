package com.woory.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "group_user")
public class GroupUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "groupId")
    private Group group;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GroupStatus status;

    @Column(name = "regDate")
    private Date regDate;

    @Column(name = "lastUpdatedDate")
    private Date lastUpdatedDate;

    @OneToMany(mappedBy = "groupUser")
    private List<Favorite> favorites = new ArrayList<>();
}
