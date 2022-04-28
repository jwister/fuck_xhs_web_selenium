/*
 * Description:
 *
 * History：
 * ========================================
 * Date              Version       Memo
 * 2022/4/1 15:23     1.0      Created by liuj
 * ========================================
 *
 * Copyright 2021, 迪爱斯信息技术股份有限公司保留。
 */
package com.weny7.selenium.main.model;

import lombok.Data;

import java.io.File;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/4/1 15:23
 */
@Data
public class User {

    private Long phone;

    private String name;

    private String url;

    private String topic;

    private String hid;

    private String fans;

    private String tempFileName;

    private HttpProxy httpProxy;



}
