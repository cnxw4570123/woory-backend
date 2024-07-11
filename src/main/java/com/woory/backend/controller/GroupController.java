package com.woory.backend.controller;

import com.woory.backend.entity.Group;
import com.woory.backend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/add")
    public ResponseEntity<Group> createGroup(@RequestParam String groupName) {
        Group group = groupService.createGroup(groupName);
        return ResponseEntity.ok(group);
    }

}
