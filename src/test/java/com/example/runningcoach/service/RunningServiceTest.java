package com.example.runningcoach.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.runningcoach.dto.RunningRequestDto;
import com.example.runningcoach.dto.RunningResponseDto;
import com.example.runningcoach.dto.SignupRequestDto;
import com.example.runningcoach.repository.RunningRepository;
import com.example.runningcoach.response.ResponseMessage;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class RunningServiceTest {

	@Autowired
	UserService userService;

	@Autowired
	RunningService runningService;

	@Autowired
	RunningRepository runningRepository;

	@Test
	@DisplayName("피드백 저장 테스트")
	public void runningServiceTest() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();

		String email = "runningservice@test.com";

		signupRequestDto.setEmail(email);
		signupRequestDto.setNickname("running");
		signupRequestDto.setPassword("TestPwd1");
		userService.SignupUser(signupRequestDto);

		//when
		RunningRequestDto runningRequestDto = new RunningRequestDto();

		runningRequestDto.setImage("test_img_url");
		runningRequestDto.setDateTime(LocalDateTime.now());
		runningRequestDto.setCadence(40);
		runningRequestDto.setLegAngle(155.2);
		runningRequestDto.setUppderBodyAngle(12.4);

		//then
		assertDoesNotThrow(() -> {
			runningService.runningAnalyze(runningRequestDto, email);
		});

	}

	@Test
	@DisplayName("피드백 저장 실패 테스트(존재하지 않은 이메일)")
	public void runningServiceFailTest() {
		//given
		String email = "runningfail@test.com";

		//when
		RunningRequestDto runningRequestDto = new RunningRequestDto();

		runningRequestDto.setImage("test_img_url");
		runningRequestDto.setDateTime(LocalDateTime.now());
		runningRequestDto.setCadence(40);
		runningRequestDto.setLegAngle(155.2);
		runningRequestDto.setUppderBodyAngle(12.4);

		//when
		Throwable throwable = assertThrows(RuntimeException.class, () -> {
			runningService.runningAnalyze(runningRequestDto, email);
		});
		//then
		assertEquals(throwable.getMessage(), ResponseMessage.NO_EXIST_EMAIL);
	}

	@Test
	@DisplayName("피드백 조회 성공 테스트")
	public void getFeedbackTest() {
		//given
		SignupRequestDto signupRequestDto = new SignupRequestDto();

		String email = "getFeedback@test.com";

		signupRequestDto.setEmail(email);
		signupRequestDto.setNickname("running");
		signupRequestDto.setPassword("TestPwd1");
		userService.SignupUser(signupRequestDto);

		//when
		RunningRequestDto runningRequestDto = new RunningRequestDto();

		LocalDateTime dateTime = LocalDateTime.now();

		runningRequestDto.setImage("test_img_url");
		runningRequestDto.setDateTime(dateTime);
		runningRequestDto.setCadence(40);
		runningRequestDto.setLegAngle(155.2);
		runningRequestDto.setUppderBodyAngle(12.4);

		runningService.runningAnalyze(runningRequestDto, email);
		//then
		RunningResponseDto runningResponseDto = runningService.getFeedback(email, dateTime);

		assertEquals(runningResponseDto.getCadence(), runningRequestDto.getCadence());
		assertEquals(runningResponseDto.getLegAngle(), runningRequestDto.getLegAngle());
		assertEquals(runningResponseDto.getUpperBodyAngle(), runningRequestDto.getUppderBodyAngle());
	}

	@Test
	@DisplayName("피드백 조회 실패 테스트(존재하지 않는 이메일)")
	public void NogetFeedbackTest() {
		//given
		String email = "NOgetFeedback@test.com";
		LocalDateTime localDateTime = LocalDateTime.now();

		//when
		Throwable throwable = assertThrows(RuntimeException.class, () -> {
			runningService.getFeedback(email, localDateTime);
		});
		//then
		assertEquals(throwable.getMessage(), ResponseMessage.NO_EXIST_EMAIL);
	}
}
