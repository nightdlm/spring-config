package io.spring.config.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateConfig {

    private Integer id;

    @NotNull(message = "服务id不能为空")
    private Integer serverId;

    @NotBlank(message = "key字段不能为空")
    private String key;

    @NotBlank(message = "value字段不能为空")
    private String value;

    private String desc;


    public interface Delete{}

    public interface Update{}
}
