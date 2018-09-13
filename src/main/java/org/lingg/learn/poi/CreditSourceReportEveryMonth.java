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

public class CreditSourceReportEveryMonth {

    public final static String url="jdbc:mysql://10.1.2.26:3306/cloudplat?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
    public final static String user="dba";
    public final static String password="dell_dba";

    public final static String starttime = "2018-07-31 17:00:00";
    public final static String endtime = "2018-08-31 17:00:00";

    public final static String fileName = "201808征信查询明细";

    public final static String outputFile="D:\\"+fileName+".xlsx";

    public final static String sql = "\t\n" +
            "SELECT ar2.`display_value` ajms,#按揭模式\t\n" +
            "CASE WHEN a.credit_status ='P' THEN '等待征信返回'\t\n" +
            "     WHEN a.credit_status ='D' THEN '已删除的征信记录'\t\n" +
            "     WHEN a.credit_status='R' THEN '被驳回' \t\n" +
            "     WHEN a.credit_status='A' THEN '已正常返回'\t\n" +
            "     WHEN a.credit_status='T' THEN '已发送银行,等待银行返回'\t\n" +
            "     WHEN a.credit_status='F' THEN '发送银行失败'\t\n" +
            "     WHEN a.credit_status='N' THEN '信息或材料有误，3天后允许修改提交'\t\n" +
            "     WHEN a.credit_status='U' THEN '不通过'\t\n" +
            "     WHEN a.credit_status='C' THEN '已通融'\t\n" +
            "     END zt,\t#状态\n" +
            "     a.ref_biz_code ,#\t业务编号     \n" +
            "     e.org_name,#部门\t\n" +
            "     d.user_name,#业务员\t\n" +
            "     a.customer_name ,#姓名\t\n" +
            "     a.id_number,#身份证号\t\n" +
            "     po.re_name poxm,#配偶姓名\t\n" +
            "     po.re_id_number posfz,#\t配偶身份证号\n" +
            "cpy.re_Name cpyname ,#共同还款人姓名\t\n" +
            "cpy.re_id_number cpysfz,#身份证号1\t\n" +
            "cpypo.`re_name` cpypoxm,#共同还款人配偶姓名\t\n" +
            "cpypo.`re_id_number` xpyposfz, #身份证号2\t\n" +
            "CASE WHEN a.is_permitted=1 THEN '通过' \t\n" +
            "     WHEN a.is_permitted=0 THEN '不通过'\t\n" +
            "     END clyj,\t#处理意见\n" +
            "bk.org_name ,    #按揭银行\t\n" +
            "f.mobile_phone,#手机\t\n" +
            " REPLACE(REPLACE(a.remarks,CHAR(10),'<br>'),CHAR(13),'') remarks ,#\t备注\n" +
            "d.user_name ,#\t制单人\n" +
            "a.create_time ,\t#制单日期\n" +
            "a.reply_time\t#审核日期\n" +
            " FROM  crm_credit_source a \t\n" +
            "LEFT JOIN biz_loan_order b ON a.`ref_biz_code`=b.`order_code`\t\n" +
            " LEFT JOIN asun_resources ar2\t\n" +
            "    ON ar2.`res_index` = 'MORTGAGE_MODE'\t\n" +
            "    AND b.`loan_type` = ar2.`real_value`\t\n" +
            "  LEFT JOIN crm_customer c\t\n" +
            "    ON a.`customer_id` = c.`customer_id`\t\n" +
            "  LEFT JOIN asun_user d\t\n" +
            "    ON c.`manager_id` = d.`user_Id`\t\n" +
            "  LEFT JOIN asun_organization e\t\n" +
            "    ON d.`org_id` = e.`org_id`\t\n" +
            "  LEFT JOIN crm_customer f\t\n" +
            "  ON a.customer_id=f.customer_id\t\n" +
            "  LEFT JOIN\t\n" +
            "    crm_relationship po\t\n" +
            "    ON po.re_type IN ( '配偶' ,'夫妻')\t\n" +
            "    AND a.`customer_id` = po.customer_id\t\n" +
            "  LEFT JOIN\t\n" +
            "    ( SELECT * FROM crm_relationship WHERE is_corepay=1) cpy\t\n" +
            "    ON a.`customer_id` = cpy.customer_id AND a.corepay_id_num=cpy.re_id_number\t\n" +
            "  LEFT JOIN crm_relationship cpypo\t\n" +
            "    ON a.corepay_couple_id_num=cpypo.re_id_number AND a.customer_id=cpypo.customer_id\t\n" +
            "LEFT JOIN asun_organization bk ON a.creditor_code=bk.org_id    \t\n" +
            " WHERE a.creditor_code IN (1,2)  AND\t\n" +
            " ((a.create_time>='"+starttime+"' AND a.create_time<'"+endtime+"')\t\n" +
            "  OR (a.reply_time>='"+starttime+"' AND a.reply_time<'"+endtime+"') )\t\n" +
            "   AND a.apply_reason='P' \t\n";

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

            String[] headers = {"按揭模式",	"状态",	"业务编号"	,"部门"	,"业务员"	,"姓名",	"身份证号",	"配偶姓名",	"配偶身份证号",	"共同还款人姓名"	,"身份证号1",	"共同还款人配偶姓名",	"身份证号2",	"处理意见",	"按揭银行",	"手机"	,"备注",	"制单人",	"制单日期"	,"审核日期"};
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