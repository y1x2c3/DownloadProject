package com.yxc.downloader.ui;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * ������JTable�еĵ�Ԫ����Ⱦ�ɽ�����
 * <p>Title: </p>
 * <p>Description:  </p>
 * @version 1.0
 */
public class ProgressRenderer extends JProgressBar implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		//ȡ��Ҫ��Ⱦ��Ԫ���е�ֵ��ǿ��ת���ɸ����ͣ��Ա㸳ֵ��������
		Float floatValue = Float.valueOf(value.toString());
		this.setValue(floatValue.intValue());
		return this;
	}

}
