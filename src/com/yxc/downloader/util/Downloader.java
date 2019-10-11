package com.yxc.downloader.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Title: </p>
 * <p>Description:  </p>
 * @version 1.0
 */
public class Downloader extends Observable{
	/** 将下载器的5个状态封装成了数组以及常量，方便使用 */
	private static final String[] STATUS = {"正在下载", "暂停", "已完成", "已取消", "下载错误"};
	/** 正在下载状态 */
	public static final int DOWNLOADING = 0;
	public static final int PAUSE = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELED = 3;
	public static final int ERROR = 4;
	
	private int status;								//用来标识当前下载器的状态
	private String netUrl;							//要下载内容的网络地址
	private String localFilePath;					//将下载下来的内容保存到的本地路径
	private int countOfThread;						//当前下载器的总线程数
	private int fileSize;							//所下载文件的总大小（字节数）
	private int downloaded;							//当前下载的总字节数
	List<DownloadTask> taskList;					//当前下载器中的下载任务列表
	private double speed;							//当前的下载速度(默认字节/秒)
	private Speeder speeder;						//速度计 - 开始下载的时候打开速度计
	
	public Downloader(String netUrl, String localFilePath, int countOfThread) {
		this.netUrl = netUrl;
		this.localFilePath = localFilePath;
		this.countOfThread = countOfThread;
		this.taskList = new ArrayList<DownloadTask>();
	}
	
	public void download() {
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(netUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Range", "bytes=0-");
			connection.connect();
			int responseCode = connection.getResponseCode();
			if(responseCode / 100 != 2) {
				throw new IllegalStateException("连接错误，返回码：" + responseCode);
			}
			this.fileSize = connection.getContentLength();
			int partSize = fileSize / countOfThread;
			ExecutorService executor = Executors.newFixedThreadPool(countOfThread);
			for(int i = 0; i < countOfThread; i++) {
				int startPosition = i * partSize;
				RandomAccessFile partFile = new RandomAccessFile(localFilePath, "rw");
				partFile.seek(startPosition);
				if(fileSize % countOfThread != 0 && i == countOfThread - 1) {
					partSize += fileSize % countOfThread;
				}
				DownloadTask task = new DownloadTask(netUrl, startPosition, partFile, partSize, this);
				this.taskList.add(task);
				executor.execute(task);
			}
			//当下载器开启下载任务后，一定记得将下载器的状态设置为 DOWNLOADING
			setChanged(DOWNLOADING);
			this.speeder = new Speeder();
			this.speeder.start();//开启速度计线程
		} catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				connection.disconnect();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/** 速度计类 */
	class Speeder extends Thread{
		private Downloader downloader;
		private double speed;
		
		public Speeder() {
			this.downloader = Downloader.this;
		}
		
		public double getSpeed() {
			return speed;
		}
		
		
		@Override
		public void run() {
			int downloaded = 0;
			long sleepTime = 500;
			while(true) {
				speed = (downloader.getDownloaded() - downloaded) * (1000.0 / sleepTime);
				if(downloader.getStatus() != DOWNLOADING) {
					break;
				}
				//通知界面更新下载速度值
				setChanged(DOWNLOADING);
				downloaded = downloader.getDownloaded();
				if(downloader.getCompleted() >= 1) {
					setChanged(COMPLETE);
					break;
				}
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public double getSpeed() {
		return speeder.getSpeed();
	}
	
	
	/**
	 * 自己定义一个方法，来封装当前下载器状态变化时的代码
	 * @param status 设置当前下载器的状态
	 */
	public void setChanged(int status) {
		this.status = status;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void pause(){
		this.setChanged(PAUSE);
	}
	
	public void resume() {
		//1.重新开启下载
		this.download();		
		//2.修改状态
		this.setChanged(DOWNLOADING);
	}
	
	public void reset() {
		
	}
	
	public void cancel() {
		
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getNetUrl() {
		return netUrl;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	public String getStatus_Str() {
		return STATUS[getStatus()];
	}
	
	public int getDownloaded() {
		this.downloaded = 0;
		for(DownloadTask task : taskList) {
			this.downloaded += task.getDownloaded();
		}
		return this.downloaded;
	}
	
	/** 返回当前的下载总进度 */
	public double getCompleted() {
		this.downloaded = getDownloaded();
		return downloaded / 1.0 / fileSize;
	}
	
	public String getLocalFilePath() {
		return localFilePath;
	}
}







