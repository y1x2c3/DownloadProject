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
	/** ����������5��״̬��װ���������Լ�����������ʹ�� */
	private static final String[] STATUS = {"��������", "��ͣ", "�����", "��ȡ��", "���ش���"};
	/** ��������״̬ */
	public static final int DOWNLOADING = 0;
	public static final int PAUSE = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELED = 3;
	public static final int ERROR = 4;
	
	private int status;								//������ʶ��ǰ��������״̬
	private String netUrl;							//Ҫ�������ݵ������ַ
	private String localFilePath;					//���������������ݱ��浽�ı���·��
	private int countOfThread;						//��ǰ�����������߳���
	private int fileSize;							//�������ļ����ܴ�С���ֽ�����
	private int downloaded;							//��ǰ���ص����ֽ���
	List<DownloadTask> taskList;					//��ǰ�������е����������б�
	private double speed;							//��ǰ�������ٶ�(Ĭ���ֽ�/��)
	private Speeder speeder;						//�ٶȼ� - ��ʼ���ص�ʱ����ٶȼ�
	
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
				throw new IllegalStateException("���Ӵ��󣬷����룺" + responseCode);
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
			//���������������������һ���ǵý���������״̬����Ϊ DOWNLOADING
			setChanged(DOWNLOADING);
			this.speeder = new Speeder();
			this.speeder.start();//�����ٶȼ��߳�
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
	
	/** �ٶȼ��� */
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
				//֪ͨ������������ٶ�ֵ
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
	 * �Լ�����һ������������װ��ǰ������״̬�仯ʱ�Ĵ���
	 * @param status ���õ�ǰ��������״̬
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
		//1.���¿�������
		this.download();		
		//2.�޸�״̬
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
	
	/** ���ص�ǰ�������ܽ��� */
	public double getCompleted() {
		this.downloaded = getDownloaded();
		return downloaded / 1.0 / fileSize;
	}
	
	public String getLocalFilePath() {
		return localFilePath;
	}
}







