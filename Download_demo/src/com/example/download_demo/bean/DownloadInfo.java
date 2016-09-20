package com.example.download_demo.bean;

/**
 * 下载信息类
 * 
 * @author Administrator
 * 
 */
public class DownloadInfo {

	private int threadId;// 下载器ID
	private int startPos;// 开始点
	private int stopPos;// 结束点
	private int completeSize;// 完成度
	private String url;// 下载文件的URL

	public DownloadInfo(int threadId, int startPos, int stopPos,
			int completeSize, String url) {
		super();
		this.threadId = threadId;
		this.startPos = startPos;
		this.stopPos = stopPos;
		this.completeSize = completeSize;
		this.url = url;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getStopPos() {
		return stopPos;
	}

	public void setStopPos(int stopPos) {
		this.stopPos = stopPos;
	}

	public int getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "DownloadInfo [threadId=" + threadId + ", startPos=" + startPos
				+ ", stopPos=" + stopPos + ", completeSize=" + completeSize
				+ ", url=" + url + "]";
	}

}
