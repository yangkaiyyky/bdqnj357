package com.bdqn.controller;

import cn.itrip.common.*;
import cn.itrip.dao.itripHotel.ItripHotelMapper;
import cn.itrip.dao.itripUser.ItripUserMapper;
import cn.itrip.pojo.ItripHotel;
import cn.itrip.pojo.ItripUser;
import cn.itrip.pojo.ItripUserVO;
import com.alibaba.fastjson.JSONArray;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class itripController {

   @Resource
    ItripHotelMapper dao;
   @Resource
    ItripUserMapper dao1;
   @Resource
    TokenBiz biz;
   @Resource
    RedisUtli redisUtli;
   @Resource
   ItripUserMapper dao2;



   //登陆
    @RequestMapping(value="/api/dologin",produces="application/json;charset=utf-8")
    @ResponseBody
    public Object login(String name, String password, HttpServletRequest request) throws Exception {
        Map map = new HashMap();
        map.put("a",name);
        map.put("b",password);
        ItripUser user = dao1.getItripUserListByMap(map);
        if (user!=null) {
            //模拟session 的票据---------------
            String token = biz.generateToken(request.getHeader("User-Agent"), user);
            //把这个token存储到redis中
            //fastjson把当前用户转成字符串
            redisUtli.setRedis(token,JSONArray.toJSONString(user));

            ItripTokenVO obj = new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis()*3600*2,Calendar.getInstance().getTimeInMillis());

            return DtoUtil.returnDataSuccess(obj);
        }
        return DtoUtil.returnFail("登陆失败","1000");
        /*return JSONArray.toJSONString(user);*/

    }
    //用户邮箱注册
    //api/doregister
    @RequestMapping(value="api/doregister",produces="application/json;charset=utf-8")

    @ResponseBody
    public Dto registeremail(@RequestBody ItripUserVO vo,HttpServletRequest request) throws Exception {
        ItripUser itripUser = new ItripUser();
        itripUser.setUserName(vo.getUserName());
        itripUser.setUserPassword(vo.getUserPassword());
        itripUser.setUserCode(vo.getUserCode());
        dao2.insertItripUser(itripUser);
        //产生随机数
        Random random = new Random(4);
        int sj = random.nextInt(9999);
        //调取发送短信接口
        App.SentSmail(itripUser.getUserCode(),""+sj);
        //存入redis
        redisUtli.setRedis(vo.getUserCode(),""+sj);
        //调取redis的vlaue值
        String redisvlaue = redisUtli.getstr(vo.getUserCode());
        if (redisvlaue.equals(""+sj)){
            itripUser.setActivated(1);
            dao2.updateItripUser(itripUser);
            return DtoUtil.returnDataSuccess("激活成功");
        }
        System.out.println(redisvlaue);
        return DtoUtil.returnDataSuccess("注册成功");
    }

    //用户手机注册
    @RequestMapping(value="api/registerbyphone",produces="application/json;charset=utf-8")

    @ResponseBody
    public Dto register(@RequestBody ItripUserVO vo,HttpServletRequest request) throws Exception {
        ItripUser itripUser = new ItripUser();
        itripUser.setUserName(vo.getUserName());
        itripUser.setUserPassword(vo.getUserPassword());
        itripUser.setUserCode(vo.getUserCode());
        dao2.insertItripUser(itripUser);
        //产生随机数
        Random random = new Random(4);
        int sj = random.nextInt(9999);
        //调取发送短信接口
        sentSms(vo.getUserCode(),""+sj);
        //存入redis
        redisUtli.setRedis(vo.getUserCode(),""+sj);
        //调取redis的vlaue值
        String redisvlaue = redisUtli.getstr(vo.getUserCode());
        if (redisvlaue.equals(""+sj)){
            itripUser.setActivated(1);
            dao2.updateItripUser(itripUser);
            return DtoUtil.returnDataSuccess("激活成功");
        }
        System.out.println(redisvlaue);
        return DtoUtil.returnDataSuccess("注册成功");
    }
    //用户邮箱激活
    //api/activate
    @RequestMapping(value="api/activate",produces="application/json;charset=utf-8")

    @ResponseBody
    public Dto validatee(String user,String code) throws Exception {
        String redisvalue = redisUtli.getstr(user);
        if (redisvalue!=null&& redisvalue.equals(code)){
            ItripUser itripUser = new ItripUser();
            itripUser.setActivated(1);
            itripUser.setUserCode(user);
            dao2.updateItripUser(itripUser);
            return DtoUtil.returnSuccess("激活成功");
        }else{
            return DtoUtil.returnFail("激活失败","1000000");
        }


    }

    //用户手机激活
    @RequestMapping(value="api/validatephone",produces="application/json;charset=utf-8")

    @ResponseBody
    public Dto validate(String user,String code) throws Exception {
        String redisvalue = redisUtli.getstr(user);
        if (redisvalue!=null&& redisvalue.equals(code)){
            ItripUser itripUser = new ItripUser();
            itripUser.setActivated(1);
            itripUser.setUserCode(user);
            dao2.updateItripUser(itripUser);
            return DtoUtil.returnSuccess("激活成功");
        }else{
            return DtoUtil.returnFail("激活失败","1000000");
        }

    }
    //验证邮箱是否存在
    @RequestMapping(value="api/ckusr",produces="application/json;charset=utf-8")

    @ResponseBody
    public Dto yanzheng(String name) throws Exception {
        Map map = new HashMap();
        map.put("email",name);
        ItripUser user = dao2.email(map);
        if (user!=null){
            return DtoUtil.returnFail("邮箱已经存在，请重新输入","10000");
        }else {
            return DtoUtil.returnSuccess();
        }
    }

    @RequestMapping(value="clist",produces="application/json;charset=utf-8")

    @ResponseBody
    public String glist(String pid) throws Exception {
        ItripHotel list=dao.getItripHotelById(new Long(56));
        return JSONArray.toJSONString(list);
    }

    @RequestMapping("/clist1")
    public String clist(){

        return "clist1";
    }




    //封装
    public static void sentSms(String phone,String message){
        //生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8aaf07087f639e2b017f6c33d61c01f2";
        String accountToken = "6139ddd775a64f5b8d1e2ff14a3dafcd";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8aaf07087f639e2b017f6c33d71d01f9";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_XML);
        String to = phone;
        String templateId= "1";
        String[] datas = {message};
        //  String subAppend="1234";  //可选	扩展码，四位数字 0~9999
        //  String reqId="***";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        //  HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }

}