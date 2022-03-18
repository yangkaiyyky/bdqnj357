package com.bdqn.controller;


import cn.itrip.common.DtoUtil;
import cn.itrip.dao.itripAreaDic.ItripAreaDicMapper;
import cn.itrip.dao.itripLabelDic.ItripLabelDicMapper;
import cn.itrip.pojo.ItripAreaDic;
import cn.itrip.pojo.ItripLabelDic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CityController {

    @Resource
    ItripAreaDicMapper dao;

    @RequestMapping(value="/api/hotel/queryhotcity/{type}",produces="application/json;charset=utf-8")
    @ResponseBody
    public Object getCity(@PathVariable("type")int t) throws Exception {

        Map map = new HashMap();
        map.put("aa",t);
        List<ItripAreaDic> list = dao.getItripAreaDicListByMap(map);

        return DtoUtil.returnDataSuccess(list);
    }

    @Resource
    ItripLabelDicMapper dao1;

    @GetMapping(value="api/hotel/queryhotelfeature",produces="application/json;charset=utf-8")
    @ResponseBody
    public Object getCitytese() throws Exception {


        List<ItripLabelDic> list = dao1.fisttop();

        return DtoUtil.returnDataSuccess(list);
    }
    }

