package info.breezes.wordman.db;

import info.breezes.orm.annotation.Column;
import info.breezes.orm.annotation.Table;

/**
 * Created by jianxingqiao on 14-6-7.
 */
@Table(name = "record")
public class StudyRecord {
    @Column(primaryKey = true,length = 11,number = 1)
    public String date;
    @Column(number = 2)
    public int studyCount;
    @Column(number = 3)
    public int reviewCount;
    @Column(number = 4)
    public long time;
}
