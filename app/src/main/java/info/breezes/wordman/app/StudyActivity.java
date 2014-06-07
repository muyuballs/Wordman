package info.breezes.wordman.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import info.breezes.orm.QueryAble;
import info.breezes.wordman.db.ClassTable;
import info.breezes.wordman.db.ClasswordsTable;
import info.breezes.wordman.db.StudyRecord;
import info.breezes.wordman.db.WordTable;
import info.breezes.wordman.media.CacheSoundPool;
import info.breezes.wordman.utils.DateUtils;

import java.util.Date;


public class StudyActivity extends Activity {

    private String classId;
    private int type;
    private ClassTable classTable;
    private QueryAble<WordTable> queryAble;
    private QueryAble<ClasswordsTable> reviewQueryAble;
    private WordTable currentWord;
    private ClasswordsTable currentClasswordsTable;

    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private EditText editText;
    private ImageView imageView;

    private CacheSoundPool soundPool;

    private int word_day = 20;
    private int lastSoundId;

    private String currentDay;
    private StudyRecord todayRecord;

    private int autoSpeech = 0;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    return checkInput();
                }
                return false;
            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(currentWord.pron)) {
                    soundPool.unloadSound(lastSoundId);
                    lastSoundId = soundPool.loadFromNet(currentWord.pron);
                }
            }
        });

        soundPool = new CacheSoundPool(1, WordmanApplication.current.getAudioCache());
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("Study Sound", sampleId + "," + status);
                soundPool.play(sampleId, 1, 1, 0, 0, 1);
            }
        });

        Intent intent = getIntent();
        classId = intent.getStringExtra("CLASS");
        type = intent.getIntExtra("TYPE", StudyType.STUDY);

        classTable = WordmanApplication.current.getDbHelper().query(ClassTable.class).where("id", classId, "=").first();

        currentDay = DateUtils.format(new Date(), "yyyy-MM-dd");
        todayRecord = WordmanApplication.current.getDbHelper().query(StudyRecord.class).where("date", currentDay, "=").first();
        if (todayRecord == null) {
            todayRecord = new StudyRecord();
            todayRecord.date = currentDay;
            WordmanApplication.current.getDbHelper().insert(todayRecord);
        }

        autoSpeech = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("auto_speech", "0"));

        word_day = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("word_day", "20"));


        if (type == StudyType.STUDY) {
            setTitle("[学习]" + classTable.name);
        } else {
            setTitle("[复习]" + classTable.name);
        }
        loadWords();
        showNextWord();
    }

    private void loadWords() {
        if (type == StudyType.STUDY) {
            queryAble = WordmanApplication.current.getDbHelper().query(WordTable.class).where("classId", classId, "=").limit(classTable.learned, word_day - todayRecord.studyCount > 0 ? word_day - todayRecord.studyCount : word_day).execute();
        } else {
            reviewQueryAble = WordmanApplication.current.getDbHelper().query(ClasswordsTable.class).where("classId", classId, "=").orderBy("times", "desc").execute();
        }
    }

    private boolean showNextWord() {
        if (type == StudyType.STUDY) {
            if (queryAble.hasNext()) {
                currentWord = queryAble.next();
                displayCurrentWord();
                return true;
            } else {
                return false;
            }
        } else {
            if (reviewQueryAble.hasNext()) {
                currentClasswordsTable = reviewQueryAble.next();
                currentWord = WordmanApplication.current.getDbHelper().query(WordTable.class).where("id", currentClasswordsTable.wordId, "=").first();
                displayCurrentWord();
                return true;
            }
            return false;
        }
    }

    private void displayCurrentWord() {
        textView.setText(currentWord.word);
        textView1.setText(currentWord.phonic);
        textView2.setText(currentWord.para);
        switch (autoSpeech) {
            case 2:
                soundPool.unloadSound(lastSoundId);
                lastSoundId = soundPool.loadFromNet(currentWord.pron);
                break;
            case 1:
                if (WordmanApplication.current.isWifiConnected()) {
                    soundPool.unloadSound(lastSoundId);
                    lastSoundId = soundPool.loadFromNet(currentWord.pron);
                }
                break;
        }
    }

    private boolean checkInput() {
        String str2 = editText.getText().toString();
        if (currentWord.word.equals(str2)) {
            if (type == StudyType.STUDY) {
                classTable.learned++;
                todayRecord.studyCount++;
                WordmanApplication.current.getDbHelper().update(classTable);
                WordmanApplication.current.getDbHelper().update(todayRecord);
                ClasswordsTable cwt = WordmanApplication.current.getDbHelper().query(ClasswordsTable.class).where("classId", classId, "=").and("wordId", currentWord.id, "=").first();
                if (cwt == null) {
                    cwt = new ClasswordsTable();
                    cwt.classId = classId;
                    cwt.wordId = currentWord.id;
                    cwt.times = 1;
                    WordmanApplication.current.getDbHelper().insert(cwt);
                } else {
                    cwt.times++;
                    WordmanApplication.current.getDbHelper().update(cwt);
                }
            } else {
                todayRecord.reviewCount++;
                WordmanApplication.current.getDbHelper().update(todayRecord);
                ClasswordsTable cwt = WordmanApplication.current.getDbHelper().query(ClasswordsTable.class).where("classId", classId, "=").and("wordId", currentWord.id, "=").first();
                if (cwt == null) {
                    cwt = new ClasswordsTable();
                    cwt.classId = classId;
                    cwt.wordId = currentWord.id;
                    cwt.times = 1;
                    WordmanApplication.current.getDbHelper().insert(cwt);
                } else {
                    cwt.times++;
                    WordmanApplication.current.getDbHelper().update(cwt);
                }
            }
            if (showNextWord()) {
                editText.setText("");
                return true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("今天已经很用功了，明天再学，贵在坚持.");
                builder.setTitle("提示");
                builder.setPositiveButton(R.string.rest, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.goon, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadWords();
                        editText.setText("");
                        if (!showNextWord()) {
                            showLessonFinished();
                        }
                    }
                });
                builder.create().show();
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        todayRecord.time+=(System.currentTimeMillis()-startTime);
        WordmanApplication.current.getDbHelper().update(todayRecord);
        super.onPause();
    }

    @Override
    protected void onResume() {
        startTime=System.currentTimeMillis();
        super.onResume();
    }

    private void showLessonFinished() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(classTable.name + " 已经全部学完");
        builder.setTitle("提示");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.study, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
