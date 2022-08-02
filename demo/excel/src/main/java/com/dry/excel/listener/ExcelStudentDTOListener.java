package com.dry.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.dry.excel.dto.ExcelStudentDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * listener 无法被spring 管理，每次使用需要自己构造
 */
@Slf4j
public class ExcelStudentDTOListener extends AnalysisEventListener<ExcelStudentDTO> {

    // 每一条数据被读取解析时都会调用
    @Override
    public void invoke(ExcelStudentDTO excelStudentDTO, AnalysisContext analysisContext) {
        log.info("解析到一条数据：{}", excelStudentDTO);
    }

    // 所有数据都解析完毕后调用
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完毕！");
    }
}
