package com.lend.lendchain.bean;

import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class BannerModel {
    public Data data;
    public static class Data{
        public List<Detail> en;
        public List<Detail> cn;
        public List<Detail> ko;
    }
    public static class Detail{
        public String title;
        public String href;
        public String url;
        public int login;

        public boolean isNeedLogin(){
            return login==1;
        }
    }
}
