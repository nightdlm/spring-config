package io.spring.config.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateConfig {

    private Integer id;

    @NotNull(message = "服务id不能为空")
    private Integer serverId;

    @NotNull(message = "key字段不能为空")
    private String key;

    @NotNull(message = "value字段不能为空",groups = {Update.class})
    private String value;

    private String desc;


    public interface Delete{}

    public interface Update{}
}
