package com.example.download_demo.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.download_demo.bean.DownloadInfo;

public class DownloadHttpTool {
	private static final String TAG = DownloadHttpTool.class.getSimpleName();
	/**
	 * 线程数量
	 */
	private int threadCount;
	private String strUrl;
	private Context mContext;
	private Handler mHandler;
	private List<DownloadInfo> downloadInfo;// 保存下载信息类
	private String filePath;// 目录
	private String fileName;// 文件名
	private int fileSize;
	private DownsqlTool sqlTool;// 文件信息保存的数据库信息工具类
	// 所有线程下载的总数
	private int globalCompelete = 0;

	/**
	 * 利用枚举表示的三种下载状态
	 * 
	 * @author Administrator
	 * 
	 */
	private enum Download_status {
		Downloading, Pause, Ready, Delete;
	}

	private Download_status status = Download_status.Ready;

	public DownloadHttpTool(int threadCount, String strUrl, Context context,
			Handler mHandler, String filePath, String fileName, int fileSize) {
		super();
		this.threadCount = threadCount;
		this.strUrl = strUrl;
		this.mContext = context;
		this.mHandler = mHandler;
		this.filePath = filePath;
		this.fileName = fileName;
		this.fileSize = fileSize;
		sqlTool = new DownsqlTool(mContext);
	}

	/**
	 * 开始下载之前的配置
	 */
	public void ready() {
		Log.w(TAG, "ready");
		globalCompelete = 0;
		downloadInfo = sqlTool.getInfo(strUrl);
		if (downloadInfo.size() == 0) {
			initFirst();
		} else {
			File file = new File(filePath + "/" + fileName);
			if (!file.exists()) {
				sqlTool.delete(strUrl);
				initFirst();
			} else {
				fileSize = downloadInfo.get(downloadInfo.size() - 1)
						.getStopPos();
				for (DownloadInfo info : downloadInfo) {
					globalCompelete += info.getCompleteSize();
				}
				Log.w(TAG, "globalCompelete:::" + globalCompelete);
			}
		}
	}

	public void start() {
		Log.w(TAG, "start");
		if (downloadInfo != null) {
			if (status == Download_status.Downloading) {
				return;
			}

			status = Download_status.Downloading;
			for (DownloadInfo info : downloadInfo) {
				Log.v(TAG, "startThread");
				new DownloadThread(info.getThreadId(), info.getStartPos(),
						info.getStopPos(), info.getCompleteSize(),
						info.getUrl()).start();
			}

		}

	}

	public void pause() {
		status = Download_status.Pause;
		sqlTool.closeDb();
	}

	public void delete() {
		status = Download_status.Delete;
		compelete();
		new File(filePath + File.separator + fileName).delete();
	}

	public void compelete() {
		sqlTool.delete(strUrl);
		sqlTool.closeDb();
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getCompeleteSize() {
		return globalCompelete;
	}

	/**
	 * 第一次下载初始化
	 */
	private void initFirst() {
		Log.w(TAG, "initFirst");

		try {
			URL url = new URL(strUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			fileSize = connection.getContentLength();
			Log.w(TAG, "fileSize::" + fileSize);
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}

			File file2 = new File(file, fileName);
			if (!file2.exists()) {
				file2.createNewFile();
			}
			// 本地访问文件
			RandomAccessFile randomAccessFile = new RandomAccessFile(file2,
					"rwd");
			randomAccessFile.setLength(fileSize);
			randomAccessFile.close();
			connection.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int range = fileSize / threadCount;

		downloadInfo = new ArrayList<DownloadInfo>();
		for (int i = 0; i < threadCount - 1; i++) {
			DownloadInfo info = new DownloadInfo(i, i * range, (i + 1) * range
					- 1, 0, strUrl);

			downloadInfo.add(info);
		}

		DownloadInfo infolast = new DownloadInfo(threadCount - 1,
				(threadCount - 1) * range, fileSize - 1, 0, strUrl);
		downloadInfo.add(infolast);

		sqlTool.insertInfos(downloadInfo);
	}

	/**
	 * 自定义下载线程
	 * 
	 * @author Administrator
	 * 
	 */
	private class DownloadThread extends Thread {
		private int threadId;
		private int startPos;
		private int stopPos;
		private int completeSize;
		private String urls;
		private int totalThreadSize;

		public DownloadThread(int threadId, int startPos, int stopPos,
				int completeSize, String url) {
			super();
			this.threadId = threadId;
			this.startPos = startPos;
			this.stopPos = stopPos;
			this.completeSize = completeSize;
			urls = url;
			totalThreadSize = stopPos - startPos + 1;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			HttpURLConnection connection = null;
			RandomAccessFile accessFile = null;
			InputStream is = null;
			try {
				accessFile = new RandomAccessFile(filePath + File.separator
						+ fileName, "rwd");
				accessFile.seek(startPos + completeSize);
				URL url2 = new URL(urls);
				connection = (HttpURLConnection) url2.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Range", "bytes="
						+ (startPos + completeSize) + "-" + stopPos);
				is = connection.getInputStream();
				byte[] bytes = new byte[1024];
				int length = 0;
				while ((length = is.read(bytes)) != -1) {
					accessFile.write(bytes, 0, length);
					completeSize += length;
					Message message = Message.obtain();
					message.what = threadId;
					message.obj = urls;
					message.arg1 = length;
					mHandler.sendMessage(message);
					Log.w(TAG, "Threadid::" + threadId + "    compelete::"
							+ completeSize + "    total::" + totalThreadSize);
					// 当程序不再是下载状态的时候，纪录当前的下载进度
					if ((status != Download_status.Downloading)
							|| (completeSize >= totalThreadSize)) {
						sqlTool.updateInfo(threadId, completeSize, urls);
						break;
					}
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					accessFile.close();
					connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
