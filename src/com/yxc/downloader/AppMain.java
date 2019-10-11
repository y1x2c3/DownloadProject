package com.yxc.downloader;

import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.yxc.downloader.ui.DownloadFrame;

public class AppMain {
	public static void main(String[] args) {
		Font font_normal = new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 18);
		UIManager.put("TextField.font", font_normal);
		UIManager.put("Button.font", font_normal);
		UIManager.put("Table.font", font_normal);
		UIManager.put("TableHeader.font", font_normal);
		UIManager.put("TitledBorder.font", font_normal);
		SwingUtilities.invokeLater(()->{
			new DownloadFrame().setVisible(true);
		});
	}
}
