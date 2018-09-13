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

//风险筛查05（杨炳红会要）
public class FengxianshaichaReportEveryMonth {

    public final static String url="jdbc:mysql://10.1.2.26:3306/cloudplat?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
    public final static String user="dba";
    public final static String password="dell_dba";

    public final static String starttime = "20180801";
    public final static String endtime = "20180901";

    public final static String fileName = "201808风险筛查";

    public final static String outputFile="D:\\"+fileName+".xlsx";

    public final static String sql = "\n" +
            "SELECT  CASE WHEN cc.`org_type` IN ('ASUN','AXIU','PARTNER','CONF') THEN '安信' ELSE '安联' END ywgs,IFNULL(og.alias_name,og.org_name) org_name,b.ref_biz_code,cc.name customer_name ,bk.`org_name` bankname,\n" +
            "DATE_FORMAT(od.`create_time`,'%Y-%m-%d') sdrq,b.risk_score,DATE_FORMAT(SUBSTR(b.receive_time ,1,8),'%Y-%m-%d') cxsj\n" +
            "FROM (SELECT\n" +
            "  a.ref_biz_code  ,\n" +
            "  c.risk_score,\n" +
            "  a.`receive_time`,\n" +
            "  a.`customer_id`\n" +
            "FROM crm_cis_report b\n" +
            "  LEFT JOIN crm_cis_reports a\n" +
            "    ON b.bat_no = a.bat_no\n" +
            "  LEFT JOIN crm_cis_spoofing_info c\n" +
            "    ON b.spoofing_info_id = c.id\n" +
            "WHERE a.query_type = '25212' \n" +
            ") b\n" +
            "LEFT JOIN crm_credit_source a ON b.`ref_biz_code`=a.ref_biz_code  AND a.`apply_reason`='P' " +
            "LEFT JOIN biz_loan_order od ON b.`ref_biz_code`=od.`order_code`\n" +
            "INNER JOIN crm_customer cc ON b.customer_id=cc.customer_id\n" +
            "INNER JOIN asun_organization og ON cc.org_id=og.org_id\n" +
            "LEFT JOIN asun_organization bk ON a.`creditor_code`=bk.org_id\n" +
            "WHERE SUBSTR(b.receive_time ,1,8)>='"+starttime+"' AND SUBSTR(b.receive_time ,1,8)<'"+endtime+"' " +
            "AND cc.`org_type` IN ('ASUN','AXIU','PARTNER','CONF','ALAN');\n";

    public static void main(String[] args) {
        System.out.println("start create xls");
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
            XSSFSheet sheet=workbook.createSheet(fileName);
            XSSFRow row = sheet.createRow((short)0);
            XSSFCell cell=null;



            String[] headers = {"业务归属",	"机构",	"业务编码",	"客户姓名",	"按揭银行",	"上单日期",	"评分",	"查询日期"};
            for(int hindex = 0; hindex < headers.length; hindex++){
                cell=row.createCell((short)hindex);
                cell.setCellValue(headers[hindex]);
            }

//            cell=row.createCell((short)0);
//            cell.setCellValue("按揭模式");
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

                for(int hindex = 0; hindex < headers.length; hindex++){
                    cell=row.createCell(hindex);
                    cell.setCellValue(resultSet.getString(hindex+1));
                }

//                cell=row.createCell(0);
//                cell.setCellValue(resultSet.getString(1));
//                cell=row.createCell(1);
//                cell.setCellValue(resultSet.getString(2));
//                cell=row.createCell(2);
//                cell.setCellValue(resultSet.getString(3));
                i++;
             }
             fout = new FileOutputStream(outputFile);
            workbook.write(fout);
            fout.flush();
            System.out.println(" create xls completed ");
            System.out.println(" output file : "+outputFile);
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