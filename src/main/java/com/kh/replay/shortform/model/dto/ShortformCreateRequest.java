package com.kh.replay.shortform.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortformCreateRequest {

    @NotBlank(message = "숏폼 제목은 필수 입력 값입니다.")
    private String shortFormTitle;

    private String caption;
    private String status;
}