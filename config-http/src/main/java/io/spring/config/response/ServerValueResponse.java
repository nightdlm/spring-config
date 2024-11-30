package io.spring.config.response;

import lombok.Data;

@Data
public class ServerValueResponse {

    private String key;

    private String value;

    private String desc;

    private String type;
}
