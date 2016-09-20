package com.example.download_demo.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class DownloadUtil {
	private DownloadHttpTool mdowDownloadHttpTool;
	private OnDownloadListener onDownloadListener;
	private int fileSize;
	private int downloadSize;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int lenght = msg.arg1;

			synchronized (this) {// ������֤�����ص���ȷ��
				downloadSize += lenght;
			}
			if (onDownloadListener != null) {
				onDownloadListener.downloadProgress(downloadSize);
			}
			if (downloadSize >= fileSize) {
				mdowDownloadHttpTool.compelete();
				if (onDownloadListener != null) {
					onDownloadListener.downloadEnd();
				}

			}
		};
	};

	public DownloadUtil(int threadCount, String strUrl, Context mContext,
			String filePath, String fileName) {
		mdowDownloadHttpTool = new DownloadHttpTool(threadCount, strUrl,
				mContext, mHandler, filePath, fileName, fileSize);

	}

	// ����֮ǰ�����첽�̵߳���ready��������ļ���С��Ϣ��֮����ÿ�ʼ����
	public void start() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				mdowDownloadHttpTool.ready();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				fileSize = mdowDownloadHttpTool.getFileSize();
				downloadSize = mdowDownloadHttpTool.getCompeleteSize();
				Log.w("Tag", "downloadedSize::" + downloadSize);

				if (onDownloadListener != null) {
					onDownloadListener.downloadStart(fileSize);
				}
				mdowDownloadHttpTool.start();

			}
		}.execute();
	}

	public void pause() {
		mdowDownloadHttpTool.pause();
	}

	public void delete() {
		mdowDownloadHttpTool.delete();
	}

	public void reset() {
		mdowDownloadHttpTool.delete();
		start();
	}

	public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
		this.onDownloadListener = onDownloadListener;
	}

	// ���ػص��ӿ�
	public interface OnDownloadListener {
		public void downloadStart(int fileSize);

		public void downloadProgress(int downloadedSize);

		public void downloadEnd();
	}
}
