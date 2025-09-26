package com.sifang.insurance.underwriting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.underwriting.entity.Underwriting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 核保Mapper接口
 */
@Mapper
public interface UnderwritingMapper extends BaseMapper<Underwriting> {
}