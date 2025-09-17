package org.kosa.listener;

import jakarta.persistence.PreRemove;
import org.kosa.entity.Member;

import java.time.LocalDateTime;

public class MemberEntityListener {

    @PreRemove
    public void preRemove(Member member) {
        member.setDeletedAt(LocalDateTime.now());
    }
}