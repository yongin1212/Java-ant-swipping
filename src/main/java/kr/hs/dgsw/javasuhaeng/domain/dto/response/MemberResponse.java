package kr.hs.dgsw.javasuhaeng.domain.dto.response;

import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Role;

public record MemberResponse(Long id, String loginId, String name, Role role) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getLoginId(), member.getName(), member.getRole());
    }
}
