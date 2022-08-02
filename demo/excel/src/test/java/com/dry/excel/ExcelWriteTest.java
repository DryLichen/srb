package com.dry.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.dry.excel.dto.ExcelStudentDTO;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 测试 easyexcel 写出功能
 */
public class ExcelWriteTest {
    @Test
    public void simpleWriteXlsx(){
        // String fileName = "D:\\java\\simpleWrite.xlsx";
        File file = new File("./simpleWrite.xlsx");
        // 需要指定数据传输对象
        EasyExcel.write(file, ExcelStudentDTO.class).sheet("模板").doWrite(data());
    }

    @Test
    public void simpleWriteXls(){
        File file = new File("simpleWrite.xls");
        EasyExcel.write(file, ExcelStudentDTO.class).excelType(ExcelTypeEnum.XLS).sheet("模板").doWrite(data());
    }

    // 生成测试用数据
    private List<ExcelStudentDTO> data(){
        List<ExcelStudentDTO> excelStudentDtos = new ArrayList<ExcelStudentDTO>();

        for( int i = 0; i < 100; i++){
            ExcelStudentDTO excelStudentDto = new ExcelStudentDTO();
            excelStudentDto.setName("Dry" + (i + 1));
            excelStudentDto.setBirthday(new Date());
            excelStudentDto.setSalary((double) (28649 + i));
            excelStudentDtos.add(excelStudentDto);
        }

        return excelStudentDtos;
    }
}
