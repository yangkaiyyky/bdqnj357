package com.bdqn.controller;

import cn.itrip.common.Dto;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.RedisUtli;
import cn.itrip.pojo.ItripLabelDic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UsereinfoController {

    @Resource
    RedisUtli redisUtli;

    @PostMapping(value="api/userinfo/adduserlinkuser",produces="application/json;charset=utf-8")
    @ResponseBody
    public Dto<Object> addUserLinkUser(HttpServletRequest request) throws Exception {
        String token = request.getHeader("token");
        String a = redisUtli.getstr(token);

        System.out.println(a);
        return DtoUtil.returnSuccess("chengg");
    }
}
