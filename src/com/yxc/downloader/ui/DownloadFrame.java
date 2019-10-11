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
 * �Զ�����������������
 * <p>Title: </p>
 * <p>Description:  </p>
 * @version 1.0
 */
public class DownloadFrame extends JFrame{
	private JTextField txtAddUrl = new JTextField(50);
	private JTable tabDownloadInfo = new JTable();
	private JButton btnAddUrl = new JButton("���");
	private JButton btnPause = new JButton("��ͣ");
	private JButton btnReset = new JButton("����");
	private JButton btnClear = new JButton("���");
	private JButton btnCancel = new JButton("ȡ��");
	private DownloaderTableModel tableModel = new DownloaderTableModel();
	
	
	public DownloadFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Javaʵ�ֵĶ��߳������� ");
		setSize(1024, 768);
		initComponents();
		initEvents();
		setLocationRelativeTo(null);
	}

	private void initComponents() {
		JPanel contentPane = (JPanel)getContentPane();
		
		//�������URL��С���
		JPanel pnlAddUrl = new JPanel();
		txtAddUrl.setText("https://www.xuetang9.com/templets/default/video/intro.mp4");
		pnlAddUrl.add(txtAddUrl);
		pnlAddUrl.add(btnAddUrl);
		
		//�����м��������Ϣ���
		JPanel pnlDownloadInfo = new JPanel(new BorderLayout());
		pnlDownloadInfo.setBorder(BorderFactory.createTitledBorder("������Ϣ"));
		pnlDownloadInfo.add(new JScrollPane(tabDownloadInfo));
		setTabDownloadInfo(tabDownloadInfo);				//������JTable�ؼ��Ĵ��뵥����װ��һ��˽�з����������Ķ�
		
		//����������İ�ť���
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
		//���õ�������ʾΪ������
		ProgressRenderer progressRenderer = new ProgressRenderer();		
		progressRenderer.setStringPainted(true);
		tabDownloadInfo2.setModel(tableModel);
		tabDownloadInfo2.getColumn("���ؽ���").setCellRenderer(progressRenderer);
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
			//ʹ��ϵͳĬ�ϳ��������ļ� - �������У�Ĭ���ļ���mp4���͵�
			//��Ҫ���Ҫ���ļ���·��
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
			btnPause.setText("��ͣ");
			return;
		}
		JButton button = (JButton)e.getSource();
		if("��ͣ".equals(button.getActionCommand())) {
			tableModel.pause(rowIndex);
			button.setText("��ʼ");			
		}else {
			tableModel.resume(rowIndex);
			button.setText("��ͣ");			
		}
	}

	private void btnAddUrlActionPerformed(ActionEvent e) {
		//��֤url�Ƿ�Ϸ�
		String netUrl = txtAddUrl.getText();
		if("".equals(netUrl)) return;
		if(!(netUrl.startsWith("http://") || netUrl.startsWith("https://"))) {
			JOptionPane.showMessageDialog(this, "��֧�ִ�����Э�飡");
			return;
		} 
		//���Ҫ�����ļ������� - ��ʾ�ļ�ѡ��� - ʡ��
		//http://www.qq.com/index.html
		String localFilePath = netUrl.substring(netUrl.lastIndexOf("/") + 1);
		//����һ���µ�������
		Downloader downloader = new Downloader(netUrl, localFilePath, 5);
		tableModel.addDownloader(downloader);
	}
	
}









