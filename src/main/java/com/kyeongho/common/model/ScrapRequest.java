package com.kyeongho.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
@Data
@AllArgsConstructor(staticName = "of")
public class ScrapRequest {

   private String name;
   private String regNo;

   @Override
   public String toString() {
      return new ToStringBuilder(this)
              .append("name", name)
              .append("regNo", "[PROTECTED]")
              .toString();
   }
}
