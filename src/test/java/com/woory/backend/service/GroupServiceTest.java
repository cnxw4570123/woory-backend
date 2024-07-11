package com.woory.backend.service;

import com.woory.backend.dto.GroupDto;
import com.woory.backend.entity.GroupStatus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@SpringBootTest
class GroupServiceTest {
//
//    @MockBean
//    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    @Test
    void createGroup() {

        final String groupName = "test";


        groupService.createGroup(groupName);


    }
}
