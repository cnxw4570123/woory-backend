package com.woory.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
    @Enumerated(EnumType.ORDINAL)
    private GroupStatus status;

    @Column(name = "regDate")
    private Date regDate;

    @Column(name = "lastUpdatedDate")
    private Date lastUpdatedDate;

    private String image;
}
