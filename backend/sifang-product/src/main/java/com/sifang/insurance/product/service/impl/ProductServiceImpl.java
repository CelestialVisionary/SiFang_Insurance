package com.sifang.insurance.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sifang.insurance.product.entity.Product;
import com.sifang.insurance.product.mapper.ProductMapper;
import com.sifang.insurance.product.service.ProductService;
import com.sifang.insurance.common.entity.PageRequest;
import com.sifang.insurance.common.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 产品服务实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_CACHE_KEY = "product:detail:";
    private static final Long CACHE_EXPIRE_TIME = 30L; // 30分钟

    @Override
    public IPage<Product> pageProducts(PageRequest pageRequest, Integer type, Integer status) {
        Page<Product> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        return productMapper.selectPage(page, type, status);
    }

    @Override
    public Product getProductById(Long id) {
        // 先从缓存获取
        String cacheKey = PRODUCT_CACHE_KEY + id;
        Product product = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (product == null) {
            // 缓存未命中，从数据库查询
            product = productMapper.selectById(id);
            if (product != null) {
                // 放入缓存
                redisTemplate.opsForValue().set(cacheKey, product, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            }
        }
        return product;
    }

    @Override
    public Product getProductByCode(String code) {
        return productMapper.selectByCode(code);
    }

    @Override
    public boolean createProduct(Product product) {
        // 生成产品编码
        product.setCode("PROD" + CommonUtils.generateRandomString(8).toUpperCase());
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return save(product);
    }

    @Override
    public boolean updateProduct(Product product) {
        product.setUpdateTime(LocalDateTime.now());
        boolean result = updateById(product);
        if (result) {
            // 清除缓存
            String cacheKey = PRODUCT_CACHE_KEY + product.getId();
            redisTemplate.delete(cacheKey);
        }
        return result;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        boolean result = updateById(product);
        if (result) {
            // 清除缓存
            String cacheKey = PRODUCT_CACHE_KEY + id;
            redisTemplate.delete(cacheKey);
        }
        return result;
    }

    @Override
    public List<Product> getHotProducts(Integer limit) {
        return productMapper.selectHotProducts(limit);
    }

    @Override
    public List<Product> getProductsByType(Integer type) {
        return productMapper.selectByType(type);
    }
}