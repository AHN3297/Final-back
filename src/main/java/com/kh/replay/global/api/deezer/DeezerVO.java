package com.kh.replay.global.api.deezer;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeezerVO {
    private List<DeezerItem> data; // iTunes는 results인데 Deezer는 data입니다.

    @Getter
    @NoArgsConstructor
    public static class DeezerItem {
        private Long id;
        private String name;
        private String picture_small;
        private String picture_medium;
        private String picture_big;   // 고화질 이미지
        private String picture_xl;
    }
}