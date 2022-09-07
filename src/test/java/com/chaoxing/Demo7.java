package com.chaoxing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.nio.file.Paths;

public class Demo7 {
    private Integer ids[] = { 1, 2, 3 };
    private String citys[] = { "青岛", "南京", "上海" };
    // private String descs[]={
    // "青岛是个美丽的城市。",
    // "南京是个有文化的城市。",
    // "上海市个繁华的城市。"
    // };
    private String descs[] = { "青岛是个美丽的城市。",
            "南京是一个文化的城市南京，简称宁，是江苏省会，地处中国东部地区，长江下游，濒江近海。全市下辖11个区，总面积6597平方公里，2013年建成区面积752.83平方公里，常住人口818.78万，其中城镇人口659.1万人。[1-4] “江南佳丽地，金陵帝王州”，南京拥有着6000多年文明史、近2600年建城史和近500年的建都史，是中国四大古都之一，有“六朝古都”、“十朝都会”之称，是中华文明的重要发祥地，历史上曾数次庇佑华夏之正朔，长期是中国南方的政治、经济、文化中心，拥有厚重的文化底蕴和丰富的历史遗存。[5-7] 南京是国家重要的科教中心，自古以来就是一座崇文重教的城市，有“天下文枢”、“东南第一学”的美誉。截至2013年，南京有高等院校75所，其中211高校8所，仅次于北京上海；国家重点实验室25所、国家重点学科169个、两院院士83人，均居中国第三。[8-10]",
            "上海市个繁华的城市。" };

    private FSDirectory dir;

    /**
     * 每次都生成索引文件
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        dir = FSDirectory.open(Paths.get("/Users/hechen/luceneproject/lucenedemo"));
        IndexWriter indexWriter = getIndexWriter();
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new IntField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("city", citys[i], Field.Store.YES));
            doc.add(new TextField("desc", descs[i], Field.Store.YES));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();
    }

    /**
     * 获取索引输出流
     * 
     * @return
     * @throws Exception
     */
    private IndexWriter getIndexWriter() throws Exception {
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        return new IndexWriter(dir, conf);
    }

    /**
     * luke查看索引生成
     * 
     * @throws Exception
     */
    @Test
    public void testIndexCreate() throws Exception {

    }

    /**
     * 测试高亮
     * 
     * @throws Exception
     */
    @Test
    public void testHeight() throws Exception {
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        QueryParser parser = new QueryParser("desc", analyzer);
        // Query query = parser.parse("南京文化");
        Query query = parser.parse("南京文明");
        TopDocs hits = searcher.search(query, 100);

        // 查询得分项
        QueryScorer queryScorer = new QueryScorer(query);
        // 得分项对应的内容片段
        SimpleSpanFragmenter fragmenter = new SimpleSpanFragmenter(queryScorer);
        // 高亮显示的样式
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span color='red'><b>", "</b></span>");
        // 高亮显示对象
        Highlighter highlighter = new Highlighter(htmlFormatter, queryScorer);
        // 设置需要高亮显示对应的内容片段
        highlighter.setTextFragmenter(fragmenter);

        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String desc = doc.get("desc");
            if (desc != null) {
                // tokenstream是从doucment的域（field)中抽取的一个个分词而组成的一个数据流，用于分词。
                TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));
                System.out.println("高亮显示的片段：" + highlighter.getBestFragment(tokenStream, desc));
            }
            System.out.println("所有内容：" + desc);
        }

    }

}