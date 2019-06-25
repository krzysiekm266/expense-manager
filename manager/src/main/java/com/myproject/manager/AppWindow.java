package com.myproject.manager;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.myproject.manager.api.Dao;
import com.myproject.manager.configuration.ManagerConfiguration;
import com.myproject.manager.dao.HibernateDao;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.Calendar;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Label;

public class AppWindow {
	private static final  ApplicationContext ctx = new AnnotationConfigApplicationContext(ManagerConfiguration.class);
	private Dao hibernateDao ;
	private DefaultTableModel tableModel ;
	private JFrame frame;
	private JTable tableExpenses;
	private JTextField textPdoductName;
	private JTextField textProductDescription;
	private JTextField textShop;
	private JTextField textSearch;
	private JTextField textUser;
	private JTextField textPassword;
	private JTextField textUrl;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = new AppWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppWindow() {
		
		 	initialize();	
		 	hibernateDao = ctx.getBean(HibernateDao.class);
			tableModel = ctx.getBean(DefaultTableModel.class);
			tableExpenses.setModel(tableModel);
		
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 881, 626);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		frame.getContentPane().add(tabbedPane, gbc_tabbedPane);
		
		JPanel panelExpenses = new JPanel();
		tabbedPane.addTab("Wydatki", null, panelExpenses, null);
		GridBagLayout gbl_panelExpenses = new GridBagLayout();
		gbl_panelExpenses.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panelExpenses.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelExpenses.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_panelExpenses.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelExpenses.setLayout(gbl_panelExpenses);
		
		JPanel panelSearchOptions = new JPanel();
		GridBagConstraints gbc_panelSearchOptions = new GridBagConstraints();
		gbc_panelSearchOptions.gridheight = 3;
		gbc_panelSearchOptions.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelSearchOptions.insets = new Insets(5, 5, 5, 5);
		gbc_panelSearchOptions.gridx = 1;
		gbc_panelSearchOptions.gridy = 0;
		panelExpenses.add(panelSearchOptions, gbc_panelSearchOptions);
		GridBagLayout gbl_panelSearchOptions = new GridBagLayout();
		gbl_panelSearchOptions.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelSearchOptions.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panelSearchOptions.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelSearchOptions.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelSearchOptions.setLayout(gbl_panelSearchOptions);
		
		JButton btnSearch = new JButton("Search");
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.gridheight = 2;
		gbc_btnSearch.insets = new Insets(0, 0, 5, 5);
		gbc_btnSearch.gridx = 0;
		gbc_btnSearch.gridy = 0;
		panelSearchOptions.add(btnSearch, gbc_btnSearch);
		
		
		
		JLabel lblSzukaneSowa = new JLabel("Szukane słowa: ");
		GridBagConstraints gbc_lblSzukaneSowa = new GridBagConstraints();
		gbc_lblSzukaneSowa.insets = new Insets(0, 0, 5, 5);
		gbc_lblSzukaneSowa.anchor = GridBagConstraints.EAST;
		gbc_lblSzukaneSowa.gridx = 1;
		gbc_lblSzukaneSowa.gridy = 0;
		panelSearchOptions.add(lblSzukaneSowa, gbc_lblSzukaneSowa);
		
		
		
		textSearch = new JTextField();
		GridBagConstraints gbc_textSearch = new GridBagConstraints();
		gbc_textSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_textSearch.gridwidth = 4;
		gbc_textSearch.insets = new Insets(0, 0, 5, 5);
		gbc_textSearch.gridx = 2;
		gbc_textSearch.gridy = 0;
		panelSearchOptions.add(textSearch, gbc_textSearch);
		textSearch.setColumns(10);
		
		
		
		JLabel lblOptionPriceFrom = new JLabel("Cena min:");
		lblOptionPriceFrom.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblOptionPriceFrom = new GridBagConstraints();
		gbc_lblOptionPriceFrom.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptionPriceFrom.gridx = 1;
		gbc_lblOptionPriceFrom.gridy = 1;
		panelSearchOptions.add(lblOptionPriceFrom, gbc_lblOptionPriceFrom);
		
