package com.dry.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.dry.srb.core.mapper.DictMapper;
import com.dry.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {
    /**
     * 每隔5条插入一次记录，实际中可以设置到3000
     */
    private static final int BATCH_COUNT = 5;
    List<ExcelDictDTO> list = new ArrayList<>();

    /**
     * Listener并不能被spring管理，如果要用到spring组件需要手动导入，比如用构造方法传入
     */
    private DictMapper dictMapper;
    public ExcelDictDTOListener(DictMapper dictMapper){
        this.dictMapper = dictMapper;
    }

    /**
     * 每读取一条数据都会调用 invoke()
     */
    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        log.info("解析到一条数据：{}", excelDictDTO);
        list.add(excelDictDTO);
        if(list.size() >= BATCH_COUNT){
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 收尾工作，防止遗漏数据
        saveData();
        log.info("成功插入所有数据！");
    }

    /**
     * 执行保存操作
     */
    private void saveData(){
        dictMapper.insertBatch(list);
        log.info("插入{}条数据成功！", list.size());
    }
}
