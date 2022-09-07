package com.chaoxing;

/**
 * 查询索引测试
 * @author Administrator
 *
 */
public class Demo2 {
    public static void main(String[] args) {
        String indexDir = "/Users/hechen/luceneproject/lucenedemo";
        String q = "qingdao";
        try {
            IndexUse.search(indexDir, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}