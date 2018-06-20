package org.lingg.learn.poi;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class ReadExcelFromDB {

    public final static String outputFile="D:\\erp"+System.currentTimeMillis()+".xlsx";

    public final static String url="jdbc:mysql://10.1.2.26:3306/cloudplat?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";

    public final static String user="dba";

    public final static String password="dell_dba";

    public final static String sql = "SELECT b.BUSINESS_KEY_, u.user_name,d.text_ \n" +
            "FROM biz_loan_order o INNER JOIN flow_hi_procinst b ON b.BUSINESS_KEY_ = o.order_code\n" +
            "INNER JOIN crm_customer cust ON cust.customer_id = o.customer_id AND cust.org_type = 'ALAN'\n" +
            "INNER JOIN flow_re_procdef a  ON a.id_=b.PROC_DEF_ID_\n" +
            "INNER JOIN flow_hi_actinst c ON  b.`PROC_INST_ID_`=c.`PROC_INST_ID_` AND c.act_id_='directorAudit'\n" +
            "INNER JOIN asun_user u ON c.assignee_ = u.user_id\n" +
            "INNER JOIN flow_hi_detail d ON d.act_inst_id_ = c.id_ AND d.name_='auditComment'\n" +
            "\n" +
            "WHERE a.key_='applyLoanOrder' -- AND b.BUSINESS_KEY_= '20180614A0000055'\n" +
            "\n" +
            "AND NOT EXISTS (SELECT 1 FROM flow_hi_actinst d WHERE c.PROC_INST_ID_=d.PROC_INST_ID_ AND d.`ACT_ID_`='directorAudit' AND c.`start_time_`>d.`start_time_`)\n" +
            "AND NOT EXISTS (SELECT 1 FROM flow_hi_procinst e,flow_re_procdef f  WHERE b.BUSINESS_KEY_=e.BUSINESS_KEY_ AND f.id_=e.PROC_DEF_ID_\n" +
            " AND b.start_time_<e.start_time_ AND f.key_='applyLoanOrder')\n" +
            "\n" +
            "AND d.text_ IS NOT NULL AND d.`TEXT_` <>''";

    public static void main(String[] args) {

        Connection conn = null;
        Statement stat = null;
        ResultSet resultSet = null;
        FileOutputStream fout = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
             conn=(Connection) DriverManager.getConnection(url, user, password);
             stat = (Statement) conn.createStatement();
             resultSet = stat.executeQuery(sql);
            XSSFWorkbook workbook=new XSSFWorkbook();
            XSSFSheet sheet=workbook.createSheet("countryDB");
            XSSFRow row = sheet.createRow((short)0);
            XSSFCell cell=null;
//            cell=row.createCell((short)0);
//            cell.setCellValue("code");
//            cell=row.createCell((short)1);
//            cell.setCellValue("shortname");
//            cell=row.createCell((short)2);
//            cell.setCellValue("name");
//            cell=row.createCell((short)3);
//            cell.setCellValue("englishname");
            int i=1;
            while(resultSet.next())
            {
                row=sheet.createRow(i);
                cell=row.createCell(0);
                cell.setCellValue(resultSet.getString(1));
                cell=row.createCell(1);
                cell.setCellValue(resultSet.getString(2));
                cell=row.createCell(2);
                cell.setCellValue(resultSet.getString(3));
                i++;
             }
             fout = new FileOutputStream(outputFile);
            workbook.write(fout);
            fout.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
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
                if(null != fout){
                    fout.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}