package com.weny7.selenium.main.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 代理模型
 * @author liuj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpProxy {

    private String ip;

    private Integer port;

    private String user;

    private String pwd;

}
