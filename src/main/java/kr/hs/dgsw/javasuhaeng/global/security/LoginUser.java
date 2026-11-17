//용인
package kr.hs.dgsw.javasuhaeng.global.security;

import kr.hs.dgsw.javasuhaeng.domain.domain.Member;
import kr.hs.dgsw.javasuhaeng.domain.domain.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class LoginUser extends User {

    private final Long memberId;
    private final Role role;

    public LoginUser(Member member) {
        super(member.getLoginId(), member.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name())));
        this.memberId = member.getId();
        this.role = member.getRole();
    }

    public Long memberId() {
        return memberId;
    }

    public Role role() {
        return role;
    }
}
