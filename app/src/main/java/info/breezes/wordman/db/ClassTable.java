package info.breezes.wordman.db;

import info.breezes.orm.annotation.Column;
import info.breezes.orm.annotation.Table;


/**
 * Created by jianxingqiao on 14-6-6.
 */

@Table(name = "class", comment = "词库表")
public class ClassTable {
    @Column(length = 32, notNull = true, number = 1,primaryKey = true)
    public String id;
    @Column(length = 128, notNull = true, number = 2)
    public String name;
    @Column(comment = "单词数", defaultValue = "0", number = 3)
    public int size;
    @Column(defaultValue = "0", number = 4)
    public int state;
    @Column(defaultValue = "0", number = 5)
    public int times;
    @Column(defaultValue = "0", number = 6)
    public int selected;
    @Column(defaultValue = "0", number = 7)
    public int learned;
    @Column(defaultValue = "0", number = 8)
    public int finished;
}
