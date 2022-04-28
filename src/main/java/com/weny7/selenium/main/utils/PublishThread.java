/*
 * Description:
 *
 * History：
 * ========================================
 * Date              Version       Memo
 * 2022/4/18 17:50     1.0      Created by liuj
 * ========================================
 *
 * Copyright 2021, 迪爱斯信息技术股份有限公司保留。
 */
package com.weny7.selenium.main.utils;

import com.weny7.selenium.main.model.User;
import com.weny7.selenium.main.type.CommitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/4/18 17:50
 */
public class PublishThread extends Thread {
    List<User> users = new ArrayList<>();
    boolean push = false;
    public PublishThread(List<User> users,boolean push) {
        super();
        this.users = users;
        this.push = push;
    }

    @Override
    public void run() {
        CommitType commitType = new CommitType();
        commitType.batByCk(users, push);
    }
}
