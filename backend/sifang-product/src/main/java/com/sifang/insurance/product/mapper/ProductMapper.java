package com.sifang.insurance.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sifang.insurance.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 分页查询产品列表
     */
    IPage<Product> selectPage(IPage<Product> page, @Param("type") Integer type, @Param("status") Integer status);
    
    /**
     * 根据产品编码查询产品
     */
    Product selectByCode(String code);
    
    /**
     * 查询热门产品
     */
    List<Product> selectHotProducts(@Param("limit") Integer limit);
    
    /**
     * 根据类型查询产品
     */
    List<Product> selectByType(Integer type);
}