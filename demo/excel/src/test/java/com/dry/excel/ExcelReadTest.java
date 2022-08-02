package com.dry.excel;

import com.alibaba.excel.EasyExcel;
import com.dry.excel.dto.ExcelStudentDTO;
import com.dry.excel.listener.ExcelStudentDTOListener;
import org.junit.Test;

import java.io.File;

public class ExcelReadTest {

    @Test
    public void simpleReadXlsx(){
        File file = new File("simpleWrite.xlsx");
        EasyExcel.read(file, ExcelStudentDTO.class, new ExcelStudentDTOListener()).sheet("模板").doRead();
    }
}
