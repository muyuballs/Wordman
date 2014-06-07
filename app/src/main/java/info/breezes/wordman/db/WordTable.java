package info.breezes.wordman.db;

import info.breezes.orm.annotation.Column;
import info.breezes.orm.annotation.Table;

/**
 * Created by jianxingqiao on 14-6-6.
 */
@Table(name = "word",comment = "单词表")
public class WordTable {
    @Column(length = 32,notNull = true,number = 1,primaryKey = true)
    public String id;
    @Column(length = 55,notNull = true,number = 2)
    public String word;
    @Column(length = 32,notNull = true,number = 3)
    public String classId;
    @Column(length = 255,notNull = true,comment = "音标",number = 4)
    public String phonic;
    @Column(length = 255,notNull = true,comment = "语音路径",number = 5)
    public String pron;
    @Column(length = 512,notNull = true,comment = "释义",number = 6)
    public String para;
    @Column(length = 512,notNull = true,comment = "构词法",number = 7)
    public String build;
    @Column(length = 1024,notNull = true,comment = "JSON格式例句",number = 8)
    public String example;
}
