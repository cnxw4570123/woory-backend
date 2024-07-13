package com.woory.backend.controller;

import com.woory.backend.repository2.UserRepository;
import com.woory.backend.service.GroupService;
import com.woory.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController("v1/users")
public class UserController {
    private GroupService groupService;
    private UserService userService;

    @Autowired
    public UserController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }
}
