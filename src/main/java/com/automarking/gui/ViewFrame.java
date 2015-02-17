package com.automarking.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("serial")
public class ViewFrame extends JFrame {
    JPanel panel;
    JLabel header, tableheader;
    Container cn = null;
    JButton back;
    String message;

    public ViewFrame(String msg) {
        cn = getContentPane();
        cn.setLayout(null);

        panel = new JPanel();
        header = new JLabel("VIEW DATA", JLabel.CENTER);
        tableheader = new JLabel("", JLabel.CENTER);
        back = new JButton("Back");
        message = msg;

        header.setBounds(0, 0, 900, 30);
        tableheader.setBounds(0, 30, 900, 30);
        panel.setBounds(0, 60, 900, 300);
        back.setBounds(400, 370, 100, 40);

        header.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 20));
        tableheader.setFont(new Font("Arial Black", Font.PLAIN, 15));

        setTitle("View");
        cn.add(header);
        cn.add(tableheader);
        cn.add(panel);
        cn.add(back);

        setVisible(true);
        setBounds(200, 50, 900, 450);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    }

    void viewFrame(String ID) {
        String sql_query;
        panel.setLayout(null);

        JTable table;

        if (message.equalsIgnoreCase("specific_QID")) {
            tableheader.setText("Answers to Question : " + ID);
            sql_query = "select  ";

        }
    }

    void viewFrame(
            int toggle, final int accno, final int permission,
            final String username) {
        String sql_query;
        panel.setLayout(null);
        back.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (permission == 1) {
                            dispose();
                            new MainAdminWindow(username);
                        } else if (permission == 0) {
                            dispose();

                        }

                    }
                }
        );

        JTable table;

        // code for transaction
        if (toggle == 1) {

            tableheader.setText("Transactions with Account No : " + accno);
            sql_query = "select accno,to_char(transdate,'DD/MM/YYYY'),transtype,amt,balance "
                    + "from transaction where accno="
                    + accno + " order by transdate";
            int columncount = 5;
            String columns[] = {
                    "Account No", "Transaction Date",
                    "Transaction Type", "Amount", "Balance"
            };
            ResultSet rs = null;
            String[][] d = null;
            try {
                rs = query("select count(*) from transaction");
                rs.next();
                int rowcount = Integer.parseInt(rs.getString(1));
                rs = query(sql_query);
                d = new String[rowcount][5];
                int jr = 0;

                table = new JTable(d, columns) {
                    DefaultTableCellRenderer renderRight = new DefaultTableCellRenderer();

                    {// initializer block
                        renderRight
                                .setHorizontalAlignment(SwingConstants.CENTER);
                    }

                    @Override
                    public TableCellRenderer getCellRenderer(int arg0, int arg1) {
                        return renderRight;

                    }

                };

                while (rs.next()) {

                    for (int i = 1; i <= columncount; i++) {
                        d[jr][i - 1] = rs.getString(i);
                    }
                    jr++;
                }
                JScrollPane spTable = new JScrollPane(table);
                spTable.setBounds(100, 20, 700, 250);

                panel.add(spTable);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (toggle == 0) {
            // code for customer
            tableheader.setText("Customers with Account in the Bank");
            int columncount = 8;
            String columns[] = {
                    "Account No", "Name", "Address", "Phone No",
                    "Account Type", "Balance", "No. of transactions",
                    "Password"
            };
            ResultSet rs = null;
            String[][] d = null;
            try {
                rs = query("select count(*) from customer");

                rs.next();
                int rowcount = Integer.parseInt(rs.getString(1));
                rs = query("select * from customer order by accno");
                d = new String[rowcount][8];
                int jr = 0;

                table = new JTable(d, columns) {
                    DefaultTableCellRenderer renderRight = new DefaultTableCellRenderer();

                    {// initializer block
                        renderRight
                                .setHorizontalAlignment(SwingConstants.CENTER);
                    }

                    @Override
                    public TableCellRenderer getCellRenderer(int arg0, int arg1) {
                        return renderRight;

                    }

                };
                while (rs.next()) {
                    for (int i = 1; i <= columncount; i++) {
                        d[jr][i - 1] = rs.getString(i);
                    }
                    jr++;
                }
                JScrollPane spTable = new JScrollPane(table);
                spTable.setBounds(25, 0, 850, 250);
                panel.add(spTable);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private ResultSet query(String string) {

        Connection con = null;
        Statement st;
        ResultSet r = null;
        try {
            String driverName = "oracle.jdbc.driver.OracleDriver";
            Class.forName(driverName);
            // select ora_database_name from dual;
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@127.0.0.1:1521:XE", "ajay", "password"
            );
            st = con.createStatement();
            r = st.executeQuery(string);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return r;
    }
}

@SuppressWarnings("serial")
class CustomTableModel extends DefaultTableModel {
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}