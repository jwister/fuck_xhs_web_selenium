/*
 * Description:
 *
 * History：
 * ========================================
 * Date              Version       Memo
 * 2022/3/29 11:33     1.0      Created by liuj
 * ========================================
 *
 * Copyright 2021, 迪爱斯信息技术股份有限公司保留。
 */
package com.weny7.selenium.main;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.weny7.selenium.main.model.Config;
import com.weny7.selenium.main.model.User;
import com.weny7.selenium.main.type.CommitType;
import com.weny7.selenium.main.utils.CommUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.bean.BeanUtil.toBean;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/29 11:33
 */
public class StartCk {


    public static void main(String[] args) throws Exception {
        String alpath = CommUtils.getAbpath();
        Config cfg = CommUtils.getConfig();
        String configPath = alpath + "\\utils\\u.txt";
        if (args.length > 0 && ObjectUtil.isNotNull(args[0])) {
            if ("mkdir".equals(args[0])) { //创建目录
                List<User> users = CommUtils.getCkUsers(configPath);
                CommUtils.mkdirWithCk(users, cfg.getVideo_path());
            }
            if ("single".equals(args[0])) { //单独ck访问 无代理
                CommitType commitType = new CommitType();
                commitType.startLoginByCk();
            }
            if ("full".equals(args[0])) { //流程化
                CommitType commitType = new CommitType();
                commitType.startBatByCkProxy(true);
            }
            if ("fulln".equals(args[0])) { //流程化
                CommitType commitType = new CommitType();
                commitType.startBatByCkProxy(false);
            }
        }


    }

}
