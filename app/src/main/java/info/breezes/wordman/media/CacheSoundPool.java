package info.breezes.wordman.media;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import info.breezes.wordman.utils.DigestUtils;
import info.breezes.wordman.utils.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jianxingqiao on 14-6-7.
 */
public class CacheSoundPool extends SoundPool {
    private AtomicInteger atomicInteger;
    private HashMap<Integer, Integer> sampleId;
    private String cacheDir;
    private File cacheFile;

    public CacheSoundPool(int maxStreams, String cacheDir) {
        super(maxStreams, AudioManager.STREAM_SYSTEM, 0);
        this.cacheDir = cacheDir;
        cacheFile = new File(cacheDir);
        if (!cacheFile.exists() || !cacheFile.isDirectory()) {
            throw new RuntimeException("CacheDir Not Exists.");
        }
        sampleId = new HashMap<Integer, Integer>();
        atomicInteger = new AtomicInteger(0);
    }

    public int loadFromNet(final String uri) {
        final int id = atomicInteger.getAndIncrement();
        String cacheFileName = DigestUtils.md5(uri);
        final File file = checkCache(cacheFileName);
        if (file.exists()) {
            Log.d("CacheSoundPool", "Load From Cache:" + file.getAbsolutePath());
            int tid = load(file.getAbsolutePath(), 0);
            sampleId.put(id, tid);
        } else {
            Log.d("CacheSoundPool", "Download From Net.....");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) new URL(uri).openConnection();
                        InputStream inputStream = urlConnection.getInputStream();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        StreamUtils.copyStream(inputStream, fileOutputStream);
                        inputStream.close();
                        fileOutputStream.close();
                        Log.d("CacheSoundPool", "Download From Net Done.");
                        int tid = load(file.getAbsolutePath(), 0);
                        sampleId.put(id, tid);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
        return id;
    }

    public void unloadSound(int id){
        if(sampleId.containsKey(id)) {
            int tid = sampleId.remove(id);
            unload(tid);
        }
    }


    private File checkCache(String cacheFileName) {
        return new File(cacheFile, cacheFileName);
    }



}
