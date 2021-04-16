package com.wooyoo.learning.controller;

import com.wooyoo.learning.ProductNotFoundException;
import com.wooyoo.learning.dao.domain.Product;
import com.wooyoo.learning.dao.mapper.ProductMapper;
import com.wooyoo.learning.util.RedisCache;
import com.wooyoo.learning.util.RedisService_pyzl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisService_pyzl redisService_pyzl;

    @GetMapping("/{id}")
    /**
     * showdoc
     * @catalog 测试文档/用户相关
     * @title 用户登录
     * @description 用户登录的接口
     * @method get
     * @url https://www.showdoc.cc/home/user/login
     * @header token 可选 string 设备token
     * @param username 必选 string 用户名1
     * @param password 必选 string 密码1
     * @param name 可选 string 用户昵称
     * @return {"error_code":0,"data":{"uid":"1","username":"12154545","name":"吴系挂","groupid":2,"reg_time":"1436864169","last_login_time":"0"}}
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 这里是备注信息
     * @number 99
     */
    public String getProductInfo(@PathVariable("id") Long productId) {
        redisService_pyzl.setValue("1","2");
        return "hi";
//        return productMapper.select(productId);
    }

    @PutMapping("/{id}")
    public Product updateProductInfo(
            @PathVariable("id")
                    Long productId,
            @RequestBody
                    Product newProduct) {
        Product product = productMapper.select(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }
        product.setName(newProduct.getName());
        product.setPrice(newProduct.getPrice());
        productMapper.update(product);
        return product;
    }
}
