package com.apabi.flow.thematic.util;

import java.util.Map;

/**
 * @Author pipi
 * @Date 2019-2-19 15:47
 **/
public class TestReadExcel {
    public static void main(String[] args) {
        int total = 0;
        int hitCount = 0;
        ReadExcelUtils readExcelUtils = new ReadExcelUtils("C:\\Users\\pirui\\Desktop\\a.xlsx");
        Map<Integer, Map<Object, Object>> data = readExcelUtils.getData();
        for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
            Map<Object, Object> value = entry.getValue();
            String meta_id = (String) value.get("meta_id");
            String isSame = (String) value.get("是否相同");
            total++;
            if ("是".equals(isSame)) {
                hitCount++;
            }
        }
        System.out.println(total);
        System.out.println(hitCount);
    }
}