package com.example.download_demo.tool;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.download_demo.bean.DownloadInfo;
import com.example.download_demo.db.DownloadHelper;

public class DownsqlTool {
	private DownloadHelper downLoadHelper;

	public DownsqlTool(Context context) {
		downLoadHelper = new DownloadHelper(context);
	}

	/**
	 * 创建具体的下载信息
	 * 
	 * @param list
	 */
	public void insertInfos(List<DownloadInfo> list) {
		SQLiteDatabase sqLiteDatabase = downLoadHelper.getWritableDatabase();
		for (DownloadInfo info : list) {
			String sql = "insert into download_info(thread_id,start_pos, end_pos,compelete_size,url) values (?,?,?,?,?)";
			Object[] bindArgs = { info.getThreadId(), info.getStartPos(),
					info.getStopPos(), info.getCompleteSize(), info.getUrl() };
			sqLiteDatabase.execSQL(sql, bindArgs);
		}
	}

	/**
	 * 得到具体下载信息
	 * 
	 * @param url
	 * @return
	 */
	public ArrayList<DownloadInfo> getInfo(String url) {
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase sqLiteDatabase = downLoadHelper.getWritableDatabase();
		String sql = "select thread_id, start_pos, end_pos,compelete_size,url from download_info where url=?";
		Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[] { url });
		while (cursor.moveToNext()) {
			DownloadInfo info = new DownloadInfo(cursor.getInt(0),
					cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
					cursor.getString(4));
			list.add(info);
		}
		return list;

	}

	/**
	 * 更新数据库下载信息
	 * 
	 * @param threadId
	 * @param completerSize
	 * @param url
	 */
	public void updateInfo(int threadId, int completerSize, String url) {
		SQLiteDatabase sqLiteDatabase = downLoadHelper.getWritableDatabase();
		String sql = "update download_info set compelete_size=? where thread_id=? and url=?";
		Object[] bindArgs = { completerSize, threadId, url };
		sqLiteDatabase.execSQL(sql, bindArgs);
	}

	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		downLoadHelper.close();
	}

	/**
	 * 下载完成后删除数据库中的数据
	 */
	public void delete(String url) {
		SQLiteDatabase database = downLoadHelper.getWritableDatabase();
		database.delete("download_info", "url=?", new String[] { url });
	}
}
