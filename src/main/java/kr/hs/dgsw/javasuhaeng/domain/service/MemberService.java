//용인
package kr.hs.dgsw.javasuhaeng.domain.service;

import kr.hs.dgsw.javasuhaeng.domain.dto.response.MemberResponse;

public interface MemberService {
    MemberResponse getMember(Long memberId);
}
