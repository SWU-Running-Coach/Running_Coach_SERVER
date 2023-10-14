package com.example.runningcoach.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.runningcoach.dto.LoginRequestDto;
import com.example.runningcoach.dto.SignupRequestDto;
import com.example.runningcoach.entity.User;
import com.example.runningcoach.repository.UserRepository;
import com.example.runningcoach.response.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserServiceTest {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("회원가입 테스트")
	public void signUpTest() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();

		signupRequestDto.setEmail("test@test.com");
		signupRequestDto.setNickname("testNickname");
		signupRequestDto.setPassword("TestPwd1");

		//when
		userService.SignupUser(signupRequestDto);

		//then
		User result = userRepository.findByEmail(signupRequestDto.getEmail()).get();
		assertThat(result.getEmail()).isEqualTo(signupRequestDto.getEmail());
		assertThat(result.getNickname()).isEqualTo(signupRequestDto.getNickname());
	}

	@Test
	@DisplayName("비밀번호 암호화 테스트")
	public void passwordTest() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();

		signupRequestDto.setEmail("pwd@test.com");
		signupRequestDto.setNickname("testNickname");
		signupRequestDto.setPassword("TestPwd1");

		//when
		userService.SignupUser(signupRequestDto);

		//then
		User result = userRepository.findByEmail(signupRequestDto.getEmail()).get();
		assertThat(result.getEmail()).isEqualTo(signupRequestDto.getEmail());
		assertThat(passwordEncoder.matches(signupRequestDto.getPassword(), result.getPassword())).isTrue();
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	public void successLogin() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();
		signupRequestDto.setEmail("login@test.com");
		signupRequestDto.setPassword("Pwdasdf1");
		signupRequestDto.setNickname("login");

		userService.SignupUser(signupRequestDto);

		LoginRequestDto loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("login@test.com");
		loginRequestDto.setPassword("Pwdasdf1");

		//when
		//then
		assertDoesNotThrow(() -> {
			userService.LoginUser(loginRequestDto);
		});
	}

	@Test
	@DisplayName("로그인 실패 테스트 (비밀번호 불일치)")
	public void failPwdLogin() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();
		signupRequestDto.setEmail("fail@test.com");
		signupRequestDto.setPassword("Pwdasdf1");
		signupRequestDto.setNickname("login");

		userService.SignupUser(signupRequestDto);

		LoginRequestDto loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("fail@test.com");
		loginRequestDto.setPassword("FailPwd2");

		//when
		Throwable throwable = assertThrows(RuntimeException.class, () -> {
			userService.LoginUser(loginRequestDto);
		});

		//then
		assertEquals(throwable.getMessage(), ResponseMessage.FAIL_LOGIN);
	}

	@Test
	@DisplayName("로그인 실패 테스트 (존재하지 않는 이메일)")
	public void failEmailLogin() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();
		signupRequestDto.setEmail("noemail@test.com");
		signupRequestDto.setPassword("Pwdasdf1");
		signupRequestDto.setNickname("login");

		userService.SignupUser(signupRequestDto);

		LoginRequestDto loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("no@test.com");
		loginRequestDto.setPassword("Pwdasdf1");

		//when
		Throwable throwable = assertThrows(RuntimeException.class, () -> {
				userService.LoginUser(loginRequestDto);
			});
		//then
		assertEquals(throwable.getMessage(), ResponseMessage.FAIL_LOGIN);
	}

	@Test
	@DisplayName("탈퇴한 계정 로그인")
	public void noLogin() {
		//given
		User user = User.builder()
			.email("leave@test.com")
			.password(passwordEncoder.encode("Pwdasdf1"))
			.nickname("leave")
			.status(0).build();

		userRepository.save(user);

		LoginRequestDto loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("leave@test.com");
		loginRequestDto.setPassword("Pwdasdf1");

		//when
		Throwable throwable = assertThrows(RuntimeException.class, () -> {
			userService.LoginUser(loginRequestDto);
		});
		//then
		assertEquals(throwable.getMessage(), ResponseMessage.LEAVE_USER);
	}

	@Test
	@DisplayName("마이페이지 불러오기")
	public void myPage() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();
		signupRequestDto.setEmail("mypage@test.com");
		signupRequestDto.setPassword("Pwdasdf1");
		signupRequestDto.setNickname("mypage");

		userService.SignupUser(signupRequestDto);

		//when
		MypageResponseDto mypageResponseDto = new MyPageResponseDto();

		assertDoesNotThrow(() -> {
			mypageResponseDto = userService.myPage("mypage@test.com");
		});

		//then
		assertEquals(mypageResponseDto.getNickname(), signupRequestDto.getNickname());
	}

	@Test
	@DisplayName("마이페이지 존재하지 않는 이메일")
	public void myPage() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();
		signupRequestDto.setEmail("mypage@test.com");
		signupRequestDto.setPassword("Pwdasdf1");
		signupRequestDto.setNickname("mypage");

		userService.SignupUser(signupRequestDto);

		//when
		MypageResponseDto mypageResponseDto = new MyPageResponseDto();

		Throwable throwable = assertThrows(RuntimeException.class, () -> {
			mypageResponseDto = userService.myPage("my@test.com");
		});

		//then
		assertEquals(throwable.getMessage(), ResponseMessage.NO_EXIST_EMAIL);
	}
}
