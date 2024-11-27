package io.spring.core.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties
public class ServerValueParam {

    private String value;

    private String type;

    private String desc;

    private LocalDateTime updateTime;
}
