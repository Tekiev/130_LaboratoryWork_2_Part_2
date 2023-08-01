package DEV130_2_2_Tekiev;

// адрес базы данных и название таблицы нужно впечатывать вручную или копировать из любого места, кроме редактора Idea, в этом случае появляется исключение.


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class DBViewer {

    private static JFrame frame;
    private static JTextField dataBase;
    private static JTextField table;
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static JPanel panel;
    private static DefaultTableModel tableModel;
    public DBViewer() {
        frame = new JFrame();
    }
    public void init() {
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] options = {"Да", "Нет"};
                int n = JOptionPane
                        .showOptionDialog(e.getWindow(),"Вы уверены что хотите закрыть приложение?",
                                "Подтверждение закрытия", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                if (n == 0) {
                    e.getWindow().setVisible(false);
                    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                }
                else {
                    frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
            }
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        frame.setTitle("Viewer DB");
        frame.setSize(1200, 600);
        frame.add(getDataBaseAndTable(), BorderLayout.NORTH);
        frame.add(initViewer(), BorderLayout.CENTER);
        JScrollPane jScrollPane = new JScrollPane(panel);
        frame.add(jScrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
    private JPanel initViewer() {
        panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(table);
        return panel;
    }
    private Container getDataBaseAndTable (){
        JPanel panelDataBaseAndTable = new JPanel();
        GridLayout layout = new GridLayout();
        panelDataBaseAndTable.setLayout(layout);
        dataBase = new JTextField(35);
        table = new JTextField(20);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblDB = new JLabel("Введите адрес БД");
        JLabel lblTable = new JLabel("Введите имя таблицы");
        lblDB.setLabelFor(dataBase);
        lblTable.setLabelFor(table);
        p.add(lblDB);
        p.add(dataBase);
        p.add(lblTable);
        p.add(table);
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new Listener());
        p.add(okBtn);
        panelDataBaseAndTable.add(p);
        return panelDataBaseAndTable;
    }
    public static void getTableFromDB(){
        try {
            connection = DriverManager.getConnection(dataBase.getText(), "root", "root");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from " + table.getText() +";");
            ResultSetMetaData md = resultSet.getMetaData();
            int count = md.getColumnCount();
            String [] nameOfColumns = new String[count];
            String [] dateOfColumns = new String[count];
            tableModel.setRowCount(0);
            while (resultSet.next()){
                for (int i = 1; i <= count; i++) {
                    nameOfColumns[i-1] = md.getColumnName(i);
                    dateOfColumns[i-1] = resultSet.getString(i);
                }
                tableModel.setColumnIdentifiers(nameOfColumns);
                tableModel.addRow(dateOfColumns);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException sqlException){
            System.out.println(sqlException);
            JOptionPane.showMessageDialog(frame, sqlException.getMessage(), "Error!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private class Listener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            if (s.equals("OK")) {
                getTableFromDB();
            }
        }
    }

}
