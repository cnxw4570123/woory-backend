package com.woory.backend.service;

import com.woory.backend.Repository.GroupRepository;
import com.woory.backend.entity.GroupEntity;
import com.woory.backend.entity.GroupStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    public GroupEntity createGroup(GroupEntity entity) {
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setGroupName(entity.getGroupName());
        groupEntity.setEmail(entity.getEmail());
        groupEntity.setStatus(GroupStatus.GROUP_LEADER);

        return groupRepository.save(groupEntity);
    }
}
