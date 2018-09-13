package org.lingg.learn.poi;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DealExcel {

    public final static String url="jdbc:mysql://10.1.2.26:3306/cloudplat?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
    public final static String user="dba";
    public final static String password="dell_dba";

    public final static String filepath="D:\\aaabbb.xls";

    public static void main(String[] args) throws  Exception{

        Connection conn = null;
        Statement stat = null;
        ResultSet resultSet = null;

        Class.forName("com.mysql.jdbc.Driver");
        conn=(Connection) DriverManager.getConnection(url, user, password);
        stat = (Statement) conn.createStatement();

        String sq =
                "select org_name,customer_id,name,id_number,org.org_id from crm_customer cust\n" +
                "left join asun_organization org on cust.org_id = org.org_id\n" +
                " where id_number = ";


        System.out.println(filepath);
        InputStream is = new FileInputStream(filepath);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

       // for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(1);
//            if (hssfSheet == null) {
//                continue;
//            }

            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    HSSFCell name = hssfRow.getCell(0);
                    HSSFCell idNumber = hssfRow.getCell(1);

                    HSSFCell cell = hssfRow.getCell(3);

                    resultSet = stat.executeQuery(sq + "'"+idNumber.getStringCellValue()+"'");
                    String orgname = "";
                    while (resultSet.next()) {
                         orgname = resultSet.getString("org_name");
                    }
                    cell.setCellValue(new HSSFRichTextString(orgname));
                    System.out.println(name.getStringCellValue()+"\t\t"+idNumber.getStringCellValue()+"\t\t"+orgname);
                }
            }
       // }



//        步骤五 : 将最新的 Excel 内容写回到原始 Excel 文件中
// 将最新的 Excel 数据写回到原始 Excel 文件（就是D盘那个 Excel 文件）中
// 首先要创建一个原始Excel文件的输出流对象！
        FileOutputStream excelFileOutPutStream = new FileOutputStream("D:/employees.xls");
// 将最新的 Excel 文件写入到文件输出流中，更新文件信息！
        hssfWorkbook.write(excelFileOutPutStream);
        // 执行 flush 操作， 将缓存区内的信息更新到文件上
        excelFileOutPutStream.flush();
// 使用后，及时关闭这个输出流对象， 好习惯，再强调一遍！
        excelFileOutPutStream.close();




        is.close();
        hssfWorkbook.close();

        try {
            if(null != conn) {
                conn.close();
            }
            if(null != stat){
                stat.close();
            }
            if(null != resultSet){
                resultSet.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}