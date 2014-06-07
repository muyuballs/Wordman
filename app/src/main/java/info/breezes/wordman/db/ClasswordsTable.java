package info.breezes.wordman.db;

import info.breezes.orm.annotation.Column;
import info.breezes.orm.annotation.Table;

/**
 * Created by jianxingqiao on 14-6-6.
 */
@Table(name = "classwords", comment = "词库单词关联表")
public class ClasswordsTable {
    @Column(primaryKey = true,autoincrement = true)
    public int id;
    @Column(length = 32, notNull = true,number = 2)
    public String wordId;
    @Column(length = 32, notNull = true,number = 3)
    public String classId;
    @Column(number = 4)
    public int times;

}
