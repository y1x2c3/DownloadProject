package com.yxc.downloader.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.yxc.downloader.model.DownloaderTableModel;
import com.yxc.downloader.util.Downloader;

/**
 * 自定义下载器的主界面
 * <p>Title: </p>
 * <p>Description:  </p>
 * @version 1.0
 */
public class DownloadFrame extends JFrame{
	private JTextField txtAddUrl = new JTextField(50);
	private JTable tabDownloadInfo = new JTable();
	private JButton btnAddUrl = new JButton("添加");
	private JButton btnPause = new JButton("暂停");
	private JButton btnReset = new JButton("重置");
	private JButton btnClear = new JButton("清空");
	private JButton btnCancel = new JButton("取消");
	private DownloaderTableModel tableModel = new DownloaderTableModel();
	
	
	public DownloadFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Java实现的多线程下载器 ");
		setSize(1024, 768);
		initComponents();
		initEvents();
		setLocationRelativeTo(null);
	}

	private void initComponents() {
		JPanel contentPane = (JPanel)getContentPane();
		
		//设置添加URL的小面板
		JPanel pnlAddUrl = new JPanel();
		txtAddUrl.setText("https://www.xuetang9.com/templets/default/video/intro.mp4");
		pnlAddUrl.add(txtAddUrl);
		pnlAddUrl.add(btnAddUrl);
		
		//设置中间的下载信息面板
		JPanel pnlDownloadInfo = new JPanel(new BorderLayout());
		pnlDownloadInfo.setBorder(BorderFactory.createTitledBorder("下载信息"));
		pnlDownloadInfo.add(new JScrollPane(tabDownloadInfo));
		setTabDownloadInfo(tabDownloadInfo);				//将设置JTable控件的代码单独封装成一个私有方法，方便阅读
		
		//设置最下面的按钮面板
		JPanel pnlButton = new JPanel();
		pnlButton.add(btnPause);
		pnlButton.add(btnReset);
		pnlButton.add(btnClear);
		pnlButton.add(btnCancel);
		
		contentPane.add(pnlAddUrl, BorderLayout.NORTH);
		contentPane.add(pnlDownloadInfo, BorderLayout.CENTER);
		contentPane.add(pnlButton, BorderLayout.SOUTH);
	}
	

	private void setTabDownloadInfo(JTable tabDownloadInfo2) {
		tabDownloadInfo2.setRowHeight(28);
		//设置第三列显示为进度条
		ProgressRenderer progressRenderer = new ProgressRenderer();		
		progressRenderer.setStringPainted(true);
		tabDownloadInfo2.setModel(tableModel);
		tabDownloadInfo2.getColumn("下载进度").setCellRenderer(progressRenderer);
	}

	private void initEvents() {
		btnAddUrl.addActionListener(e->{
			btnAddUrlActionPerformed(e);
		});
		btnPause.addActionListener(e->{
			btnPauseActionPerformed(e);
		});
		tabDownloadInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tableMouseClicked(e);
			}
		});
	}
	
	protected void tableMouseClicked(MouseEvent e) {
		int selectedRowIndex = tabDownloadInfo.getSelectedRow();
		if(selectedRowIndex == -1) return;
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			//使用系统默认程序打开这个文件 - 本程序中，默认文件是mp4类型的
			//需要获得要打开文件的路径
			File file = new File(tableModel.getLocalFile(selectedRowIndex));
			String absPath = file.getAbsolutePath();
			try {
				Runtime.getRuntime().exec(new String[] {
						"C:\\Program Files\\Windows Media Player\\wmplayer.exe",
						absPath
				});
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void btnPauseActionPerformed(ActionEvent e) {
		int rowIndex = tabDownloadInfo.getSelectedRow();
		if(rowIndex == -1) {
			btnPause.setText("暂停");
			return;
		}
		JButton button = (JButton)e.getSource();
		if("暂停".equals(button.getActionCommand())) {
			tableModel.pause(rowIndex);
			button.setText("开始");			
		}else {
			tableModel.resume(rowIndex);
			button.setText("暂停");			
		}
	}

	private void btnAddUrlActionPerformed(ActionEvent e) {
		//验证url是否合法
		String netUrl = txtAddUrl.getText();
		if("".equals(netUrl)) return;
		if(!(netUrl.startsWith("http://") || netUrl.startsWith("https://"))) {
			JOptionPane.showMessageDialog(this, "不支持此下载协议！");
			return;
		} 
		//获得要保存文件的名称 - 显示文件选择框 - 省略
		//http://www.qq.com/index.html
		String localFilePath = netUrl.substring(netUrl.lastIndexOf("/") + 1);
		//创建一个新的下载器
		Downloader downloader = new Downloader(netUrl, localFilePath, 5);
		tableModel.addDownloader(downloader);
	}
	
}









