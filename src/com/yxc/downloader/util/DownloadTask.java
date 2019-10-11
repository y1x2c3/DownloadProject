package com.yxc.downloader.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ����������
 * <p>Title: </p>
 * <p>Description:  </p>
 * @version 1.0
 */
public class DownloadTask implements Runnable{
	private static final int MAX_BUFFER_SIZE = 1024 * 1024 * 4;
	private String netUrl;
	private int startPosition;
	private int partSize;
	private RandomAccessFile partFile;
	private int downloaded;
	private long timeCose;
	private Downloader downloader;
	
	public DownloadTask(String netUrl, int startPosition, RandomAccessFile partFile, int partSize, Downloader downloader) {
		this.netUrl = netUrl;
		this.startPosition = startPosition;
		this.partFile = partFile;
		this.partSize = partSize;
		this.downloader = downloader;
	}
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + "��ʼ����");
		URL url = null;
		HttpURLConnection connection = null;
		InputStream inStream = null;
		try {
			url = new URL(netUrl);
			connection = (HttpURLConnection) url.openConnection();
			String range = String.format("bytes=%d-%d", startPosition, startPosition + partSize);
			connection.setRequestProperty("Range", range);
			connection.connect();
			int responseCode = connection.getResponseCode();
			if(responseCode / 100 != 2) {
				throw new IllegalStateException("���Ӵ��󣬷����룺" + responseCode);
			}
			inStream = connection.getInputStream();
			//������Ҫ���ݵ�ǰ��������״̬�����Ƿ��������
			while(downloaded < partSize && downloader.getStatus() == Downloader.DOWNLOADING) {
				byte[] buffer = null;
				if(partSize - downloaded > MAX_BUFFER_SIZE) {
					buffer = new byte[MAX_BUFFER_SIZE];
				}else {
					buffer = new byte[partSize - downloaded];
				}
				int currDownloaded = 0;
				int readed = -1;
				while(currDownloaded < buffer.length && (readed = inStream.read()) != -1) {
					buffer[currDownloaded++] = (byte)readed;
					downloaded++;
				}
				if(readed == -1) {//˵���Ѿ���ȡ�����ļ���β
					if(currDownloaded != 0) partFile.write(buffer, 0, currDownloaded);
					break;
				}
				partFile.write(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				partFile.close();
				inStream.close();
				connection.disconnect();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() + "���������");
	}
	
	public int getDownloaded() {
		return downloaded;
	}

}
















