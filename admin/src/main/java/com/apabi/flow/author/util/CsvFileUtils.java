package com.apabi.flow.author.util;

import com.apabi.flow.author.constant.AuthorTypeEnum;
import com.apabi.flow.author.constant.DieOver50Enum;
import com.apabi.flow.author.constant.SexEnum;
import com.apabi.flow.author.constant.TitleTypeEnum;
import com.apabi.flow.author.model.Author;
import com.apabi.flow.common.UUIDCreater;
import com.csvreader.CsvReader;
import org.apache.poi.ss.formula.functions.T;

import javax.management.DescriptorKey;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述： <br>
 * <csv 文件操作工具类>
 *
 * @author supeng
 * @date 2018/8/30 14:32
 * @since 1.0.0
 */
public class CsvFileUtils {

    public static List<Author> getAuthorsFromCSV(InputStream inputStream) {

        List<Author> list = null;

        try {
            // 用来保存数据
            ArrayList<String[]> csvFileList = new ArrayList<>();
            // 创建CSV读对象 例如:CsvReader(文件路径，分隔符，编码格式);
            CsvReader reader = new CsvReader(inputStream, ',', Charset.forName("GBK"));
            // 跳过表头 如果需要表头的话，这句可以忽略
            reader.readHeaders();

            System.out.println("记录数： " + reader.getCurrentRecord());
            // 逐行读入除表头的数据
            while (reader.readRecord()) {
                csvFileList.add(reader.getValues());
            }
            reader.close();

            list = new ArrayList<>();

            // 遍历读取的CSV文件
            for (int row = 0; row < csvFileList.size(); row++) {

                list.add(currentAuthorStrategy(csvFileList.get(row)));

                //初始
                //list.add(myAuthorStrategy(csvFileList.get(row)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Author currentAuthorStrategy(String[] strings) {
        Author author = new Author();
        author.setId(UUIDCreater.nextId());

        //0
        author.setTitle(strings[0]);
        //todo 类型
        /*author.setTitleType(TitleTypeEnum.getEnum(Integer.valueOf(strings[1])));
        author.setStartDate(strings[2]);
        author.setEndDate(strings[3]);
        author.setNationalityCode(strings[4]);
        author.setPersonId(strings[5]);*/
        //2
        author.setBirthday(strings[2]);
        //3
        author.setDeathDay(strings[3]);
        /*author.setSexCode(SexEnum.getEnum(Integer.valueOf(strings[8])));
        author.setType(AuthorTypeEnum.getEnum(Integer.valueOf(strings[9])));
        author.setNationalCode(strings[10]);
        author.setQualificationCode(strings[11]);*/
        //1
        author.setDynastyName(strings[1]);
        /*author.setOriginCode(strings[13]);
        author.setCareerClassCode(strings[14]);
        author.setServiceAgency(strings[15]);
        author.setHeadPortraitPath(strings[16]);
        author.setSummary(strings[17]);
        author.setDieOver50(DieOver50Enum.getEnum(Integer.valueOf(strings[18])));
        author.setNlcAuthorId(strings[19]);*/

        author.setCreateTime(new Date());
        //author.setUpdateTime(null);
        //author.setOperator("");
        return author;
    }

    /**
     * 根据不同的 CSV 文件来生成 作者信息实体
     * @param strings
     * @return
     */
    private static Author myAuthorStrategy(String[] strings){
        Author author = new Author();
        author.setId(UUIDCreater.nextId());

        author.setTitle(strings[0]);
        //todo 类型
        author.setTitleType(TitleTypeEnum.getEnum(Integer.valueOf(strings[1])));
        author.setStartDate(strings[2]);
        author.setEndDate(strings[3]);
        author.setNationalityCode(strings[4]);
        author.setPersonId(strings[5]);
        author.setBirthday(strings[6]);
        author.setDeathDay(strings[7]);
        author.setSexCode(SexEnum.getEnum(Integer.valueOf(strings[8])));
        author.setType(AuthorTypeEnum.getEnum(Integer.valueOf(strings[9])));
        author.setNationalCode(strings[10]);
        author.setQualificationCode(strings[11]);
        author.setDynastyName(strings[12]);
        author.setOriginCode(strings[13]);
        author.setCareerClassCode(strings[14]);
        author.setServiceAgency(strings[15]);
        author.setHeadPortraitPath(strings[16]);
        author.setSummary(strings[17]);
        author.setDieOver50(DieOver50Enum.getEnum(Integer.valueOf(strings[18])));
        author.setNlcAuthorId(strings[19]);

        author.setCreateTime(new Date());
        //author.setUpdateTime(null);
        //author.setOperator("");
        return author;
    }


    public static void readCSV() {
        try {
            // 用来保存数据
            ArrayList<String[]> csvFileList = new ArrayList<>();
            // 定义一个CSV路径
            String csvFilePath = "C:/Users/supeng/Desktop/云加工/5-2著者卒于50年作者与作品6195条.csv";
            // 创建CSV读对象 例如:CsvReader(文件路径，分隔符，编码格式);
            CsvReader reader = new CsvReader(csvFilePath, ',', Charset.forName("GBK"));
            // 跳过表头 如果需要表头的话，这句可以忽略
            reader.readHeaders();

            int i = 0;
            // 逐行读入除表头的数据
            while (reader.readRecord()) {
                System.out.println(++i + "  :" + reader.getRawRecord());
                csvFileList.add(reader.getValues());
            }
            reader.close();

            // 遍历读取的CSV文件
            for (int row = 0; row < csvFileList.size(); row++) {
                // 取得第row行第0列的数据
                String cell = csvFileList.get(row)[0];
                System.out.println("------------>"+cell);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //readCSV();
    }
}
