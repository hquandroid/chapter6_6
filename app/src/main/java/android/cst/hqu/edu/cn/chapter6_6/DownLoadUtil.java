package android.cst.hqu.edu.cn.chapter6_6;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.RandomAccess;

class DownLoadUtil {
    private String mUrl;
    private String mFileLoc;
    private int mthreadNum;
    private DownThread[] mthreads;
    private long mFileSize;

    public DownLoadUtil(String url, String fileLoc, int threadNum) {
        this.mUrl=url;
        this.mthreadNum=threadNum;
        mthreads=new DownThread[threadNum];
        this.mFileLoc=fileLoc;
    }

    public void start() throws Exception{
        URL url=new URL(mUrl);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5*1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept","image/gif,image/jpeg,image/pjpeg,image/pjpeg,*/*");
        conn.setRequestProperty("Accept-Language","zh-CN");
        conn.setRequestProperty("Charset","UTF-8");
        conn.setRequestProperty("Connection","Keep-Alive");
        mFileSize=conn.getContentLength();
        conn.disconnect();
        int currentPartSize=(int)(mFileSize/mthreadNum)+1;
        RandomAccessFile file=new RandomAccessFile(mFileLoc,"rw");
        file.setLength(mFileSize);
        file.close();

        for(int i=0;i<mthreadNum;i++){
            int startPos=i*currentPartSize;
            RandomAccessFile currentPart=new RandomAccessFile(mFileLoc,"rw");
            currentPart.seek(startPos);
            mthreads[i]=new DownThread(mUrl,startPos,currentPartSize,currentPart);
            mthreads[i].start();

        }


    }

    public double getCompleteRate() {
        long sumSize=0;
        for(int i=0;i<mthreadNum;i++){
            sumSize+=mthreads[i].getMLength();
        }
        return sumSize*1.0/mFileSize;
    }
}

class DownThread extends Thread{
    public long mLength=0;
    private long mStartPos;
    private String mUrl;
    private RandomAccessFile mCurrentPart;
    private long mCurrentPartSize;
    public DownThread(String url,long startPos, long currentPartSize, RandomAccessFile currentPart) {
        this.mUrl=url;
        this.mStartPos=startPos;
        this.mCurrentPartSize=currentPartSize;
        this.mCurrentPart=currentPart;
    }

    public  long getMLength(){
        return mLength;
    }
    @Override
    public void run() {
        try {
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg,image/pjpeg,*/*");
            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty("Charset", "UTF-8");
            InputStream is = conn.getInputStream();
            skipFully(is, this.mStartPos);
            byte[] buf = new byte[1024];
            int hasRead = 0;
            while (mLength < mCurrentPartSize && (hasRead = is.read(buf)) > 0) {
                mCurrentPart.write(buf, 0, hasRead);
                mLength += hasRead;

            }
            mCurrentPart.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void skipFully(InputStream in,long bytes) throws IOException {
        long remainning=bytes;
        long len=0;
        while(remainning>0){
            len=in.skip(remainning);
            remainning-=len;
        }
    }

}
