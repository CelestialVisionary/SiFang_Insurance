package com.sifang.insurance.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sifang.insurance.product.entity.Product;
import com.sifang.insurance.product.service.ProductService;
import com.sifang.insurance.common.entity.PageRequest;
import com.sifang.insurance.common.entity.PageResult;
import com.sifang.insurance.common.entity.Result;
import com.sifang.insurance.common.entity.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品Controller
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 分页查询产品列表
     */
    @GetMapping("/page")
    public Result<PageResult<Product>> pageProducts(PageRequest pageRequest, 
                                                   @RequestParam(required = false) Integer type,
                                                   @RequestParam(required = false) Integer status) {
        IPage<Product> page = productService.pageProducts(pageRequest, type, status);
        PageResult<Product> result = PageResult.build(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
        return Result.success(result);
    }

    /**
     * 获取产品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return Result.fail(ResultCode.NOT_FOUND);
        }
        return Result.success(product);
    }

    /**
     * 新增产品
     */
    @PostMapping
    public Result<Boolean> createProduct(@RequestBody Product product) {
        boolean result = productService.createProduct(product);
        return result ? Result.success(true) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 更新产品
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        boolean result = productService.updateProduct(product);
        return result ? Result.success(true) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 上下架产品
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean result = productService.updateStatus(id, status);
        return result ? Result.success(true) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 获取热门产品
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Product> products = productService.getHotProducts(limit);
        return Result.success(products);
    }

    /**
     * 根据类型获取产品列表
     */
    @GetMapping("/type/{type}")
    public Result<List<Product>> getProductsByType(@PathVariable Integer type) {
        List<Product> products = productService.getProductsByType(type);
        return Result.success(products);
    }
}