package io.spring.core.param;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServerValueParam {

    private String value;

    private String type;

    private String desc;

    private LocalDateTime updateTime;
}
