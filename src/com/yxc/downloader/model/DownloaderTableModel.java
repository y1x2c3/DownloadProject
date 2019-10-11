package com.yxc.downloader.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import com.yxc.downloader.util.Downloader;

/**
 * ������Table������ģ�� - �۲���
 * <p>Title: </p>
 * <p>Description:  </p>
 * @version 1.0
 */
public class DownloaderTableModel extends AbstractTableModel implements Observer{
	private static final String[] ColNames = {"����", "�ļ���С(MB)", "���ؽ���", "�����ٶ�", "����״̬"};
	/** һ����������У������ж����������һ�����������Կ�������������� */
	private List<Downloader> downloaderList = new ArrayList<Downloader>();

	@Override
	public int getRowCount() {
		return downloaderList.size();
	}

	@Override
	public int getColumnCount() {
		return ColNames.length;
	}
	
	//��������Ҫ�ֶ���д-����ÿ���е�����
	@Override
	public String getColumnName(int column) {
		return ColNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DecimalFormat format = new DecimalFormat("#.#");
		//��õ�ǰ������Ҫ��ʾ�Ķ�Ӧ����������
		Downloader currDownloader = downloaderList.get(rowIndex);
		//ʡ�Էǿ��ж�
		switch (columnIndex) {
		case 0:		//����
			return currDownloader.getNetUrl();
		case 1:		//�ļ���С
			int fileSize = currDownloader.getFileSize();
			if(fileSize == -1) return "--";
			return format.format(fileSize / 1024 / 1024.0) + " MB";
		case 2:    //���ؽ��� - ��Ҫ�ѵ�Ԫ����Ⱦ�ɽ����� - ������Ȼ�������������ؽ�������
			return new Float(currDownloader.getCompleted() * 100);
		
		case 3:		//�����ٶ�
			return format.format(currDownloader.getSpeed() / 1024 / 1024.0) + " MB/s";
		case 4:		//����״̬
			return currDownloader.getStatus_Str();
		default:
			break;
		}
		return null;
	}

	/**
	 * �������Ӱ�ťʱ���ͻ���ñ�����
	 * @param downloader
	 */
	public void addDownloader(Downloader downloader) {
		downloaderList.add(downloader);
		downloader.addObserver(this);		//��������ע��Ϊdownloader�Ĺ۲���
		//��������
		downloader.download();
		//֪ͨ���棬����ģ���ѷ����ı� - �����һ�в���һ������
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}
	
	/**
	 * ����������״̬�ı�ʱ��update�����ͻᱻ����
	 * ������update����������ʱ��Ӧ�������������Ҫ����Ҫ��ʾ�����ݣ�Ȼ��֪ͨ����������ʾ
	 */
	@Override
	public void update(Observable o, Object arg) {
		if(downloaderList.size() == 0) return;
		//�������Ӱ�ťʱ���������ͻᱻ�Զ����� - ����������ʱ���������ͱ��Զ�����
		int newIndex = downloaderList.indexOf(o);
		if(newIndex == -1) return;
		//֪ͨ����㣬������Ӧ�е�����
		fireTableRowsUpdated(newIndex, newIndex);//һ��ֻ����һ��
	}
	
	public String getLocalFile(int rowIndex) {
		if(downloaderList.size() == 0) return null;
		Downloader downloader = downloaderList.get(rowIndex);
		return downloader.getLocalFilePath();
	}
	
	/**
	 * ��ָͣ����������
	 * @param rowIndex
	 */
	public void pause(int rowIndex) {
		if(downloaderList.size() == 0) return;
		Downloader downloader = downloaderList.get(rowIndex);
		if(downloader.getStatus() != Downloader.PAUSE) {
			downloader.pause();
		}
	}
	
	public void resume(int rowIndex) {
		if(downloaderList.size() == 0) return;
		Downloader downloader = downloaderList.get(rowIndex);
		downloader.resume();
	}

}
