package com.yxc.downloader.ui;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 用来将JTable中的单元格渲染成进度条
 * <p>Title: </p>
 * <p>Description:  </p>
 * @version 1.0
 */
public class ProgressRenderer extends JProgressBar implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		//取出要渲染单元格中的值，强制转换成浮点型，以便赋值给进度条
		Float floatValue = Float.valueOf(value.toString());
		this.setValue(floatValue.intValue());
		return this;
	}

}
