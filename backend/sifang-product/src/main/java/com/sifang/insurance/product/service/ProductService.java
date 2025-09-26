package com.sifang.insurance.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sifang.insurance.product.entity.Product;
import com.sifang.insurance.common.entity.PageRequest;

import java.util.List;

/**
 * 产品服务接口
 */
public interface ProductService extends IService<Product> {
    
    /**
     * 分页查询产品列表
     */
    IPage<Product> pageProducts(PageRequest pageRequest, Integer type, Integer status);
    
    /**
     * 根据ID获取产品详情
     */
    Product getProductById(Long id);
    
    /**
     * 根据编码获取产品
     */
    Product getProductByCode(String code);
    
    /**
     * 新增产品
     */
    boolean createProduct(Product product);
    
    /**
     * 更新产品
     */
    boolean updateProduct(Product product);
    
    /**
     * 上下架产品
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 获取热门产品列表
     */
    List<Product> getHotProducts(Integer limit);
    
    /**
     * 根据类型获取产品列表
     */
    List<Product> getProductsByType(Integer type);
}