import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.text.*;

import java.io.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;

class MenuFrame extends JInternalFrame implements ActionListener, InternalFrameListener
{
	private JSplitPane jsp;
	private JTextField txt_search, txt_book_num, txt_book_name,txt_price;
	private JList <String> menu_list;
	private JScrollPane sp;
	private JPanel left_panel, right_panel;
	private JLabel lbl_msg, lbl;
	private Vector <String>books_vector;
	private JButton b;
	private static Connection con = null;
	private static PreparedStatement ps;
	private MenuFrame me = this;

	public MenuFrame()
	{
		super("Menu Section", true, true, true, true);

		// left_panel
		left_panel = new JPanel();
		left_panel.setLayout(null);

		lbl = new JLabel("Search: ");
		lbl.setBounds(30, 20, 230, 35);
		left_panel.add(lbl);

		txt_search = new JTextField();
		txt_search.setBounds(30, 60, 230, 35);
		left_panel.add(txt_search);
		txt_search.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String search_text = txt_search.getText().trim().toUpperCase();

					connect();

					String sql = "select * from books where book_name like '%"+search_text+"%'";
					ps = con.prepareStatement(sql);					
					ResultSet rs = ps.executeQuery();

					books_vector.clear();

					int book_no = 0;
					String book_name = "";
					double book_rate = 0;

					while(rs.next())
					{
						book_no = rs.getInt("book_no");
						book_name = rs.getString("book_name");
						book_rate = rs.getDouble("book_price");
						books_vector.add(book_no+" "+book_name+" "+book_rate);
					}
					rs.close();
					ps.close();
					disconnect();
					menu_list.setListData(books_vector);			
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(me, "TxtSearch: "+ex.toString());
				}
			}
		});


		books_vector = new Vector <String>();
		menu_list = new JList <String>(books_vector);
		sp = new JScrollPane(menu_list);
		sp.setBounds(30, 120, 230, 350);
		left_panel.add(sp);
		menu_list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(menu_list.getSelectedIndex() >= 0)
				{
					String item = menu_list.getSelectedValue().toString();

					int i = item.indexOf(" ");
					String bno = item.substring(0, i);

					int j = item.lastIndexOf(" ");
					String bname = item.substring(i+1, j);

					String bprice = item.substring(j+1);

					//JOptionPane.showMessageDialog(me, i+"");

					txt_book_num.setText(bno);
					txt_book_name.setText(bname);
					txt_price.setText(bprice);
				}
			}
		});

		// right_panel
		right_panel = new JPanel();
		right_panel.setLayout(null);

		lbl = new JLabel("MENU SECTION", JLabel.CENTER);
		lbl.setFont(new Font("Calibri", Font.BOLD, 20));  
		lbl.setBounds(140, 20, 280, 35);
		lbl.setForeground(Color.RED);
		right_panel.add(lbl);

		lbl = new JLabel("Book No.");
		lbl.setBounds(50, 70, 135, 35);
		right_panel.add(lbl);

		txt_book_num = new JTextField();
		txt_book_num.setBounds(195, 70, 200, 35);
		right_panel.add(txt_book_num);
		txt_book_num.setEditable(false);
		

		lbl = new JLabel("Book Name");
		lbl.setBounds(50, 115, 135, 35);
		right_panel.add(lbl);

		txt_book_name= new JTextField();
		txt_book_name.setBounds(195, 115, 200, 35);
		right_panel.add(txt_book_name);

		lbl = new JLabel("Book Price");
		lbl.setBounds(50, 160, 135, 35);
		right_panel.add(lbl);

		txt_price = new JTextField();
		txt_price.setBounds(195, 160, 200, 35);
		right_panel.add(txt_price);

		b = new JButton("INSERT");
		b.setBounds(50, 220, 135, 35);
		right_panel.add(b);
		b.addActionListener(this);

		b = new JButton("UPDATE");
		b.setBounds(195, 220, 135, 35);
		right_panel.add(b);
		b.addActionListener(this);

		b = new JButton("DELETE");
		b.setBounds(50, 275, 135, 35);
		right_panel.add(b);
		b.addActionListener(this);

		b = new JButton("CLEAR");
		b.setBounds(195, 275, 135, 35);
		right_panel.add(b);
		b.addActionListener(this);

		lbl_msg = new JLabel();
		lbl_msg.setBounds(50, 300, 280, 100);
		right_panel.add(lbl_msg);

		// JSplitPane
		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left_panel, right_panel);
		this.add(jsp, BorderLayout.CENTER);
		jsp.setDividerLocation(300);
		jsp.setDividerSize(2);
		this.setVisible(true);
		this.setSize(850, 550);
		getBooks();

		this.addInternalFrameListener(this);
	}

	public void connect()
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybookdb", "root", "raj05");										
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "connect Method: "+ex.toString());
		}
	}

	public void disconnect()
	{
		try
		{
			if(!con.isClosed())
			con.close();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "disconnect Method: "+ex.toString());
		}
	}

	public void getBooks()
	{
		try
		{
			String sql = "select * from books";
			connect();
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			books_vector.clear();

			int book_no = 0;
			String book_name = "";
			double book_rate = 0;

			while(rs.next())
			{
				book_no = rs.getInt("book_no");
				book_name = rs.getString("book_name");
				book_rate = rs.getDouble("book_price");
				books_vector.add(book_no+" "+book_name+" "+book_rate);
			}
			rs.close();
			ps.close();
			disconnect();
			menu_list.setListData(books_vector);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "getBooks Method: "+ex.toString());
		}
	}

	public void clear_data()
	{
		txt_search.setText("");
		txt_book_num.setText("");
		txt_book_name.setText("");
		txt_price.setText("");
		lbl_msg.setText("");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String text = e.getActionCommand();

		String book_name = "", book_price = "", book_no = "";
		double book_rate = 0;
		String err_msg = "";
		int n = 0;
				
		switch(text)
		{
			case "INSERT":
					try
					{
						book_no = txt_book_num.getText();
						book_name = txt_book_name.getText().trim().toUpperCase();
						book_price = txt_price.getText().trim();
						/*
						if(book_no.length() == 0)
						err_msg = err_msg + "Book Number is Required\n";
						*/
						if(book_name.length() == 0)
						err_msg = err_msg + "Book name is required\n";

						if(book_price.length() == 0)
						err_msg = err_msg + "Book price is required\n";
						else
						{
							try
							{
								book_rate = Double.parseDouble(book_price);	
								if(book_rate <= 0)
								err_msg = err_msg + "Book price cannot be zero/negative";
							}
							catch(Exception ex)
							{
								err_msg = err_msg + "Invalid book price\n";
							}
						}

						if(!err_msg.equals(""))
						{
							JOptionPane.showMessageDialog(this, err_msg);
						}
						else
						{
							// Insert code
							String sql = "insert into books (book_name, book_price) values (?, ?)";
							connect();
							ps = con.prepareStatement(sql);
							ps.setString(1, book_name);
							ps.setDouble(2, book_rate);
							n = ps.executeUpdate();
							ps.close();
							disconnect();

							if(n == 1)
							{
								lbl_msg.setForeground(Color.BLUE);
								lbl_msg.setText("Record saved..");
								getBooks();
							}							
							else
							{
								lbl_msg.setForeground(Color.RED);
								lbl_msg.setText("Record not saved..");
							}
						}
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(this,"INSERT SECTION: "+ex.toString());
					}
					break;

			case "UPDATE":
					try
					{
						String bno = txt_book_num.getText().trim();

						if(bno.length() == 0)
						JOptionPane.showMessageDialog(this,"Book is not selected. Cannot update");
						else
						{
							book_no = bno;
							book_name = txt_book_name.getText().trim().toUpperCase();
							book_price = txt_price.getText().trim();

							if(book_name.length() == 0)
							err_msg = err_msg + "Book name is required\n";

							if(book_price.length() == 0)
							err_msg = err_msg + "Book price is required\n";
							else
							{
								try
								{
									book_rate = Double.parseDouble(book_price);	
									if(book_rate <= 0)
									err_msg = err_msg + "Book price cannot be zero/negative";
								}
								catch(Exception ex)
								{
									err_msg = err_msg + "Invalid book price\n";
								}
							}

							if(!err_msg.equals(""))
							JOptionPane.showMessageDialog(this, err_msg);
							else
							{
								// code to update
								String sql = "update books set book_name = ?, book_price = ? where book_no = ?";
								connect();
								ps = con.prepareStatement(sql);
								ps.setString(1, book_name);
								ps.setDouble(2, book_rate);
								ps.setString(3, book_no);
								n = ps.executeUpdate();
								ps.close();
								disconnect();

								if(n == 1)
								{
									getBooks();
									clear_data();
									lbl_msg.setForeground(Color.BLUE);
									lbl_msg.setText("Book details updated..");
								}
								else
								{
									lbl_msg.setForeground(Color.RED);
									lbl_msg.setText("Book details not updated..");
								}
							}
						}
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(this, "Update Sectiion: "+ex.toString());
					}
					break;

			case "DELETE":
					try
					{
						String bno = txt_book_num.getText().trim();
						if(bno.length() == 0)
						JOptionPane.showMessageDialog(this, "Invalid Book number. Cannot delete");
						else
						{
							int book_num = Integer.parseInt(bno);
							String sql = "delete from books where book_no = ?";
							connect();
							ps = con.prepareStatement(sql);
							ps.setInt(1, book_num);
							n = ps.executeUpdate();
							ps.close();
							disconnect();

							if(n == 1)
							{								
								getBooks();
								clear_data();
								lbl_msg.setForeground(Color.BLUE);
								lbl_msg.setText("Book deleted..");
							}
							else
							{
								lbl_msg.setForeground(Color.RED);
								lbl_msg.setText("Book not deleted..");
							}
						}
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(this, "Delete Section: "+ex.toString());
					}
					break;

			case "CLEAR":
					clear_data();
					break;
		}
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e){}

	@Override
	public void internalFrameClosed(InternalFrameEvent e){}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e){}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e){}

	@Override
	public void internalFrameIconified(InternalFrameEvent e){}

	@Override
	public void internalFrameOpened(InternalFrameEvent e){}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		MainFrame.menu_frame = null;
		this.dispose();
	}	
}

