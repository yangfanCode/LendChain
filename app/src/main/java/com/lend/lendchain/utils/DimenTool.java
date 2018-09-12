package com.lend.lendchain.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by MUCH on 2016/7/14.
 * 这个方法是用来适配不同尺寸
 * 以720为基数   720P屏幕
 */
public class DimenTool {

    public static void gen() {

        //以此文件夹下的dimens.xml文件内容为初始值参照

        File file = new File("./app/src/main/res/values/dimens.xml");

        BufferedReader reader = null;

        StringBuilder sw120 = new StringBuilder();

        StringBuilder sw240 = new StringBuilder();

        StringBuilder sw320 = new StringBuilder();

        StringBuilder sw480 = new StringBuilder();

        StringBuilder sw600 = new StringBuilder();

        StringBuilder sw720 = new StringBuilder();

        StringBuilder sw800 = new StringBuilder();

        StringBuilder sw820 = new StringBuilder();

        StringBuilder sw1080 = new StringBuilder();

        try {

            System.out.println("生成不同分辨率：");

            reader = new BufferedReader(new FileReader(file));

            String tempString;

            int line = 1;

            // 一次读入一行，直到读入null为文件结束

            while ((tempString = reader.readLine()) != null) {

                if (tempString.contains("</dimen>")) {

                    //tempString = tempString.replaceAll(" ", "");

                    String start = tempString.substring(0, tempString.indexOf(">") + 1);

                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);

                    //截取<dimen></dimen>标签内的内容，从>右括号开始，到左括号减2，取得配置的数字

                    Double num = Double.parseDouble

                            (tempString.substring(tempString.indexOf(">") + 1,

                                    tempString.indexOf("</dimen>") - 2));

                    //根据不同的尺寸，计算新的值，拼接新的字符串，并且结尾处换行。

                    sw120.append(start).append(num * 0.375).append(end).append("\r\n");

                    sw240.append(start).append(num * 0.75).append(end).append("\r\n");

                    sw320.append(start).append(num * 1).append(end).append("\r\n");

                    sw480.append(start).append(num * 1.5).append(end).append("\r\n");
//
//                    sw600.append(start).append(num * 3.75).append(end).append("\r\n");
//
//                    sw720.append(start).append(num * 4.5).append(end).append("\r\n");
//
//                    sw800.append(start).append(num * 5).append(end).append("\r\n");
//
//                    sw820.append(start).append(num * 5.125).append(end).append("\r\n");
//
//                    sw1080.append(start).append(num * 6.75).append(end).append("\r\n");

                } else {

                    sw120.append(tempString).append("");

                    sw240.append(tempString).append("");

                    sw320.append(tempString).append("");

                    sw480.append(tempString).append("");

                    sw600.append(tempString).append("");

                    sw720.append(tempString).append("");

                    sw800.append(tempString).append("");

                    sw820.append(tempString).append("");

                    sw1080.append(tempString).append("");

                }
                line++;
            }

            reader.close();

            System.out.println("<!--  sw240 -->");

            System.out.println(sw240);

            System.out.println("<!--  sw480 -->");

            System.out.println(sw480);

            System.out.println("<!--  sw600 -->");

            System.out.println(sw600);

            System.out.println("<!--  sw720 -->");

            System.out.println(sw720);

            System.out.println("<!--  sw800 -->");

            System.out.println(sw800);

            System.out.println("<!--  sw820 -->");

            System.out.println(sw820);

            File file9 = new File("./app/src/main/res/values-sw120dp-port");

            file9.mkdirs();

            File file1 = new File("./app/src/main/res/values-sw240dp-port");

            file1.mkdirs();

            File file8= new File("./app/src/main/res/values-sw320dp-port");

            file8.mkdirs();

            File file2 = new File("./app/src/main/res/values-sw480dp-port");

            file2.mkdirs();

//            File file3 = new File("./app/src/main/res/values-sw600dp-port");
//
//            file3.mkdirs();
//
//            File file4 = new File("./app/src/main/res/values-sw720dp-port");
//
//            file4.mkdirs();
//
//            File file5 = new File("./app/src/main/res/values-sw800dp-port");
//
//            file5.mkdirs();
//
//            File file6 = new File("./app/src/main/res/values-sw820dp-port");
//
//            file6.mkdirs();
//
//            File file7 = new File("./app/src/main/res/values-sw1080dp-port");
//
//            file7.mkdirs();
            String sw120file = "./app/src/main/res/values-sw120dp-port/dimens.xml";

            String sw240file = "./app/src/main/res/values-sw240dp-port/dimens.xml";

            String sw320file = "./app/src/main/res/values-sw320dp-port/dimens.xml";

            String sw480file = "./app/src/main/res/values-sw480dp-port/dimens.xml";

            String sw600file = "./app/src/main/res/values-sw600dp-port/dimens.xml";

            String sw720file = "./app/src/main/res/values-sw720dp-port/dimens.xml";

            String sw800file = "./app/src/main/res/values-sw800dp-port/dimens.xml";

            String sw820file = "./app/src/main/res/values-sw820dp-port/dimens.xml";
            
            String sw1080file = "./app/src/main/res/values-sw1080dp-port/dimens.xml";

            //将新的内容，写入到指定的文件中去

            writeFile(sw120file, sw120.toString());

            writeFile(sw240file, sw240.toString());

            writeFile(sw320file, sw320.toString());

            writeFile(sw480file, sw480.toString());
//
//            writeFile(sw600file, sw600.toString());
//
//            writeFile(sw720file, sw720.toString());
//
//            writeFile(sw800file, sw800.toString());
//
//            writeFile(sw820file, sw820.toString());
//
//            writeFile(sw1080file, sw1080.toString());

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (reader != null) {

                try {

                    reader.close();

                } catch (IOException e1) {

                    e1.printStackTrace();

                }

            }

        }

    }

    /**
     * 写入方法
     */

    public static void writeFile(String file, String text) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
    }

    public static void main(String[] args) {
        gen();
    }
}