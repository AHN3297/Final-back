package com.kh.replay.chat.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter 
@ToString
public class ChatVO {
    private long chatNo;
    private long memberId;
    private String chatContent;
    private String createDate;  
    private String nickname; 
}
