package com.apabi.flow.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 功能描述： <br>
 * <字符串参数，向map 中添加的时候，如果为 空 ,则不添加>
 *
 * @author supeng
 * @date 2018/8/28 10:33
 * @since 1.0.0
 */
public class ParamsUtils {

    /**
     * 如果参数位 为 空，则该 参数对 不添加进 map 中
     * eg: ParamsUtils.checkParameterAndPut2Map(map,"name",name,"email",email)
     *
     * @param paramMap
     * @param params
     * @throws IllegalArgumentException
     */
    public static void checkParameterAndPut2Map(Map paramMap, String... params) throws NullPointerException,IllegalArgumentException{
        if (paramMap == null){
            throw new NullPointerException("paramMap is null");
        }
        if (params == null){
            throw new NullPointerException("params are null");
        }
        if (params.length%2 == 0){
            for (int i=0; i<params.length;i=i+2){
                if (StringUtils.isNotBlank(params[i]) && StringUtils.isNotBlank(params[i+1])){
                    paramMap.put(params[i],params[i+1]);
                }
            }
        }else{
            throw new IllegalArgumentException("Parameters are not paired!");
        }

    }

    public static void putParams2Map(Map paramMap, String... params) throws NullPointerException,IllegalArgumentException{
        if (paramMap == null){
            throw new NullPointerException("paramMap is null");
        }
        if (params == null){
            throw new NullPointerException("params are null");
        }
        if (params.length%2 == 0){
            for (int i=0; i<params.length;i=i+2){
                if (StringUtils.isNotBlank(params[i]) && StringUtils.isNotBlank(params[i+1])){
                    paramMap.put(params[i],params[i+1]);
                }
            }
        }else{
            throw new IllegalArgumentException("Parameters are not paired!");
        }

    }
}
