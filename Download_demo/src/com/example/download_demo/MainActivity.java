package com.example.download_demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.download_demo.tool.DownloadUtil;
import com.example.download_demo.tool.DownloadUtil.OnDownloadListener;

public class MainActivity extends Activity implements OnClickListener {
	String urlString = "http://sw.bos.baidu.com/sw-search-sp/software/2bebf2dba4c1d/FileZilla_3.17.0.1_win64_setup.exe";
	final String localPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/ADownLoadTest";
	private Button btnStart, btnStop;
	private ProgressBar pb;
	private TextView tvFileName;
	private ImageView iv_image;
	private DownloadUtil downloadUtil = null;
	private int max = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();

	}

	private void initView() {
		tvFileName = (TextView) findViewById(R.id.textView1);
		btnStart = (Button) findViewById(R.id.btn_start);
		btnStop = (Button) findViewById(R.id.btn_stop);
		findViewById(R.id.btn_reset).setOnClickListener(this);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		iv_image = (ImageView) findViewById(R.id.iv_image);
		downloadUtil = new DownloadUtil(5, urlString, MainActivity.this,
				localPath, "FileZilla_3.17.0.1_win64_setup.exe");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start:
			downloadUtil.start();
			downloadUtil.setOnDownloadListener(new OnDownloadListener() {

				@Override
				public void downloadStart(int fileSize) {
					max = fileSize;
					pb.setMax(max);
				}

				@Override
				public void downloadProgress(int downloadedSize) {
					pb.setProgress(downloadedSize);
					tvFileName
							.setText((int) (downloadedSize * 100) / max + "%");
				}

				@Override
				public void downloadEnd() {
					// Bitmap bitmap = BitmapFactory.decodeFile(localPath
					// + File.separator + "abc.jpg");
					// iv_image.setImageBitmap(bitmap);
					Toast.makeText(MainActivity.this, "œ¬‘ÿÕÍ≥…",
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case R.id.btn_stop:
			downloadUtil.pause();
			break;
		case R.id.btn_reset:
			downloadUtil.reset();
			break;

		}

	}
}
