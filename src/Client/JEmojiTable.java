package Client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;

public class JEmojiTable extends JFrame {
    private JTable table;
    static String emoji_value="";

    JEmojiTable() {
        super();
        setTitle("选择表情");
        setBounds(100, 100, 410, 375);
        final JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        String[] columnName = {"", "", "", "", ""};  //定义表格列名
        String[][] tableValues = new String[21][5];  //定义存储表格数据的数组

        //初始化表格数据值
        int count = 0;
        for (int row = 0; row < 21; row++) {
            for (int column = 0; column < 5; column++) {
                tableValues[row][column] = String.valueOf(count);
                count++;
            }
        }
        table = new JTable(tableValues, columnName);  //创建表格
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  //关闭表格列的自动调整功能
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  //选择模式为单选
        table.setSelectionBackground(Color.WHITE);  //被选择项的背景色为白色
        //table.setSelectionForeground(Color.RED);  //被选择项的文字颜色为红色
        table.setRowHeight(30);  //表格行高为30像素
        //table.getColumnModel().getColumn(0).setPreferredWidth(30);

        //设置表格数据居中
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, r);

        scrollPane.setViewportView(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {  //鼠标单击时响应
                //得到选中的行列的索引值
                int r = table.getSelectedRow();
                int c = table.getSelectedColumn();

                String value = table.getValueAt(r, c).toString();  //得到选中的单元格的值

                emoji_value=value;

                dispose();
                //System.out.println(r);
                //System.out.println(c);
                //System.out.println(emoji_value);
            }
        });
    }

//    public static void main(String[] args) {
//        JEmojiTable frame = new JEmojiTable();
//        frame.setVisible(true);
//    }
}
