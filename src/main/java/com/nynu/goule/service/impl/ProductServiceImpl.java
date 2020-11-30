package com.nynu.goule.service.impl;

import com.nynu.goule.utils.OSSUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nynu.goule.common.Result;
import com.nynu.goule.mapper.CategoryMapper;
import com.nynu.goule.mapper.ProductMapper;
import com.nynu.goule.pojo.OperateLog;
import com.nynu.goule.pojo.Product;
import com.nynu.goule.service.OperateLogService;
import com.nynu.goule.service.ProductService;
import com.nynu.goule.utils.DateUtil;
import com.nynu.goule.utils.ValidateUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;


@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private OperateLogService operateLogService;

    @Override
    public Result getAll(int pageNum, int pageSize) {
        Result result = new Result();
        pageNum = pageNum == 1 ? 1 : pageNum;
        pageSize = pageSize == 0 ? 5 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> products = productMapper.getAll();
        for(int j=0; j<products.size(); j++){
            Map<String, Object> map = products.get(j);
            List<String> imgs = new ArrayList<>();
            String imgInfo = (String) map.get("imgs");
            String[] img = imgInfo.split(",");
            for(int i=0; i<img.length; i++){
                imgs.add(img[i]);
            }
            map.put("imgs",imgs);
        }
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(products);
        if (!StringUtils.isEmpty(pageInfo)) {
            result.setData(pageInfo);
            result.setStatus(Result.RTN_CODE.SUCCESS);
        } else {
            result.setStatus(Result.RTN_CODE.ERROR);
            result.setMsg("查询失败");
        }
        return result;
    }

    @Override
    public Result queryProductByIndex(int pageNum, int pageSize, String productName, String description) {
        Result result = new Result();
        Map<String, Object> param = new HashMap<>();
        String productName1 = productName.replace(" ",""); // 对搜索框去空格处理
        String description1 = description.replace(" ","");
        param.put("productName", productName1);
        param.put("description", description1);
        param.put("pageNum", pageNum);
        param.put("pageSize", pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.queryProductByIndex(param);
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        if (!(products.size() == 0)) {
            result.setData(pageInfo);
            result.setStatus(Result.RTN_CODE.SUCCESS);
        } else {
            result.setStatus(Result.RTN_CODE.ERROR);
            result.setMsg("暂无该商品");
        }
        return result;
    }

    /**
     * @param param
     * @return
     */
    @Override
    public Result addNewProduct(Map<String, Object> param) {
        Map<String, Object> map = (Map<String, Object>) param.get("product");
        Result result = new Result();
        List<String> imgList = (List<String>) map.get("imgs");
        Map<String, Object> inputMap = new HashMap<>();
        String pCategoryId = (String) map.get("pCategoryId"); //所在父分类
        String categoryId = (String) map.get("categoryId"); //二级分类Id
        String price = (String) map.get("price"); //价格
        String productName = (String) ValidateUtil.isBlankParam(map, "productName", "产品名"); //产品名称
        String description = (String) map.get("description"); //产品描述
        String detail = (String) map.get("detail"); //产品详情
        String imgs = "";
        if("".equals(categoryId)){
            result.setMsg("一级分类下不可添加商品");
            result.setStatus(Result.RTN_CODE.ERROR);
            return result;
        }
        int num = categoryMapper.queryCategoryNumById(Integer.valueOf(categoryId));
        int pNum = categoryMapper.queryCategoryNumById(Integer.valueOf(pCategoryId));
        if (num == 0 || pNum == 0) {
            result.setStatus(Result.RTN_CODE.ERROR);
            result.setMsg("分类不存在");
        } else {
            for (int i=0; i<imgList.size(); i++){
                imgs += imgList.get(0).toString() + ",";
            }
            Timestamp dateTime = DateUtil.getCurrentTimestamp();
            inputMap.put("pCategoryId",pCategoryId);
            inputMap.put("categoryId",categoryId);
            inputMap.put("price",price);
            inputMap.put("productName",productName);
            inputMap.put("description",description);
            inputMap.put("detail",detail);
            inputMap.put("imgs",imgs);
            inputMap.put("dateTime",dateTime);
            int count = productMapper.addNewProduct(inputMap);
            if (count >= 1) {
                result.setStatus(Result.RTN_CODE.SUCCESS);
                result.setMsg("添加成功");
            } else {
                result.setStatus(Result.RTN_CODE.ERROR);
                result.setMsg("添加失败");
            }
        }
        return result;
    }

    @Override
    public Result delProduct(int id) {
        Result result = new Result();
        return null;
    }

    @Override
    public Result updateStatus(Map<String, Object> map) {
        Result result = new Result();
        Map<String, Object> operateMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        String beforeStatus = "";
        String afterStatus = "";
        param.put("id",map.get("id"));
        param.put("productStatus",map.get("productStatus"));
        int successNum = productMapper.updateStatusById(param);
        if(successNum > 0){
            if(((int)param.get("productStatus") == 1)){
                result.setMsg("上架成功");
                result.setStatus(Result.RTN_CODE.SUCCESS);
                beforeStatus = "下架";
                afterStatus = "上架";
            }else {
                result.setMsg("下架成功");
                result.setStatus(Result.RTN_CODE.SUCCESS);
                beforeStatus = "上架";
                afterStatus = "下架";
            }
            Map<String, Object> productInfoMap = productMapper.getProductInfoById(map);
            String productMsg = "更新商品“" + productInfoMap.get("productname") +"”状态";
            operateMap.put("acctId",map.get("account_name"));
            operateMap.put("opType", OperateLog.OP_TYPE.MODIFY);
            operateMap.put("logCntt",productMsg);
            operateMap.put("opMenu",OperateLog.OP_PATH.PRODUCT_MANAGEMENT);
            operateMap.put("beforeCntt",beforeStatus);
            operateMap.put("afterCntt",afterStatus);
            operateLogService.addOperateLog(operateMap);
        }else{
            result.setMsg("更新失败");
            result.setStatus(Result.RTN_CODE.ERROR);
        }
        return result;
    }

    public Result uploadImages(MultipartFile file) throws IOException {
        Result result = new Result();
        Map<String, Object> imgMap = new HashMap<>();
        File f = null;
        if(file.equals("")||file.getSize()<=0){
            file = null;
        }else{
            InputStream ins = file.getInputStream();
            f=new File(file.getOriginalFilename());
            inputStreamToFile(ins, f);
        }
        OSSUtil ossUtil = new OSSUtil();
        imgMap= ossUtil.uploadObject2OSS(f);
        result.setData(imgMap);
        result.setStatus(Result.RTN_CODE.SUCCESS);
        File del = new File(f.toURI());
        del.delete();
        return result;
    }

    public static void inputStreamToFile(InputStream ins,File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
