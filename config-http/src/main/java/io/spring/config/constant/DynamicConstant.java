package io.spring.config.constant;


import io.spring.config.annotation.DynamicConfig;
import io.spring.config.annotation.UnityClass;

import java.util.List;
import java.util.Map;

@UnityClass
public class DynamicConstant {

    @DynamicConfig("top_k:10000")
    public static volatile Integer top_k;

    @DynamicConfig("test_map:{\"aa\":\"bb\"}")
    public static volatile Map<String,String> test_map;

    @DynamicConfig("test_list:[1,5,6]")
    public static volatile List<Integer> test_list_int;

    @DynamicConfig("test_list:[\"a\",\"b\",\"c\"]")
    public static volatile List<String> test_list_string;

    @DynamicConfig("test_double:18.65")
    public static volatile Double test_double;

    @DynamicConfig("test_boolean:true")
    public static volatile Boolean test_boolean;

    @DynamicConfig("test_float:85.65")
    public static volatile Float test_float;
}
