package com.td.traveldiary.domain.member.entity;

import com.td.traveldiary.global.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    private Long id;
    private String name;
    private String profileImageUrl;
    private String providerId;
    private Provider provider;
    private Role role;
    private boolean isDeleted;

    @Builder
    public Member(Long id, String name, String profileImageUrl, String providerId, Provider provider, Role role, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.providerId = providerId;
        this.provider = provider;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    public void updateProfile(String newName) {
        Assert.hasText(newName, "이름은 비어있을 수 없습니다.");
        this.name = newName;
    }
}
