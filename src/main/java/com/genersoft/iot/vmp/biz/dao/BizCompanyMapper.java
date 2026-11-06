package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizCompany;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BizCompanyMapper {

    @Insert("INSERT INTO biz_company (tenant_id, company_name, company_addr, region_code, uniform_social_credit_code, "
            + "legal_name, legal_phone_number, status) VALUES (#{tenantId}, #{companyName}, #{companyAddr}, #{regionCode}, "
            + "#{uniformSocialCreditCode}, #{legalName}, #{legalPhoneNumber}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(BizCompany company);

    @Update("UPDATE biz_company SET tenant_id=#{tenantId}, company_name=#{companyName}, company_addr=#{companyAddr}, "
            + "region_code=#{regionCode}, uniform_social_credit_code=#{uniformSocialCreditCode}, legal_name=#{legalName}, "
            + "legal_phone_number=#{legalPhoneNumber}, status=#{status} WHERE id=#{id}")
    int update(BizCompany company);

    @Delete("DELETE FROM biz_company WHERE id=#{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM biz_company WHERE id=#{id}")
    BizCompany selectById(@Param("id") Long id);

    @Select("<script>SELECT * FROM biz_company WHERE 1=1"
            + "<if test='keyword != null'> AND (company_name LIKE concat('%', #{keyword}, '%') "
            + "OR uniform_social_credit_code LIKE concat('%', #{keyword}, '%'))</if>"
            + "<if test='status != null'> AND status=#{status}</if>"
            + " ORDER BY id DESC</script>")
    List<BizCompany> selectList(@Param("keyword") String keyword, @Param("status") Integer status);

    @Select("<script>SELECT COUNT(*) FROM biz_company WHERE uniform_social_credit_code=#{creditCode}"
            + "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    int countByCreditCode(@Param("creditCode") String creditCode, @Param("excludeId") Long excludeId);
}
