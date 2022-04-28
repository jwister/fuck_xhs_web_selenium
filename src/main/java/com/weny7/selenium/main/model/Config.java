/*
 * Description:
 *
 * History：
 * ========================================
 * Date              Version       Memo
 * 2022/4/4 15:37     1.0      Created by liuj
 * ========================================
 *
 * Copyright 2021, 迪爱斯信息技术股份有限公司保留。
 */
package com.weny7.selenium.main.model;

import lombok.Data;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/4/4 15:37
 */
@Data
public class Config {

    private String proxy_url;

    private String video_path;

    private Integer thread_size;
}
