package com.nynu.goule.utils;

import com.nynu.goule.mapper.LoginUserMapper;
import com.nynu.goule.mapper.RoleAuthMapper;
import com.nynu.goule.pojo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class Security {

    private static Security security;

    @Autowired
    private LoginUserMapper loginUserMapper;
    @Autowired
    private RoleAuthMapper roleAuthMapper;

    //通过@PostConstruct实现初始化bean之前进行的操作
    @PostConstruct
    public void init() {
        // 初使化时将已静态化的Service实例化
        security = this;
    }


    /**
     * 查询当前操作人权限
     * @param mainAcctId 操作人id
     * @param currentOperation  当前操作
     * @return
     */
    public boolean checkAccountAuth(String mainAcctId, String currentOperation){
        LoginUser user  = security.loginUserMapper.getUserIdByUserName(mainAcctId);
        String userId = String.valueOf(user.getId());
        //获取当前操作所需的角色id
        String[] roleIds = currentOperation.split(",");
        List<String> roleIdList = Arrays.asList(roleIds);
        List<Map<String, Object>> roleAuth = security.roleAuthMapper.queryAuthId(userId);
        //检验该权限是否有效
        for(String param : roleIdList){
            int num = security.roleAuthMapper.getAuthUseful(param);
            if(num > 0){
                return true;
            }
        }
        int same = 0;
        String authMsg = "";
        for(Map<String, Object> authMap : roleAuth){
            authMsg += authMap.get("authId")+",";
        }
        for(int i = 0; i<roleIds.length; i++){
            if(authMsg.contains(roleIds[i])){
                same++;
            }
        }
        if(same == 0){
            return false;
        }else{
            return true;
        }
    }
}