class BillingFrame extends JInternalFrame implements ActionListener, InternalFrameListener
{	
	private JSplitPane jsp;
	private JScrollPane sp1, sp2;
	private JLabel lbl;
	private JTextField txt_search, txt_bill_date, txt_bill_no, txt_cust_name, txt_mobile_no;
	private JTextField txt_book_name, txt_book_price, txt_book_quantity, txt_discounted_bill, txt_amount, txt_total_bill,txt_discount;
	private JList menu_list;
	private Vector <String> books_vector;
	private JPanel left_panel, right_panel, panel1, panel2, panel3;
	private JTable tbl_items;
	private JButton b;
	private DefaultTableModel table_model;
	private Vector <Vector> rows;
	private Vector <String> cols;
	private static Connection con;
	private static PreparedStatement ps;
	private BillingFrame me = this;

	public BillingFrame()
	{
		super("Billing Section", true, true, true, true);

		// left_panel
		left_panel = new JPanel();
		left_panel.setLayout(null);

		lbl = new JLabel("Search:");
		lbl.setBounds(20, 20, 230, 35);
		left_panel.add(lbl);

		txt_search = new JTextField();
		txt_search.setBounds(20, 60, 230, 35);
		left_panel.add(txt_search);
		txt_search.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String search_text = txt_search.getText().trim().toUpperCase();

					connect();

					String sql = "select book_name, book_price from books where book_name like '%"+search_text+"%'";
					ps = con.prepareStatement(sql);					
					ResultSet rs = ps.executeQuery();

					books_vector.clear();

					//int book_no = 0;
					String book_name = "";
					double book_rate = 0;

					while(rs.next())
					{
						//dish_no = rs.getInt("book_no");
						book_name = rs.getString("book_name");
						book_rate = rs.getDouble("book_price");
						books_vector.add(book_name+" "+book_rate);
					}
					rs.close();
					ps.close();
					disconnect();
					menu_list.setListData(books_vector);			
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(me, "TxtSearch: "+ex.toString());
				}
			}
		});


		books_vector = new Vector <String>();
		menu_list = new JList(books_vector);
		sp1 = new JScrollPane(menu_list);
		sp1.setBounds(20, 120, 230, 450);
		left_panel.add(sp1);
		menu_list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(menu_list.getSelectedIndex() >= 0)
				{
					String item = menu_list.getSelectedValue().toString();
				

					int j = item.lastIndexOf(" ");
					String bprice = item.substring(j+1);
					String bname = item.substring(0, j);
					//JOptionPane.showMessageDialog(me, i+"");

					txt_book_name.setText(bname);
					txt_book_price.setText(bprice);
				}
			}
		});

		// right_panel
		right_panel = new JPanel();
		right_panel.setLayout(null);

		lbl = new JLabel("Billing Section", JLabel.CENTER);
		lbl.setFont(new Font("Calibri", Font.BOLD, 20));
		lbl.setBounds(30, 10, 580, 50);
		right_panel.add(lbl);

		panel1 = new JPanel();
		panel1.setBounds(10, 70, 580, 180);
		right_panel.add(panel1);

		panel1.setLayout(new GridLayout(5, 4, 10, 1));

		lbl = new JLabel("Bill Date");
		panel1.add(lbl);

		txt_bill_date = new JTextField();
		java.util.Date d = new java.util.Date();
		java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		//java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("dd-MM-yyyy");
		String cur_date_time = f.format(d);
		txt_bill_date.setText(cur_date_time);
		txt_bill_date.setEditable(false);

		panel1.add(txt_bill_date);

		lbl = new JLabel("Bill No.");
		panel1.add(lbl);

		txt_bill_no = new JTextField();
		panel1.add(txt_bill_no);
		txt_bill_no.setEditable(false);

		lbl = new JLabel("Customer Name");
		panel1.add(lbl);

		txt_cust_name = new JTextField();
		panel1.add(txt_cust_name);

		panel1.add(new JLabel());
		panel1.add(new JLabel());

		lbl = new JLabel("Mobile No.");
		panel1.add(lbl);

		txt_mobile_no = new JTextField();
		panel1.add(txt_mobile_no);

		panel1.add(new JLabel());
		panel1.add(new JLabel());

		lbl = new JLabel("Book Name");
		panel1.add(lbl);

		lbl = new JLabel("Book Price");
		panel1.add(lbl);

		lbl = new JLabel("Quantity");
		panel1.add(lbl);

		lbl = new JLabel("Amount");
		panel1.add(lbl);

		txt_book_name = new JTextField();
		panel1.add(txt_book_name);
		txt_book_name.setEditable(false);

		txt_book_price = new JTextField();
		panel1.add(txt_book_price);
		txt_book_price.setEditable(false);

		txt_book_quantity = new JTextField();
		panel1.add(txt_book_quantity);
		txt_book_quantity.setText(1+"");

		txt_book_quantity.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String book_name = txt_book_name.getText();
				String book_price = txt_book_price.getText();
				String quantity = txt_book_quantity.getText().trim();
				double total_bill = 0;
				if(book_name.length() != 0  && book_price.length() != 0 && quantity.length() != 0)
				{
					try
					{

						Integer qty = Integer.parseInt(quantity);
						//JOptionPane.showMessageDialog(me, qty.getClass().getName());

						double amount = Double.parseDouble(book_price) * qty;
						txt_amount.setText(amount+"");

						Vector <String>single_row = new Vector <String>();

						single_row.add(book_name);
						single_row.add(book_price);
						single_row.add(quantity);
						single_row.add(amount+"");
						rows.add(single_row);
						table_model.setDataVector(rows, cols);
						txt_book_name.setText("");
						txt_book_price.setText("");
						txt_book_quantity.setText(1+"");
						txt_amount.setText("");

						total_bill = Double.parseDouble(txt_total_bill.getText()) + amount;
						txt_total_bill.setText(total_bill+"");						
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(me, "TxtQuantity Section: "+ex.toString());
					}
				}
				else
				JOptionPane.showMessageDialog(me, "Book quantity not selected");
			}
		});

		txt_amount = new JTextField();
		panel1.add(txt_amount);
		txt_amount.setEditable(false);

		panel2 = new JPanel();
		panel2.setBounds(10, 240, 580, 250);
		right_panel.add(panel2);

		panel2.setLayout(null);

		lbl = new JLabel("Items:");
		lbl.setBounds(10, 10, 150, 30);
		panel2.add(lbl);

		cols = new Vector <String>();
		String arr[] = {"PRODUCT NAME", "PRICE", "QUANTITY", "AMOUNT"};
		for(String item : arr)
		{
			cols.add(item);
		}

		rows = new Vector<Vector>();

		table_model = new DefaultTableModel(rows, cols);
		tbl_items = new JTable(table_model);
		sp2 = new JScrollPane(tbl_items);
		sp2.setBounds(10, 45, 560, 180);
		panel2.add(sp2);
		tbl_items.getTableHeader().setBackground(Color.WHITE);

		panel3 = new JPanel();
		panel3.setBounds(10, 490, 580, 100);
		right_panel.add(panel3);

		panel3.setLayout(new GridLayout(3, 3, 30, 1));

		b = new JButton("Delete Selected Row");
		b.setActionCommand("Delete");
		panel3.add(b);
		b.addActionListener(this);

		lbl = new JLabel("Total Bill", JLabel.RIGHT);
		lbl.setFont(new Font("Calibri", Font.BOLD, 20));
		panel3.add(lbl);

		txt_total_bill = new JTextField("0");
		panel3.add(txt_total_bill);
		txt_total_bill.setEditable(false);
		txt_total_bill.setFont(new Font("Calibri", Font.BOLD, 20));

		b = new JButton("Save and Print");
		b.setActionCommand("Save");
		panel3.add(b);
		b.addActionListener(this);

		JTextField txt_book_discount = new JTextField();
		panel3.add(txt_book_discount);
		txt_book_discount.setText(10+"");

		txt_book_discount.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				
				String total_bill = txt_total_bill.getText().trim();
				String discount = txt_book_discount.getText().trim();
				double discounted_price=0;
				if(total_bill.length() != 0 )
				{
					try
					{
						//Integer dis = Integer.parseInt(total_bill);
						//JOptionPane.showMessageDialog(me, qty.getClass().getName());
						//double amount = Double.parseDouble(book_price) * qty;
						Integer dis = Integer.parseInt(discount);
						Double disc = (Double.parseDouble(total_bill)*dis)/100;
						txt_discount.setText(disc+"");
					
						txt_book_discount.setText(10+"");
						double final1 = Double.parseDouble(total_bill)-disc;
						txt_discounted_bill.setText(final1+"");						
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(me, "TxtQuantity Section: "+ex.toString());
					}
				}
				else
				JOptionPane.showMessageDialog(me, "Book quantity not selected");
			}
		});

		txt_discount = new JTextField();
		panel3.add(txt_discount);
		txt_discount.setEditable(false);

		JLabel ds = new JLabel("Discounted Bill");
		ds.setFont(new Font("Calibri", Font.BOLD, 20));
		panel3.add(ds);		

		txt_discounted_bill = new JTextField();
		panel3.add(txt_discounted_bill);
		txt_discounted_bill.setEditable(false);

		// JSplitPane
		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left_panel, right_panel);
		jsp.setDividerLocation(270);
		jsp.setDividerSize(2);
		this.add(jsp, BorderLayout.CENTER);

		this.setVisible(true);
		this.setSize(910, 680);
		getBooks();
		getMaxBillNo();

		this.addInternalFrameListener(this);
	}

	public void connect()
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybookdb", "root", "raj05");										
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "connect Method: "+ex.toString());
		}
	}

	public void disconnect()
	{
		try
		{
			if(!con.isClosed())
			con.close();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "disconnect Method: "+ex.toString());
		}
	}

	public void getBooks()
	{
		try
		{
			String sql = "select book_name, book_price from books";
			connect();
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			books_vector.clear();

			//int book_no = 0;
			String book_name = "";
			double book_rate = 0;

			while(rs.next())
			{
				//book_no = rs.getInt("dish_no");
				book_name = rs.getString("book_name");
				book_rate = rs.getDouble("book_price");
				books_vector.add(book_name+" "+book_rate);
			}
			rs.close();
			ps.close();
			disconnect();
			menu_list.setListData(books_vector);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "getBooks Method: "+ex.toString());
		}
	}

	public void getMaxBillNo()
	{
		try
		{
			String sql = "select max(bill_no) from bills";
			connect();
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int bill_number = 0;

			if(rs.next())
			{
				bill_number = rs.getInt(1);				
			}
			rs.close();
			ps.close();
			disconnect();

			if(bill_number == 0)
			bill_number =  1;
			else
			bill_number = bill_number + 1;

			txt_bill_no.setText(bill_number+"");
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "getMaxBillNo: "+ex.toString());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String text = e.getActionCommand();

		switch(text)
		{
			case "Save": 
				try
				{
					String cust_name = txt_cust_name.getText().trim().toUpperCase();
					String mob_no = txt_mobile_no.getText().trim();
					double total_bill = Double.parseDouble(txt_total_bill.getText());
					double show = Double.parseDouble(txt_discounted_bill.getText());
					String err_msg = "";
					String bill_date = txt_bill_date.getText().trim();
					if(cust_name.length() == 0)
					err_msg = err_msg + "Customer name is required\n";

					if(mob_no.length() != 0)
					{
						if(mob_no.length() != 10 )
						err_msg = err_msg + "Mobile no. is invalid\n";
					}
					else
					{
						err_msg = err_msg + "Mobile no. is required\n";
					}

					if(total_bill <= 0)
					err_msg = err_msg + "No book selected\n";

					if(err_msg.length() == 0)
					{
						java.util.Date d = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(bill_date);
						// String sql = "insert into bills (bill_date, cust_name, mobile_no, total_bill) values(str_to_date(?, '%d-%m-%y %h:%i:%s'), ?, ?, ?)";							
						String sql = "insert into bills (bill_date, cust_name, mobile_no, total_bill) values(?, ?, ?, ?)";							
						connect();
						ps = con.prepareStatement(sql);
						//ps.setString(1, bill_date);
						ps.setDate(1, new java.sql.Date(d.getTime()));
						ps.setString(2, cust_name);
						ps.setString(3, mob_no);
						ps.setDouble(4, show);

						int n = ps.executeUpdate();
						ps.close();
						disconnect();
						
						if(n == 1)
						{
							sql = "insert into bill_details values(?, ?, ?, ?, ?)";
							//JOptionPane.showMessageDialog(this, "Bill Saved..");
							connect();
							ps = con.prepareStatement(sql);
							int bill_no = Integer.parseInt(txt_bill_no.getText());
							
							for(Vector single_row: rows)
							{
								ps.setInt(1, bill_no);
								ps.setString(2, single_row.get(0).toString());
								ps.setDouble(3, Double.parseDouble(single_row.get(1).toString()));
								ps.setInt(4, Integer.parseInt(single_row.get(2).toString()));
								ps.setDouble(5, Double.parseDouble(single_row.get(3).toString()));
								n = ps.executeUpdate();																
							}
							ps.close();
							disconnect();

							if(n == 1)
							{
								// creating pdf

								Document doc = new Document();
								PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream("C:/bills_pdf/"+bill_no+".pdf"));
								doc.open();

								// Title
								com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, BaseColor.RED);
								Paragraph p = new Paragraph("*** BILL ***", f);
								p.setAlignment(1);

								/*
	           					Alignment 
	           					0 = left
	           					1 = center
	           					2 = right
	           					*/
	           					doc.add(p);

	           					doc.add(new Paragraph("Bill Number: "+bill_no));
	           					doc.add(new Paragraph("Bill Date: "+bill_date));
	           					doc.add(new Paragraph("Customer Name: "+cust_name));
	           					doc.add(new Paragraph("Mobile No.: "+mob_no));
	           					doc.add(new Paragraph("\n\n"));
	           					doc.add(new Paragraph("Bill Details:"));


	           					PdfPTable bill_table = new PdfPTable(4);
            					bill_table.setWidthPercentage(100);
            					bill_table.setSpacingBefore(11f);
            					bill_table.setSpacingAfter(11f);

            					float col_width[] = {3f, 2f, 2f, 2f};
            					bill_table.setWidths(col_width);

            					String cols[] = {"Book Name", "Book Rate", "Quantity", "Amount"};

            					for(int i = 0; i < cols.length; i++)
            					{
                					PdfPCell c = new PdfPCell(new Paragraph(cols[i], new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11, com.itextpdf.text.Font.BOLD, BaseColor.BLACK)));
                					c.setHorizontalAlignment(Element.ALIGN_CENTER);       
                					bill_table.addCell(c);
            					}

            					for(Vector <String> single_row : rows)
            					{
            						for(Object item : single_row)
            						{
            							PdfPCell c = new PdfPCell(new Paragraph((String)item));
                    					c.setHorizontalAlignment(Element.ALIGN_CENTER);       
                    					bill_table.addCell(c);
            						}
            					}

            					doc.add(bill_table);

            					p = new Paragraph("Total Bill Amount: "+show+" Rs.");
            					p.setAlignment(2);
            					doc.add(p);
            					double ts = total_bill-show;
            					p = new Paragraph("total savings "+ts+"Rs.");
            					p.setAlignment(2);
            					doc.add(p);

            					p = new Paragraph("*** Thank You!!! ***");
            					p.setAlignment(1);
            					doc.add(p);

            					p = new Paragraph("*** Visit Again!!!***");
            					p.setAlignment(1);
            					doc.add(p);

	           					doc.close();
           	 					w.close();	
								JOptionPane.showMessageDialog(this, "Bill Saved..");
							}
							else
							JOptionPane.showMessageDialog(this, "Bill not saved..");
						}
						else
							JOptionPane.showMessageDialog(this, "Bill Not Saved..");
					}
					else
					JOptionPane.showMessageDialog(this, err_msg);
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(this, "Save and Print: "+ex.toString());
				}
			break;

			case "Delete":
				int selected_row_index = tbl_items.getSelectedRow();
				double amount = Double.parseDouble(table_model.getValueAt(selected_row_index, 3).toString());
				double total_bill = Double.parseDouble(txt_total_bill.getText());
				total_bill = total_bill - amount;
				txt_total_bill.setText(total_bill+"");
				table_model.removeRow(selected_row_index);
				break;
		}
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e){}

	@Override
	public void internalFrameClosed(InternalFrameEvent e){}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e){}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e){}

	@Override
	public void internalFrameIconified(InternalFrameEvent e){}

	@Override
	public void internalFrameOpened(InternalFrameEvent e){}

	@Override
	public void internalFrameClosing(InternalFrameEvent e)
	{
		MainFrame.billing_frame = null;
		this.dispose();
	}	
}

