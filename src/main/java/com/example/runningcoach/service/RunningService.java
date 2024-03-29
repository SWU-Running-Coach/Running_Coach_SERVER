package com.example.runningcoach.service;

import com.example.runningcoach.dto.FeedbackByMonthDayResponseDto;
import com.example.runningcoach.dto.FeedbackByMonthResponseDto;
import com.example.runningcoach.dto.RunningRequestDto;
import com.example.runningcoach.dto.RunningResponseDto;
import com.example.runningcoach.entity.Running;
import com.example.runningcoach.entity.User;
import com.example.runningcoach.exception.custom.NoExistEmailException;
import com.example.runningcoach.exception.custom.NoExistValueException;
import com.example.runningcoach.repository.RunningRepository;
import com.example.runningcoach.repository.UserRepository;
import com.example.runningcoach.response.ResponseMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RunningService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RunningRepository runningRepository;

	public void runningAnalyze(RunningRequestDto runningRequestDto, String email) {
		Optional<User> user = userRepository.findByEmail(email);

		//존재하지 않는 이메일
		if (user.isEmpty()) {
			throw new NoExistEmailException(ResponseMessage.NO_EXIST_EMAIL);
		}

		Running running = Running.builder()
			.user(user.get())
			.image(runningRequestDto.getImage())
			.cadence(runningRequestDto.getCadence())
			.uppderBodyAngle(runningRequestDto.getUppderBodyAngle())
			.legAngle(runningRequestDto.getLegAngle())
			.dateTime(runningRequestDto.getDateTime())
			.feedback_body(runningRequestDto.getFeedback_body())
			.feedback_leg(runningRequestDto.getFeedback_leg()).build();

		runningRepository.save(running);
	}

	public RunningResponseDto getFeedback(String email, LocalDateTime localDateTime) {
		Optional<User> user = userRepository.findByEmail(email);

		//존재하지 않는 이메일
		if (user.isEmpty()) {
			throw new NoExistEmailException(ResponseMessage.NO_EXIST_EMAIL);
		}

		List<Running> result = runningRepository.findByUserEmailAndDateTime(email, localDateTime);

		if (result.isEmpty()) {
			throw new NoExistValueException(ResponseMessage.NO_EXIST_VALUE);
		}

		Running running = result.get(0);

		RunningResponseDto runningResponseDto = new RunningResponseDto();

		runningResponseDto.setImage(running.getImage());
		runningResponseDto.setCadence(running.getCadence());
		runningResponseDto.setLegAngle(running.getLegAngle());
		runningResponseDto.setUpperBodyAngle(running.getUppderBodyAngle());
		runningResponseDto.setDateTime(running.getDateTime());

		return runningResponseDto;
	}

	public List<FeedbackByMonthResponseDto> getFeedbackByMonth(String email, int year, int month) {
		Optional<User> user = userRepository.findByEmail(email);

		//존재하지 않는 이메일
		if (user.isEmpty()) {
			throw new NoExistEmailException(ResponseMessage.NO_EXIST_EMAIL);
		}

		List<Running> result = runningRepository.findByDateTimeYearAndMonthAndUserEmail(year, month, email);

		if (result.isEmpty()) {
			throw new NoExistValueException(ResponseMessage.NO_EXIST_VALUE);
		}

		List<FeedbackByMonthResponseDto> feedbackByMonthResponseDtos = new ArrayList<>();

		for (Running entity : result) {
			FeedbackByMonthResponseDto dto = new FeedbackByMonthResponseDto();

			dto.setDate(entity.getDateTime());

			feedbackByMonthResponseDtos.add(dto);
		}

		return feedbackByMonthResponseDtos;
	}

	public List<FeedbackByMonthDayResponseDto> getFeedbackByMonthAndDay(String email, int year, int month, int day) {
		Optional<User> user = userRepository.findByEmail(email);

		//존재하지 않는 이메일
		if (user.isEmpty()) {
			throw new NoExistEmailException(ResponseMessage.NO_EXIST_EMAIL);
		}

		List<Running> result = runningRepository.findByDateTimeYearAndMonthAndDayAndUserEmail(year, month, day, email);

		if (result.isEmpty()) {
			throw new NoExistValueException(ResponseMessage.NO_EXIST_VALUE);
		}

		List<FeedbackByMonthDayResponseDto> feedbackByMonthDayResponseDtos = new ArrayList<>();

		int i = 0;
		for (Running entity : result) {
			FeedbackByMonthDayResponseDto dto = new FeedbackByMonthDayResponseDto();

			dto.setIdx(i);
			dto.setImage(entity.getImage());
			dto.setCadence(entity.getCadence());
			dto.setLegAngle(entity.getLegAngle());
			dto.setUpperBodyAngle(entity.getUppderBodyAngle());

			feedbackByMonthDayResponseDtos.add(dto);
			i++;
		}

		return feedbackByMonthDayResponseDtos;
	}
}
