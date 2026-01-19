package com.kh.replay.global.auth.local.model.service;

import org.springframework.stereotype.Service;

import com.kh.replay.global.auth.local.model.dto.LocalDTO;


public interface LocalService {
	int signUp(LocalDTO localDto);

}