class MainFrame extends JFrame implements ActionListener
{
	private JToolBar tbar;
	private JDesktopPane jdp;
	public static MenuFrame menu_frame = null;
	public static BillingFrame billing_frame = null;
	public MainFrame()
	{
		tbar = new JToolBar();
		this.add(tbar, BorderLayout.NORTH);
		tbar.setFloatable(false);
		tbar.setLayout(new GridLayout(1, 2));

		String arr[] = {"MENU SECTION", "BILLING SECTION"};

		for(String item : arr)
		{
			JButton b = new JButton(item);
			tbar.add(b);
			b.addActionListener(this);
		}

		jdp = new JDesktopPane();
		this.add(jdp, BorderLayout.CENTER);

		this.setVisible(true);
		this.setSize(1200, 900);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String text = e.getActionCommand();

		switch(text)
		{
			case "MENU SECTION": 
					if(menu_frame == null)
					{
						menu_frame = new MenuFrame();
						jdp.add(menu_frame);
					}
					break;
			case "BILLING SECTION":
				if(billing_frame == null)
				{
					billing_frame = new BillingFrame();
					jdp.add(billing_frame);
				}
				break;
		}
	}
}

class Book
{
	public static void main(String ...args)
	{
		MainFrame f = new MainFrame();
	}
}