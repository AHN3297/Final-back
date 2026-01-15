package com.kh.replay.global.auth.local.model.service;

import org.springframework.stereotype.Service;

import com.kh.replay.global.auth.local.model.dao.LocalDAO;
import com.kh.replay.global.auth.local.model.dto.LocalDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalServiceImpl implements LocalService {

	private final LocalDAO localDao; 
	
	@Override
	public LocalDTO signUp(LocalDTO localDto) {
		
		
		
		
		
		return null;
	}

}
