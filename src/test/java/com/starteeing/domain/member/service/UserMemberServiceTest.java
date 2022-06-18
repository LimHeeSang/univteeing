package com.starteeing.domain.member.service;

import com.starteeing.domain.member.dto.UserMemberSignupRequestDto;
import com.starteeing.domain.member.entity.UserMember;
import com.starteeing.domain.member.exception.ExistMemberException;
import com.starteeing.domain.member.repository.MemberRepository;
import com.starteeing.domain.member.repository.UserMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserMemberServiceTest {

    @Mock
    UserMemberRepository userMemberRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    UserMemberService userMemberService;

    @Test
    void 회원가입() {
        UserMember saveMember = createUserMemberRequestDto().toEntity(new BCryptPasswordEncoder());
        Long fakeMemberId = 1L;
        ReflectionTestUtils.setField(saveMember, "id", fakeMemberId);

        given(userMemberRepository.save(any())).willReturn(saveMember);

        UserMemberSignupRequestDto memberRequestDto = createUserMemberRequestDto();
        Long savedId = userMemberService.memberJoin(memberRequestDto);

        Assertions.assertThat(savedId).isEqualTo(fakeMemberId);
    }

    @Test
    void 중복회원_회원가입_예외() {
        given(memberRepository.existsByEmail("abc@naver.com")).willReturn(true);

        assertThatThrownBy(
                () -> userMemberService.memberJoin(createUserMemberRequestDto())
        ).isInstanceOf(ExistMemberException.class);
    }

    private UserMemberSignupRequestDto createUserMemberRequestDto() {
        return UserMemberSignupRequestDto.builder()
                .name("홍길동")
                .email("abc@naver.com")
                .password("1234")
                .nickname("길동이")
                .birthOfDate("1998-09-04")
                .phoneNumber("010-8543-0619")
                .mbti("estj")
                .school("순천향대")
                .department("정보보호학과")
                .uniqSchoolNumber("12345678")
                .build();
    }
}