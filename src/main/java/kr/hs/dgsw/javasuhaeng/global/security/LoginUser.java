package kr.hs.dgsw.javasuhaeng.global.security;

import kr.hs.dgsw.javasuhaeng.domain.domain.Role;

public record LoginUser(Long memberId, Role role) {
}
