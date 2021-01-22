package com.nynu.goule.controller;

import com.nynu.goule.common.BaseController;
import com.nynu.goule.service.RoleService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author  goule
 * @date  2021/1/22 9:40
 */

@RestController
@RequestMapping("/manage/role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;

    @RequestMapping("/getAllRole")
    public ModelAndView getAllRoles(){
        return feedback(roleService.getAllRoles());
    }
    
    @RequestMapping("/getAcctRoles")
    public ModelAndView getAcctRoles(@RequestBody Map<String, Object> param){
        return feedback(roleService.getAccountRoles(param));
    }
}
