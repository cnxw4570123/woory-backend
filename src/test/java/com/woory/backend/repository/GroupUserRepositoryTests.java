//package com.woory.backend.repository2;
//
//import com.woory.backend.entity.Group;
//import com.woory.backend.entity.GroupUser;
//import com.woory.backend.entity.User;
//import com.woory.backend.service.GroupService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class GroupUserRepositoryTests {
//
//    @Autowired
//    private GroupUserRepository groupUserRepository;
//
//    @Autowired
//    private GroupRepository groupRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private GroupService groupUserService;
//
//    private Group group;
//    private User user;
//
//    @BeforeEach
//    public void setup() {
//        user = new User();
//        user.setUsername("testUser");
//        user.setEmail("test@example.com");
//        userRepository.save(user);
//
//        group = new Group();
//        group.setGroupName("testGroup");
//        groupRepository.save(group);
//
//        GroupUser groupUser = new GroupUser();
//        groupUser.setUser(user);
//        groupUser.setGroup(group);
//        groupUserRepository.save(groupUser);
//    }
//
//    @Test
//    @Transactional
//    public void testDeleteByGroup_GroupId() {
//        Long groupId = group.getGroupId();
//
//        // Pre-condition: GroupUser 존재 여부 확인
//        assertTrue(groupUserRepository.existsByGroup_GroupId(groupId));
//
//        // Perf
//        groupUserRepository.deleteByGroup_GroupId(groupId);
//        // Post-condition: GroupUser 존재 여부 확인
//        assertFalse(groupUserRepository.existsByGroup_GroupId(groupId));
//    }
//}