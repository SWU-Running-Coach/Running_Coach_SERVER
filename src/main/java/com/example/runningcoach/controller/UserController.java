package com.example.runningcoach.controller;

import com.example.runningcoach.dto.SignupRequestDto;
import com.example.runningcoach.response.BaseResponse;
import com.example.runningcoach.response.ResponseMessage;
import com.example.runningcoach.response.StatusCode;
import com.example.runningcoach.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;

	//TODO: swagger 등록
	@GetMapping("/join")
	public ResponseEntity signUp(@RequestBody @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {
		//valid check
		if (bindingResult.hasErrors()) {
			return new ResponseEntity(BaseResponse.response(StatusCode.BAD_REQUEST, Objects.requireNonNull(
				bindingResult.getFieldError()).getDefaultMessage()),
				HttpStatus.BAD_REQUEST);
		}
		userService.SignupUser(signupRequestDto);

		return new ResponseEntity(BaseResponse.response(StatusCode.CREATED, ResponseMessage.SUCCESS),
			HttpStatus.CREATED);
	}
}
