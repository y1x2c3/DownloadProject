package com.yxc.downloader.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import com.yxc.downloader.util.Downloader;

/**
 * 下载器Table的数据模型 - 观察者
 * <p>Title: </p>
 * <p>Description:  </p>
 * @version 1.0
 */
public class DownloaderTableModel extends AbstractTableModel implements Observer{
	private static final String[] ColNames = {"链接", "文件大小(MB)", "下载进度", "下载速度", "下载状态"};
	/** 一个下载面板中，可以有多个下载器；一个下载器可以开启多个下载任务 */
	private List<Downloader> downloaderList = new ArrayList<Downloader>();

	@Override
	public int getRowCount() {
		return downloaderList.size();
	}

	@Override
	public int getColumnCount() {
		return ColNames.length;
	}
	
	//本方法需要手动重写-设置每个列的列名
	@Override
	public String getColumnName(int column) {
		return ColNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DecimalFormat format = new DecimalFormat("#.#");
		//获得当前方法的要显示的对应下载器对象
		Downloader currDownloader = downloaderList.get(rowIndex);
		//省略非空判断
		switch (columnIndex) {
		case 0:		//链接
			return currDownloader.getNetUrl();
		case 1:		//文件大小
			int fileSize = currDownloader.getFileSize();
			if(fileSize == -1) return "--";
			return format.format(fileSize / 1024 / 1024.0) + " MB";
		case 2:    //下载进度 - 需要把单元格渲染成进度条 - 这里仍然返回正常的下载进度数字
			return new Float(currDownloader.getCompleted() * 100);
		
		case 3:		//下载速度
			return format.format(currDownloader.getSpeed() / 1024 / 1024.0) + " MB/s";
		case 4:		//下载状态
			return currDownloader.getStatus_Str();
		default:
			break;
		}
		return null;
	}

	/**
	 * 当点击添加按钮时，就会调用本方法
	 * @param downloader
	 */
	public void addDownloader(Downloader downloader) {
		downloaderList.add(downloader);
		downloader.addObserver(this);		//将本对象注册为downloader的观察者
		//开启下载
		downloader.download();
		//通知界面，数据模型已发生改变 - 在最后一行插入一个新行
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}
	
	/**
	 * 当下载器的状态改变时，update方法就会被调用
	 * 所以在update方法被调用时，应该在这里根据需要更新要显示的数据，然后通知界面层更新显示
	 */
	@Override
	public void update(Observable o, Object arg) {
		if(downloaderList.size() == 0) return;
		//当点击添加按钮时，本方法就会被自动调用 - 新增下载器时，本方法就被自动调用
		int newIndex = downloaderList.indexOf(o);
		if(newIndex == -1) return;
		//通知界面层，更新相应行的数据
		fireTableRowsUpdated(newIndex, newIndex);//一次只更新一行
	}
	
	public String getLocalFile(int rowIndex) {
		if(downloaderList.size() == 0) return null;
		Downloader downloader = downloaderList.get(rowIndex);
		return downloader.getLocalFilePath();
	}
	
	/**
	 * 暂停指定的下载器
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