		JSpinner spinnerPriceFrom = new JSpinner();
		spinnerPriceFrom.setModel(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 0.1));
		GridBagConstraints gbc_spinnerPriceFrom = new GridBagConstraints();
		gbc_spinnerPriceFrom.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerPriceFrom.weightx = 100.0;
		gbc_spinnerPriceFrom.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerPriceFrom.gridx = 2;
		gbc_spinnerPriceFrom.gridy = 1;
		panelSearchOptions.add(spinnerPriceFrom, gbc_spinnerPriceFrom);
		
		JLabel lblOptionPriceTo = new JLabel("Cena max:");
		GridBagConstraints gbc_lblOptionPriceTo = new GridBagConstraints();
		gbc_lblOptionPriceTo.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptionPriceTo.gridx = 4;
		gbc_lblOptionPriceTo.gridy = 1;
		panelSearchOptions.add(lblOptionPriceTo, gbc_lblOptionPriceTo);
		
		JSpinner spinnerPriceTo = new JSpinner();
		spinnerPriceTo.setModel(new SpinnerNumberModel(1000.0, 0.0, 1000000.0, 0.1));
		GridBagConstraints gbc_spinnerPriceTo = new GridBagConstraints();
		gbc_spinnerPriceTo.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerPriceTo.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerPriceTo.weightx = 100.0;
		gbc_spinnerPriceTo.gridx = 5;
		gbc_spinnerPriceTo.gridy = 1;
		panelSearchOptions.add(spinnerPriceTo, gbc_spinnerPriceTo);
		
		JPanel panelMenu = new JPanel();
		GridBagConstraints gbc_panelMenu = new GridBagConstraints();
		gbc_panelMenu.insets = new Insets(0, 0, 5, 0);
		gbc_panelMenu.gridheight = 3;
		gbc_panelMenu.gridx = 6;
		gbc_panelMenu.gridy = 0;
		panelSearchOptions.add(panelMenu, gbc_panelMenu);
		GridBagLayout gbl_panelMenu = new GridBagLayout();
		gbl_panelMenu.columnWidths = new int[]{0, 0, 0};
		gbl_panelMenu.rowHeights = new int[]{0, 0, 0};
		gbl_panelMenu.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panelMenu.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelMenu.setLayout(gbl_panelMenu);
		
		JButton btnEdit = new JButton("Edycja...");
		GridBagConstraints gbc_btnEdit = new GridBagConstraints();
		gbc_btnEdit.insets = new Insets(0, 0, 5, 5);
		gbc_btnEdit.gridx = 0;
		gbc_btnEdit.gridy = 0;
		panelMenu.add(btnEdit, gbc_btnEdit);
		
		
		
		JButton btnDefaultFilters = new JButton("Wyczyść filtry");
		GridBagConstraints gbc_btnDefaultFilters = new GridBagConstraints();
		gbc_btnDefaultFilters.insets = new Insets(0, 0, 0, 5);
		gbc_btnDefaultFilters.gridx = 0;
		gbc_btnDefaultFilters.gridy = 1;
		panelMenu.add(btnDefaultFilters, gbc_btnDefaultFilters);
		
		JLabel lblOptionDateMin = new JLabel("Data zakupu od:");
		lblOptionDateMin.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblOptionDateMin = new GridBagConstraints();
		gbc_lblOptionDateMin.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptionDateMin.gridx = 1;
		gbc_lblOptionDateMin.gridy = 2;
		panelSearchOptions.add(lblOptionDateMin, gbc_lblOptionDateMin);
		
		JSpinner spinnerDateMin = new JSpinner();
		spinnerDateMin.setModel(new SpinnerDateModel(new Date(946681200000L), new Date(946681200000L), null, Calendar.DAY_OF_YEAR));
		spinnerDateMin.setEditor(new JSpinner.DateEditor(spinnerDateMin, "dd/MM/yyyy"));
		GridBagConstraints gbc_spinnerDateMin = new GridBagConstraints();
		gbc_spinnerDateMin.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerDateMin.weightx = 100.0;
		gbc_spinnerDateMin.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDateMin.gridx = 2;
		gbc_spinnerDateMin.gridy = 2;
		panelSearchOptions.add(spinnerDateMin, gbc_spinnerDateMin);
		
		JLabel lblOptionDateMax = new JLabel("do");
		GridBagConstraints gbc_lblOptionDateMax = new GridBagConstraints();
		gbc_lblOptionDateMax.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptionDateMax.gridx = 4;
		gbc_lblOptionDateMax.gridy = 2;
		panelSearchOptions.add(lblOptionDateMax, gbc_lblOptionDateMax);
		
		JSpinner spinnerDateMax = new JSpinner();
		spinnerDateMax.setModel(new SpinnerDateModel(new Date(1556661600000L), new Date(946681200000L), null, Calendar.DAY_OF_YEAR));
		spinnerDateMax.setEditor(new JSpinner.DateEditor(spinnerDateMax, "dd/MM/yyyy"));
		spinnerDateMax.setValue(new Date());
		GridBagConstraints gbc_spinnerDateMax = new GridBagConstraints();
		gbc_spinnerDateMax.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerDateMax.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDateMax.gridx = 5;
		gbc_spinnerDateMax.gridy = 2;
		panelSearchOptions.add(spinnerDateMax, gbc_spinnerDateMax);
		
		JPanel panelSummary = new JPanel();
		GridBagConstraints gbc_panelSummary = new GridBagConstraints();
		gbc_panelSummary.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelSummary.gridwidth = 5;
		gbc_panelSummary.insets = new Insets(0, 0, 0, 5);
		gbc_panelSummary.gridx = 1;
		gbc_panelSummary.gridy = 3;
		panelSearchOptions.add(panelSummary, gbc_panelSummary);
		GridBagLayout gbl_panelSummary = new GridBagLayout();
		gbl_panelSummary.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelSummary.rowHeights = new int[]{0, 0};
		gbl_panelSummary.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelSummary.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelSummary.setLayout(gbl_panelSummary);
		
		JLabel lblExpensesSummary = new JLabel("Suma wydatków:");
		GridBagConstraints gbc_lblExpensesSummary = new GridBagConstraints();
		gbc_lblExpensesSummary.anchor = GridBagConstraints.WEST;
		gbc_lblExpensesSummary.insets = new Insets(0, 0, 0, 5);
		gbc_lblExpensesSummary.gridx = 1;
		gbc_lblExpensesSummary.gridy = 0;
		panelSummary.add(lblExpensesSummary, gbc_lblExpensesSummary);
		
		JLabel lblExpensesSummaryValue = new JLabel("*");
		GridBagConstraints gbc_lblExpensesSummaryValue = new GridBagConstraints();
		gbc_lblExpensesSummaryValue.gridwidth = 2;
		gbc_lblExpensesSummaryValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblExpensesSummaryValue.gridx = 2;
		gbc_lblExpensesSummaryValue.gridy = 0;
		panelSummary.add(lblExpensesSummaryValue, gbc_lblExpensesSummaryValue);
		
		JLabel lblExpensesFindRows = new JLabel("Znalezione wiersze:");
		GridBagConstraints gbc_lblExpensesFindRows = new GridBagConstraints();
		gbc_lblExpensesFindRows.anchor = GridBagConstraints.EAST;
		gbc_lblExpensesFindRows.gridwidth = 2;
		gbc_lblExpensesFindRows.insets = new Insets(0, 0, 0, 5);
		gbc_lblExpensesFindRows.gridx = 6;
		gbc_lblExpensesFindRows.gridy = 0;
		panelSummary.add(lblExpensesFindRows, gbc_lblExpensesFindRows);
		
		Label lblExpensesFindRowsValue = new Label("***");
		//lblExpensesFindRowsValue.setText(String.valueOf(hibernateDao.printRows()));
		GridBagConstraints gbc_lblExpensesFindRowsValue = new GridBagConstraints();
		gbc_lblExpensesFindRowsValue.anchor = GridBagConstraints.WEST;
		gbc_lblExpensesFindRowsValue.gridwidth = 2;
		gbc_lblExpensesFindRowsValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblExpensesFindRowsValue.gridx = 8;
		gbc_lblExpensesFindRowsValue.gridy = 0;
		panelSummary.add(lblExpensesFindRowsValue, gbc_lblExpensesFindRowsValue);
		
		JLabel lblExpensesAllProducts = new JLabel("Produktów w bazie:");
		GridBagConstraints gbc_lblExpensesAllProducts = new GridBagConstraints();
		gbc_lblExpensesAllProducts.anchor = GridBagConstraints.EAST;
		gbc_lblExpensesAllProducts.insets = new Insets(0, 0, 0, 5);
		gbc_lblExpensesAllProducts.gridx = 10;
		gbc_lblExpensesAllProducts.gridy = 0;
		panelSummary.add(lblExpensesAllProducts, gbc_lblExpensesAllProducts);
		
		JLabel lblExpensesAllProductsValue = new JLabel("***");
		//lblExpensesAllProductsValue.setText(String.valueOf(hibernateDao.rowCountAll()));
		GridBagConstraints gbc_lblExpensesAllProductsValue = new GridBagConstraints();
		gbc_lblExpensesAllProductsValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblExpensesAllProductsValue.anchor = GridBagConstraints.WEST;
		gbc_lblExpensesAllProductsValue.gridwidth = 2;
		gbc_lblExpensesAllProductsValue.gridx = 11;
		gbc_lblExpensesAllProductsValue.gridy = 0;
		panelSummary.add(lblExpensesAllProductsValue, gbc_lblExpensesAllProductsValue);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.weighty = 100.0;
		gbc_scrollPane.weightx = 100.0;
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		panelExpenses.add(scrollPane, gbc_scrollPane);
		
		tableExpenses = new JTable();
		tableExpenses.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				/*if(tableExpenses.getRowCount()>0) {
					int row = tableExpenses.getSelectedRow();
					Object txt = tableModel.getValueAt(row, 0);
					
					//textSearch.setText(String.valueOf(txt));
				}
				*/
			}
		});
		tableExpenses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableExpenses.setFillsViewportHeight(true);
		scrollPane.setViewportView(tableExpenses);
		
		
		
		JPanel panelProductEdit = new JPanel();
		panelProductEdit.setVisible(false);
		GridBagConstraints gbc_panelProductEdit = new GridBagConstraints();
		gbc_panelProductEdit.gridheight = 2;
		gbc_panelProductEdit.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelProductEdit.gridwidth = 4;
		gbc_panelProductEdit.weightx = 100.0;
		gbc_panelProductEdit.insets = new Insets(0, 5, 5, 0);
		gbc_panelProductEdit.gridx = 0;
		gbc_panelProductEdit.gridy = 6;
		panelExpenses.add(panelProductEdit, gbc_panelProductEdit);
		GridBagLayout gbl_panelProductEdit = new GridBagLayout();
		gbl_panelProductEdit.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelProductEdit.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panelProductEdit.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelProductEdit.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panelProductEdit.setLayout(gbl_panelProductEdit);
		
		JLabel lblPanelEdycjiProduktu = new JLabel("Panel edycji produktu:");
		lblPanelEdycjiProduktu.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblPanelEdycjiProduktu = new GridBagConstraints();
		gbc_lblPanelEdycjiProduktu.anchor = GridBagConstraints.WEST;
		gbc_lblPanelEdycjiProduktu.insets = new Insets(0, 0, 5, 5);
		gbc_lblPanelEdycjiProduktu.gridx = 2;
		gbc_lblPanelEdycjiProduktu.gridy = 0;
		panelProductEdit.add(lblPanelEdycjiProduktu, gbc_lblPanelEdycjiProduktu);
		
		
		JLabel lblProductName = new JLabel("Nazwa produktu:");
		GridBagConstraints gbc_lblProductName = new GridBagConstraints();
		gbc_lblProductName.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblProductName.anchor = GridBagConstraints.EAST;
		gbc_lblProductName.insets = new Insets(0, 0, 5, 5);
		gbc_lblProductName.gridx = 2;
		gbc_lblProductName.gridy = 2;
		panelProductEdit.add(lblProductName, gbc_lblProductName);
		
		textPdoductName = new JTextField();
		GridBagConstraints gbc_textPdoductName = new GridBagConstraints();
		gbc_textPdoductName.insets = new Insets(0, 0, 5, 5);
		gbc_textPdoductName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPdoductName.gridx = 3;
		gbc_textPdoductName.gridy = 2;
		panelProductEdit.add(textPdoductName, gbc_textPdoductName);
		textPdoductName.setColumns(10);
		
		JLabel lblPrice = new JLabel("Cena: ");
		GridBagConstraints gbc_lblPrice = new GridBagConstraints();
		gbc_lblPrice.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPrice.anchor = GridBagConstraints.EAST;
		gbc_lblPrice.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrice.gridx = 5;
		gbc_lblPrice.gridy = 2;
		panelProductEdit.add(lblPrice, gbc_lblPrice);
		
		JSpinner spinnerPrice = new JSpinner();
		spinnerPrice.setModel(new SpinnerNumberModel(0.0, 0.0, 2.147483647E9, 0.1));
		GridBagConstraints gbc_spinnerPrice = new GridBagConstraints();
		gbc_spinnerPrice.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerPrice.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerPrice.gridx = 7;
		gbc_spinnerPrice.gridy = 2;
		panelProductEdit.add(spinnerPrice, gbc_spinnerPrice);
		
		JLabel lblPurchaseDate = new JLabel("Data zakupu:");
		GridBagConstraints gbc_lblPurchaseDate = new GridBagConstraints();
		gbc_lblPurchaseDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPurchaseDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblPurchaseDate.gridx = 9;
		gbc_lblPurchaseDate.gridy = 2;
		panelProductEdit.add(lblPurchaseDate, gbc_lblPurchaseDate);
		
		JSpinner spinnerDate = new JSpinner();
		spinnerDate.setModel(new SpinnerDateModel(new Date(1556316000000L), new Date(-305514000000L), null, Calendar.DAY_OF_MONTH));
		spinnerDate.setEditor(new JSpinner.DateEditor(spinnerDate, "dd/MM/yyyy"));
		GridBagConstraints gbc_spinnerDate = new GridBagConstraints();
		gbc_spinnerDate.weightx = 100.0;
		gbc_spinnerDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerDate.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDate.gridx = 10;
		gbc_spinnerDate.gridy = 2;
		panelProductEdit.add(spinnerDate, gbc_spinnerDate);
		
		JLabel lblShop = new JLabel("Sklep: ");
		GridBagConstraints gbc_lblShop = new GridBagConstraints();
		gbc_lblShop.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblShop.anchor = GridBagConstraints.EAST;
		gbc_lblShop.insets = new Insets(0, 0, 5, 5);
		gbc_lblShop.gridx = 2;
		gbc_lblShop.gridy = 3;
		panelProductEdit.add(lblShop, gbc_lblShop);
		
		textShop = new JTextField();
		GridBagConstraints gbc_textShop = new GridBagConstraints();
		gbc_textShop.insets = new Insets(0, 0, 5, 5);
		gbc_textShop.fill = GridBagConstraints.HORIZONTAL;
		gbc_textShop.gridx = 3;
		gbc_textShop.gridy = 3;
		panelProductEdit.add(textShop, gbc_textShop);
		textShop.setColumns(10);
		JLabel lblDescription = new JLabel("Opips produktu: ");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblDescription.anchor = GridBagConstraints.EAST;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 5;
		gbc_lblDescription.gridy = 3;
		panelProductEdit.add(lblDescription, gbc_lblDescription);
		
		textProductDescription = new JTextField();
		GridBagConstraints gbc_textProductDescription = new GridBagConstraints();
		gbc_textProductDescription.weightx = 100.0;
		gbc_textProductDescription.gridwidth = 4;
		gbc_textProductDescription.insets = new Insets(0, 0, 5, 5);
		gbc_textProductDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_textProductDescription.gridx = 7;
		gbc_textProductDescription.gridy = 3;
		panelProductEdit.add(textProductDescription, gbc_textProductDescription);
		textProductDescription.setColumns(10);
		
		JButton btnAddProduct = new JButton("Dodaj produkt");
		GridBagConstraints gbc_btnAddProduct = new GridBagConstraints();
		gbc_btnAddProduct.weightx = 100.0;
		gbc_btnAddProduct.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddProduct.gridwidth = 4;
		gbc_btnAddProduct.insets = new Insets(0, 0, 0, 5);
		gbc_btnAddProduct.gridx = 2;
		gbc_btnAddProduct.gridy = 4;
		panelProductEdit.add(btnAddProduct, gbc_btnAddProduct);
		
		JButton btnEditPanelClose = new JButton("Zakończ edycję");
		GridBagConstraints gbc_btnEditPanelClose = new GridBagConstraints();
		gbc_btnEditPanelClose.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEditPanelClose.gridwidth = 9;
		gbc_btnEditPanelClose.insets = new Insets(0, 0, 0, 5);
		gbc_btnEditPanelClose.gridx = 2;
		gbc_btnEditPanelClose.gridy = 5;
		panelProductEdit.add(btnEditPanelClose, gbc_btnEditPanelClose);
		
		btnEditPanelClose.addActionListener(e->{
			panelProductEdit.setVisible(false);
			panelSearchOptions.setVisible(true);
		});
		
		JButton btnRemoveProduct = new JButton("Usuń produkt");
		GridBagConstraints gbc_btnRemoveProduct = new GridBagConstraints();
		gbc_btnRemoveProduct.weightx = 100.0;
		gbc_btnRemoveProduct.gridwidth = 4;
		gbc_btnRemoveProduct.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemoveProduct.insets = new Insets(0, 0, 0, 5);
		gbc_btnRemoveProduct.gridx = 7;
		gbc_btnRemoveProduct.gridy = 4;
		panelProductEdit.add(btnRemoveProduct, gbc_btnRemoveProduct);
		
			btnAddProduct.addActionListener(e->{
				if(textShop.getText().isEmpty() || textPdoductName.getText().isEmpty()|| (Double)spinnerPrice.getValue()==0.0 
												|| textProductDescription.getText().isEmpty()  ) {
					JOptionPane.showMessageDialog(frame, "Jedna z wartośći jest niprawidłowa.", "Błąd przy dodawaniu produktu.", JOptionPane.ERROR_MESSAGE);
					
				}
				else {
					hibernateDao.addRow(textShop.getText(), textPdoductName.getText(), (Double)spinnerPrice.getValue(), 
														textProductDescription.getText(), (Date)spinnerDate.getValue());
					int result = hibernateDao.printRows();
					lblExpensesAllProductsValue.setText(String.valueOf(result));
					lblExpensesFindRowsValue.setText(String.valueOf(result));
				}
				
			});
			
			btnSearch.addActionListener(e->{	
				int resultCount =  hibernateDao.search(textSearch.getText()
						,(double)spinnerPriceFrom.getValue()
						,(double)spinnerPriceTo.getValue()
						,(Date)spinnerDateMin.getValue()
						,(Date)spinnerDateMax.getValue()
				);
				
				String summary = Double.toString(hibernateDao.getExpensesSummary());
				lblExpensesSummaryValue.setText(summary);
				frame.setTitle(hibernateDao.test());
			});
			
			btnRemoveProduct.addActionListener(e->{
				if(tableExpenses.getRowCount()>0 && tableExpenses.getSelectedRow()>=0) {
					int row = tableExpenses.getSelectedRow();
					long id = (long)tableModel.getValueAt(row, 0);
					hibernateDao.removeRow(id);
					lblExpensesAllProductsValue.setText(String.valueOf(hibernateDao.rowCountAll()));
					int resultCount =  hibernateDao.search(textSearch.getText()
							,(double)spinnerPriceFrom.getValue()
							,(double)spinnerPriceTo.getValue()
							,(Date)spinnerDateMin.getValue()
							,(Date)spinnerDateMax.getValue()
					);
					lblExpensesFindRowsValue.setText(String.valueOf(resultCount));
				}
			});
			
			btnEdit.addActionListener(e->{
				panelProductEdit.setVisible(true);
				panelSearchOptions.setVisible(false);;
			});
			
		JPanel panelLogin = new JPanel();
		tabbedPane.addTab("Logowanie", null, panelLogin, null);
		GridBagLayout gbl_panelLogin = new GridBagLayout();
		gbl_panelLogin.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panelLogin.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelLogin.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_panelLogin.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelLogin.setLayout(gbl_panelLogin);
		
		Component verticalGlue = Box.createVerticalGlue();
		GridBagConstraints gbc_verticalGlue = new GridBagConstraints();
		gbc_verticalGlue.weighty = 100.0;
		gbc_verticalGlue.insets = new Insets(0, 0, 5, 5);
		gbc_verticalGlue.gridx = 3;
		gbc_verticalGlue.gridy = 9;
		panelLogin.add(verticalGlue, gbc_verticalGlue);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		GridBagConstraints gbc_horizontalGlue = new GridBagConstraints();
		gbc_horizontalGlue.weightx = 100.0;
		gbc_horizontalGlue.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalGlue.gridx = 2;
		gbc_horizontalGlue.gridy = 10;
		panelLogin.add(horizontalGlue, gbc_horizontalGlue);
		
		JPanel panelInnerLogin = new JPanel();
		GridBagConstraints gbc_panelInnerLogin = new GridBagConstraints();
		gbc_panelInnerLogin.weightx = 100.0;
		gbc_panelInnerLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelInnerLogin.insets = new Insets(0, 0, 5, 5);
		gbc_panelInnerLogin.gridx = 3;
		gbc_panelInnerLogin.gridy = 10;
		panelLogin.add(panelInnerLogin, gbc_panelInnerLogin);
		GridBagLayout gbl_panelInnerLogin = new GridBagLayout();
		gbl_panelInnerLogin.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panelInnerLogin.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panelInnerLogin.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelInnerLogin.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelInnerLogin.setLayout(gbl_panelInnerLogin);
		
		JLabel lblUrl = new JLabel("Url:");
		GridBagConstraints gbc_lblUrl = new GridBagConstraints();
		gbc_lblUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblUrl.gridx = 1;
		gbc_lblUrl.gridy = 1;
		panelInnerLogin.add(lblUrl, gbc_lblUrl);
		
		textUrl = new JTextField();
		textUrl.setText("jdbc:mysql://localhost:3306/expenses?serverTimezone=UTC");
		GridBagConstraints gbc_textUrl = new GridBagConstraints();
		gbc_textUrl.ipady = 2;
		gbc_textUrl.ipadx = 2;
		gbc_textUrl.weightx = 100.0;
		gbc_textUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_textUrl.gridwidth = 5;
		gbc_textUrl.insets = new Insets(0, 0, 5, 0);
		gbc_textUrl.gridx = 2;
		gbc_textUrl.gridy = 1;
		panelInnerLogin.add(textUrl, gbc_textUrl);
		textUrl.setColumns(10);
		
		JLabel lblUser = new JLabel("User:");
		GridBagConstraints gbc_lblUser = new GridBagConstraints();
		gbc_lblUser.insets = new Insets(0, 0, 5, 5);
		gbc_lblUser.gridx = 1;
		gbc_lblUser.gridy = 2;
		panelInnerLogin.add(lblUser, gbc_lblUser);
		
		textUser = new JTextField();
		textUser.setText("krzysiek");
		GridBagConstraints gbc_textUser = new GridBagConstraints();
		gbc_textUser.ipady = 2;
		gbc_textUser.ipadx = 2;
		gbc_textUser.weightx = 100.0;
		gbc_textUser.fill = GridBagConstraints.HORIZONTAL;
		gbc_textUser.gridwidth = 5;
		gbc_textUser.insets = new Insets(0, 0, 5, 0);
		gbc_textUser.gridx = 2;
		gbc_textUser.gridy = 2;
		panelInnerLogin.add(textUser, gbc_textUser);
		textUser.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 1;
		gbc_lblPassword.gridy = 3;
		panelInnerLogin.add(lblPassword, gbc_lblPassword);
		
		textPassword = new JTextField();
		textPassword.setText("open1qw23er4");
		GridBagConstraints gbc_textPassword = new GridBagConstraints();
		gbc_textPassword.ipadx = 2;
		gbc_textPassword.ipady = 2;
		gbc_textPassword.weightx = 100.0;
		gbc_textPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPassword.gridwidth = 5;
		gbc_textPassword.insets = new Insets(0, 0, 5, 0);
		gbc_textPassword.gridx = 2;
		gbc_textPassword.gridy = 3;
		panelInnerLogin.add(textPassword, gbc_textPassword);
		textPassword.setColumns(10);
		
		JButton btnLogin = new JButton("Logowanie");
		btnLogin.addActionListener(e -> {
			String info = hibernateDao.login(textUser.getText(), textPassword.getText(), textUrl.getText());
			frame.setTitle(info);
			
		});
		
		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.gridwidth = 5;
		gbc_btnLogin.gridx = 1;
		gbc_btnLogin.gridy = 4;
		panelInnerLogin.add(btnLogin, gbc_btnLogin);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		GridBagConstraints gbc_horizontalGlue_1 = new GridBagConstraints();
		gbc_horizontalGlue_1.weightx = 100.0;
		gbc_horizontalGlue_1.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalGlue_1.gridx = 4;
		gbc_horizontalGlue_1.gridy = 10;
		panelLogin.add(horizontalGlue_1, gbc_horizontalGlue_1);
		
		Component verticalGlue_1 = Box.createVerticalGlue();
		GridBagConstraints gbc_verticalGlue_1 = new GridBagConstraints();
		gbc_verticalGlue_1.weighty = 100.0;
		gbc_verticalGlue_1.insets = new Insets(0, 0, 5, 5);
		gbc_verticalGlue_1.gridx = 3;
		gbc_verticalGlue_1.gridy = 11;
		panelLogin.add(verticalGlue_1, gbc_verticalGlue_1);
			
		
		
		
	}

}
