package com.myproject.manager;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.queries.function.valuesource.IfFunction;
import org.hibernate.search.engine.integration.impl.SearchIntegration;
import org.hibernate.type.YesNoType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.myproject.manager.api.DaoApp;
import com.myproject.manager.configuration.ManagerConfiguration;
import com.myproject.manager.dao.HibernateDao;
import com.myproject.manager.pojo.Product;

import javax.naming.directory.SearchResult;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class AppWindow {
	private final ApplicationContext ctx = new AnnotationConfigApplicationContext(ManagerConfiguration.class);
	private final DefaultTableModel dataBaseTableModel  = ctx.getBean("dataBaseTableModel",DefaultTableModel.class);
	private final DefaultTableModel infoTableModel = ctx.getBean("infoTableModel", DefaultTableModel.class);
	private final DaoApp hibernateDao = ctx.getBean(HibernateDao.class);
	
	private JFrame frame;
	private JTable tableExpenses;
	private JTextField textProductName;
	private JTextField textProductDescription;
	private JTextField textShop;
	private JTextField textSearch;
	
	private boolean editModeOn;
	private long selectedId;
	private String selectedProductName;
	private String selectedProductDescription;
	private String selectedShop;
	private double selectedPrice;
	private Date selectedDate;
	private JTable tableInfo;
	
	

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
		 	
	
	}
	
	/*
	 *wypelnia model danymi z wynikow wyszukiwamnia bazy danych 
	 */
	private  void  setDataBaseTableModelFromQueryResult(ListIterator<?> listIterator) {
		//wyczysc model
		while(dataBaseTableModel.getRowCount()!=0) {
			dataBaseTableModel.removeRow(dataBaseTableModel.getRowCount()-1);
		}
				
		//wypelnij model danymi
		while(listIterator.hasNext()) {
			Product product = (Product)listIterator.next();
			dataBaseTableModel.addRow(new Object[] {
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getDescription(),
				product.getPurchaseDate(),
				product.getShop().getName()
			});
				
		}
		
	}
	/*
	 * oblicza sume znalezionych produktow
	 */
	private double searchPriceSum(ListIterator<?> searchResults) {
		double sum = 0;
		
		Map<String, Double> priceSum = new TreeMap<>();
		@SuppressWarnings("unchecked")
		ListIterator<Product> iter  = (ListIterator<Product>) searchResults;
		iter.forEachRemaining(product ->{
			double price = ((Product)product).getPrice();
			priceSum.merge("SearchSummaryPrices",price ,Double::sum);
		});
		
		if(priceSum.get("SearchSummaryPrices")!=null) {
			sum = priceSum.get("SearchSummaryPrices");
			DecimalFormat priceFormat = new DecimalFormat();
			priceFormat.setMaximumFractionDigits(2);
			sum = Double.valueOf(priceFormat.format(sum).replace(",", "."));
		}
		
		return sum;
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
		gbc_panelSearchOptions.gridwidth = 2;
		gbc_panelSearchOptions.gridheight = 3;
		gbc_panelSearchOptions.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelSearchOptions.insets = new Insets(5, 5, 5, 5);
		gbc_panelSearchOptions.gridx = 1;
		gbc_panelSearchOptions.gridy = 0;
		panelExpenses.add(panelSearchOptions, gbc_panelSearchOptions);
		GridBagLayout gbl_panelSearchOptions = new GridBagLayout();
		gbl_panelSearchOptions.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelSearchOptions.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panelSearchOptions.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelSearchOptions.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelSearchOptions.setLayout(gbl_panelSearchOptions);
		
		
		
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
		gbc_panelMenu.weightx = 50.0;
		gbc_panelMenu.fill = GridBagConstraints.VERTICAL;
		gbc_panelMenu.gridheight = 5;
		gbc_panelMenu.gridx = 6;
		gbc_panelMenu.gridy = 0;
		panelSearchOptions.add(panelMenu, gbc_panelMenu);
		GridBagLayout gbl_panelMenu = new GridBagLayout();
		gbl_panelMenu.columnWidths = new int[]{0, 0, 0};
		gbl_panelMenu.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panelMenu.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panelMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelMenu.setLayout(gbl_panelMenu);
		
		JButton btnAdd = new JButton("Dodaj");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 0;
		panelMenu.add(btnAdd, gbc_btnAdd);
		
		JButton btnDelete = new JButton("Usuń");
		
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnDelete.gridx = 0;
		gbc_btnDelete.gridy = 1;
		panelMenu.add(btnDelete, gbc_btnDelete);
		
		JButton btnUpdate = new JButton("Edytuj");
		
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnUpdate.insets = new Insets(0, 0, 5, 5);
		gbc_btnUpdate.gridx = 0;
		gbc_btnUpdate.gridy = 2;
		panelMenu.add(btnUpdate, gbc_btnUpdate);
		
		
		
		JButton btnDefaultFilters = new JButton("Wyczyść filtry");
		
		GridBagConstraints gbc_btnDefaultFilters = new GridBagConstraints();
		gbc_btnDefaultFilters.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDefaultFilters.insets = new Insets(0, 0, 5, 5);
		gbc_btnDefaultFilters.gridx = 0;
		gbc_btnDefaultFilters.gridy = 3;
		panelMenu.add(btnDefaultFilters, gbc_btnDefaultFilters);
		
		JLabel lblOptionDateMin = new JLabel("Data zakupu od:");
		lblOptionDateMin.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblOptionDateMin = new GridBagConstraints();
		gbc_lblOptionDateMin.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptionDateMin.gridx = 1;
		gbc_lblOptionDateMin.gridy = 2;
		panelSearchOptions.add(lblOptionDateMin, gbc_lblOptionDateMin);
		
		JSpinner spinnerDateMin = new JSpinner();
		spinnerDateMin.setModel(new SpinnerDateModel(new Date(662688000000L), new Date(631152000000L), null, Calendar.DAY_OF_YEAR));
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
		spinnerDateMax.setModel(new SpinnerDateModel(new Date(1563450994542L), new Date(631152000000L), null, Calendar.DAY_OF_YEAR));
		spinnerDateMax.setEditor(new JSpinner.DateEditor(spinnerDateMax, "dd/MM/yyyy"));
		spinnerDateMax.setValue(new Date());
		GridBagConstraints gbc_spinnerDateMax = new GridBagConstraints();
		gbc_spinnerDateMax.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerDateMax.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDateMax.gridx = 5;
		gbc_spinnerDateMax.gridy = 2;
		panelSearchOptions.add(spinnerDateMax, gbc_spinnerDateMax);
		
		JButton btnSearch = new JButton("Wyszukaj");
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.gridwidth = 5;
		gbc_btnSearch.fill = GridBagConstraints.BOTH;
		gbc_btnSearch.insets = new Insets(0, 0, 5, 5);
		gbc_btnSearch.gridx = 1;
		gbc_btnSearch.gridy = 3;
		panelSearchOptions.add(btnSearch, gbc_btnSearch);
		
		
		
		JPanel panelSummary = new JPanel();
		GridBagConstraints gbc_panelSummary = new GridBagConstraints();
		gbc_panelSummary.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelSummary.gridwidth = 5;
		gbc_panelSummary.insets = new Insets(5, 5, 5, 5);
		gbc_panelSummary.gridx = 1;
		gbc_panelSummary.gridy = 4;
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
		
		JLabel lblExpensesSummaryValue = new JLabel("***");
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
		//lblExpensesFindRowsValue.setText(String.valueOf(hibernateDao.showRows()));
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
		//lblExpensesAllProductsValue.setText(String.valueOf(hibernateDao.rowsCount()));
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
		gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		panelExpenses.add(scrollPane, gbc_scrollPane);
		
		tableExpenses = new JTable();	
		tableExpenses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableExpenses.setFillsViewportHeight(true);
		tableExpenses.setModel(dataBaseTableModel);
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
		gbl_panelProductEdit.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelProductEdit.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelProductEdit.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		gbc_lblProductName.insets = new Insets(0, 0, 5, 5);
		gbc_lblProductName.gridx = 2;
		gbc_lblProductName.gridy = 2;
		panelProductEdit.add(lblProductName, gbc_lblProductName);
		
		textProductName = new JTextField();
		GridBagConstraints gbc_textProductName = new GridBagConstraints();
		gbc_textProductName.weightx = 100.0;
		gbc_textProductName.gridwidth = 2;
		gbc_textProductName.insets = new Insets(0, 0, 5, 5);
		gbc_textProductName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textProductName.gridx = 3;
		gbc_textProductName.gridy = 2;
		panelProductEdit.add(textProductName, gbc_textProductName);
		textProductName.setColumns(10);
		
		JLabel lblPrice = new JLabel("Cena: ");
		GridBagConstraints gbc_lblPrice = new GridBagConstraints();
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
		gbc_lblPurchaseDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblPurchaseDate.gridx = 9;
		gbc_lblPurchaseDate.gridy = 2;
		panelProductEdit.add(lblPurchaseDate, gbc_lblPurchaseDate);
		
		JSpinner spinnerDate = new JSpinner();
		
		spinnerDate.setModel(new SpinnerDateModel(new Date(1563449666292L), new Date(10029600000L), null, Calendar.DAY_OF_YEAR));
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
		gbc_lblShop.insets = new Insets(0, 0, 5, 5);
		gbc_lblShop.gridx = 2;
		gbc_lblShop.gridy = 3;
		panelProductEdit.add(lblShop, gbc_lblShop);
		
		textShop = new JTextField();
		GridBagConstraints gbc_textShop = new GridBagConstraints();
		gbc_textShop.weightx = 100.0;
		gbc_textShop.gridwidth = 2;
		gbc_textShop.insets = new Insets(0, 0, 5, 5);
		gbc_textShop.fill = GridBagConstraints.HORIZONTAL;
		gbc_textShop.gridx = 3;
		gbc_textShop.gridy = 3;
		panelProductEdit.add(textShop, gbc_textShop);
		textShop.setColumns(10);
		JLabel lblDescription = new JLabel("Opis produktu: ");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
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
		gbc_btnAddProduct.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddProduct.weightx = 100.0;
		gbc_btnAddProduct.gridwidth = 9;
		gbc_btnAddProduct.insets = new Insets(2, 2, 2, 5);
		gbc_btnAddProduct.gridx = 2;
		gbc_btnAddProduct.gridy = 4;
		panelProductEdit.add(btnAddProduct, gbc_btnAddProduct);
		
		JButton btnUpdateProduct = new JButton("Zapisz zmiany");
		
		GridBagConstraints gbc_btnUpdateProduct = new GridBagConstraints();
		gbc_btnUpdateProduct.gridwidth = 9;
		gbc_btnUpdateProduct.weightx = 100.0;
		gbc_btnUpdateProduct.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnUpdateProduct.insets = new Insets(2, 2, 2, 5);
		gbc_btnUpdateProduct.gridx = 2;
		gbc_btnUpdateProduct.gridy = 5;
		panelProductEdit.add(btnUpdateProduct, gbc_btnUpdateProduct);
		btnUpdateProduct.setVisible(false);
		
		JButton btnDeleteProduct = new JButton("Usuń produkt");
		GridBagConstraints gbc_btnDeleteProduct = new GridBagConstraints();
		gbc_btnDeleteProduct.weightx = 100.0;
		gbc_btnDeleteProduct.gridwidth = 9;
		gbc_btnDeleteProduct.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDeleteProduct.insets = new Insets(2, 2, 2, 5);
		gbc_btnDeleteProduct.gridx = 2;
		gbc_btnDeleteProduct.gridy = 6;
		panelProductEdit.add(btnDeleteProduct, gbc_btnDeleteProduct);
		
		JButton btnEditPanelClose = new JButton("Zakończ edycję");
		GridBagConstraints gbc_btnEditPanelClose = new GridBagConstraints();
		gbc_btnEditPanelClose.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEditPanelClose.gridwidth = 9;
		gbc_btnEditPanelClose.insets = new Insets(2, 2, 2, 5);
		gbc_btnEditPanelClose.gridx = 2;
		gbc_btnEditPanelClose.gridy = 7;
		panelProductEdit.add(btnEditPanelClose, gbc_btnEditPanelClose);
		
		
			/*
		 	* klikniecie przycisku usuwanie produktu
		 	* najpierw sprawdza czy widok tabeli nie jest pusty i  
		 	* czy sa jakies zaznaczone wiersze 
		 	* wyswietla okno dialogowe z pytaniem o potwierdzenie usuniecia wiersza
		 	*/
			btnDeleteProduct.addActionListener(e->{
				if(tableExpenses.getRowCount()>0 && tableExpenses.getSelectedRow()>=0) {
					int confirm = JOptionPane.showConfirmDialog(frame, "Czy na pewno chcesz usunąć zaznaczony wiersz?\nUsunięte dane zostaną utracone!"
											, "Usuwanie zaznaczonego wiersza", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(confirm == JOptionPane.YES_OPTION) {
						int row = tableExpenses.getSelectedRow();
						long id = (long)dataBaseTableModel.getValueAt(row, 0);
						
						List<?> removeResult =  hibernateDao.removeRow(id);
						int allRows = 0;
						if(removeResult != null) {
							allRows = removeResult.size();
						}
						
						List<?> searchResult =  hibernateDao.search(textSearch.getText()
								,(double)spinnerPriceFrom.getValue()
								,(double)spinnerPriceTo.getValue()
								,(Date)spinnerDateMin.getValue()
								,(Date)spinnerDateMax.getValue()
							);
						int searchResultCount = 0;
						if(searchResult != null) {
							searchResultCount = searchResult.size();
							ListIterator<?> iter = searchResult.listIterator();
							setDataBaseTableModelFromQueryResult(iter);
						}
						
						
						lblExpensesFindRowsValue.setText(String.valueOf(searchResultCount));
						lblExpensesAllProductsValue.setText(String.valueOf(allRows));
					}
					
				}
			});
		
			/*
			 * klikniecie przycisku zakonczenia edycji
			 * ukrywa panel edycji 
			 * pokazuje ustawia panel wyszukiwania 
			 * ukrywa przyciski dodawania produktu ,usuwania i zapisywania zmian
			 * wylacza tryb edycji
			 * czysci wartosci pol edytora
			 * pokazuje wszystkie pola i etykiety panelu edycji(sa ukrywane przy kliknieciu przycisku usun)
			 */
			btnEditPanelClose.addActionListener(e->{
				panelProductEdit.setVisible(false);
				panelSearchOptions.setVisible(true);
				btnAddProduct.setVisible(false);
				btnDeleteProduct.setVisible(false);
				btnUpdateProduct.setVisible(false);
				
				editModeOn=false;
				
				textProductName.setText("");
				spinnerPrice.setValue(0.00);
				LocalDate localDate = LocalDate.now();
				Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
				spinnerDate.setValue(Date.from(instant));
				textShop.setText("");
				textProductDescription.setText("");
				
				textProductName.setVisible(true);
				textProductDescription.setVisible(true);
				textShop.setVisible(true);
				spinnerPrice.setVisible(true);
				spinnerDate.setVisible(true);
				lblProductName.setVisible(true);
				lblShop.setVisible(true);
				lblPrice.setVisible(true);
				lblDescription.setVisible(true);
				lblPurchaseDate.setVisible(true);
			});
			
			/*
			 * klikniecie przycisku wyszukiwania
			 * pokazuje sume cen znalezionych produktow
			 *  liczbe znalezionych wierszy
			 *  liczbe wszystkich wierszy w bazie
			 */
			btnSearch.addActionListener(e->{
				String summary="";
				Date tempDateMin = (Date)spinnerDateMin.getValue();
				tempDateMin.setTime(tempDateMin.getTime()+30000000);
				
				Date tempDateMax = (Date)spinnerDateMax.getValue();
				tempDateMax.setTime(tempDateMax.getTime()+30000000);
				
				List<?> resultList =  hibernateDao.search(textSearch.getText()
						,(double)spinnerPriceFrom.getValue()
						,(double)spinnerPriceTo.getValue()
						,(Date)spinnerDateMin.getValue()
						,(Date)spinnerDateMax.getValue()
						);
				int result = 0;
				if(resultList != null) {
					ListIterator<?> iter = resultList.listIterator();
					ListIterator<?> iter2 = resultList.listIterator();
					result = resultList.size();
					setDataBaseTableModelFromQueryResult(iter);
					summary = String.valueOf(searchPriceSum(iter2));
				}
				
				
				lblExpensesSummaryValue.setText(summary);
				lblExpensesFindRowsValue.setText(String.valueOf(result));
				
				List<?> allRowsList = hibernateDao.getAllRows();
				int allRows = 0;
				if(allRowsList != null) {
					allRows = allRowsList.size();
				}
				lblExpensesAllProductsValue.setText(String.valueOf(allRows));
				
			});
			
			/*
			 * klikniecie przycisku dodawania produktu
			 * sprawdza poprawnosc wartosci w panelu wdycji
			 * formatuje cene do 2 miejsc po przecinku
			 * dodaje produkt do bazy
			 * wyswietla wszystkie wiersze zaktualizowanej bazy
			 * pokazuje sume cen  produktow
			 *   liczbe znalezionych wierszy
			 *   liczbe wszystkich wierszy w bazie
			 * czysci pole z nazwa produktu,pozostawia pozostałe pola z ostatnimi wartosciami
			 *  
			 */
			btnAddProduct.addActionListener(e->{
				if(textShop.getText().isEmpty() || textProductName.getText().isEmpty() || (Double)spinnerPrice.getValue()==0.0 
												|| textProductDescription.getText().isEmpty()  ) {
					JOptionPane.showMessageDialog(frame, "Jedna z wartośći jest nieprawidłowa.", "Błąd przy dodawaniu produktu.", JOptionPane.ERROR_MESSAGE);
					
				}
				else {
					//formatowanie moze zaokraglac = niedokladny wynik
					Double price = (Double)spinnerPrice.getValue();
					DecimalFormat df = new DecimalFormat();
					//df.setMaximumFractionDigits(2);	
					price = Double.valueOf(df.format(price).replace(",", "."));
					
					
					List<?> resultList =  hibernateDao.addRow(textShop.getText(), textProductName.getText(), price, 
														textProductDescription.getText(), (Date)spinnerDate.getValue());
					int result = 0;
					if(resultList!=null) {
						result = resultList.size();
						ListIterator<?> iter = resultList.listIterator();
						setDataBaseTableModelFromQueryResult(iter);
					}
					
					lblExpensesAllProductsValue.setText(String.valueOf(result));
					lblExpensesFindRowsValue.setText(String.valueOf(result));
					textProductName.setText("");
					
				}
				btnSearch.doClick();
			});
			
			/*
			 * klikniecie przycisku dodawania
			 * ukrywa panel wyszukiwania
			 * pokazuje panel edycji
			 * pokazuje przycisk dodawanie produktu w panelu edycji
			 */
			btnAdd.addActionListener(e->{
				panelSearchOptions.setVisible(false);
				panelProductEdit.setVisible(true);
				btnAddProduct.setVisible(true);
			});
			
			/*
			 * klikniecie przycisku usun
			 * ukrywa panel wyszukiwania
			 * pokazuje panel edycji
			 * pokazuje przycisk usuwania
			 * ukrywa wszystkie pola i etykiety panelu edycji
			 */
			btnDelete.addActionListener(e->{
				panelSearchOptions.setVisible(false);
				panelProductEdit.setVisible(true);
				btnDeleteProduct.setVisible(true);
				
				textProductName.setVisible(false);
				textProductDescription.setVisible(false);
				textShop.setVisible(false);
				spinnerPrice.setVisible(false);
				spinnerDate.setVisible(false);
				lblProductName.setVisible(false);
				lblShop.setVisible(false);
				lblPrice.setVisible(false);
				lblDescription.setVisible(false);
				lblPurchaseDate.setVisible(false);
				
			});
			
			
			
			/*
			 * klikniecie przycisku ustwania domyslnego filtru
			 * ustawia pola wyszukiwania na domyslne wartosci
			 * uzywa klasy Instant do przekazywania daty pomiedzy LocalDate i Date
			 */
			btnDefaultFilters.addActionListener(e->{
				textSearch.setText("");
				spinnerPriceFrom.setValue(0.00);
				spinnerPriceTo.setValue(1000.00);
				
				LocalDate localDate = LocalDate.of(1990, 01, 01);
				Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
				spinnerDateMin.setValue(Date.from(instant));
				
				localDate = LocalDate.now();
				instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
				spinnerDateMax.setValue(Date.from(instant));
				
			});
			
			/*
			 * klikniecie przycisku edycji
			 * wlacza tryb edycji(zmienna uzywana do wczytania wartosci ostatnio kliknietego wiersza do panelu edycji )
			 * ukrywa panel wyszukiwania
			 * pokazuje panel edycji
			 * pokazuje przycisk zapisu zamin po edycji
			 * zapamietuje wartosci wybranego wiersza(zmienne selected... sa uzywane sprawdzeniu czy dane po edycji ulegly zmianie)
			 * wczytuje do pol edytora wartosci wybranego  wiersza(jezeli jakis jest wybrany)
			 */
			btnUpdate.addActionListener(e->{
				editModeOn=true;
				panelSearchOptions.setVisible(false);
				panelProductEdit.setVisible(true);
				btnUpdateProduct.setVisible(true);
				
					if(tableExpenses.getRowCount()>0 && tableExpenses.getSelectedRow()>=0) {
						int row = tableExpenses.getSelectedRow();
						selectedId = (long)dataBaseTableModel.getValueAt(row, 0);
						selectedProductName = (String)dataBaseTableModel.getValueAt(row, 1);
						selectedPrice = (double)dataBaseTableModel.getValueAt(row, 2);
						selectedProductDescription = (String)dataBaseTableModel.getValueAt(row, 3);
						selectedDate = (Date)dataBaseTableModel.getValueAt(row, 4);
						selectedShop = (String)dataBaseTableModel.getValueAt(row, 5);
						
						textProductName.setText(selectedProductName);
						spinnerPrice.setValue(selectedPrice);
						textProductDescription.setText(selectedProductDescription);
						spinnerDate.setValue(selectedDate);
						textShop.setText(selectedShop);			
						
								
					}
					
				
				
			});
			
			/*
			 * klikniecie przycisku zapisywania(aktualizacji) danych po edycji
			 * sprawdza czy jest wybrany jakis wiersz 
			 * sprawdza czy zostaly dokonane zmiany wartosci edytowanych pol
			 * (zmienione wartosci sa  aktualizowane - pola sa sprawdzane i aktualizowane oddzielnie)
			 * wyswietla wszystie wiersze bazy
			 */
			btnUpdateProduct.addActionListener(e->{
				if(tableExpenses.getRowCount()>0 && tableExpenses.getSelectedRow()>=0) {
					//int row = tableExpenses.getSelectedRow();
					if(!textProductName.getText().isEmpty() && !textProductDescription.getText().isEmpty() && 
							!textShop.getText().isEmpty() && (double)spinnerPrice.getValue() != 0) {
						
						if(!textProductName.getText().equals(selectedProductName)) {
							hibernateDao.updateRow("Product", "name", textProductName.getText(), "idproduct", selectedId);
						}
						
						if(!textProductDescription.getText().equals(selectedProductDescription)) {
							hibernateDao.updateRow("Product", "description", textProductDescription.getText(), "idproduct", selectedId);
						}
						
						//do sprawdzenia
						if(!textShop.getText().equals(selectedShop)) {
							hibernateDao.updateRow("Product", "shop.nameShop", textShop.getText(), "idproduct", selectedId);
						}
						
						//do sprawadzenia
						if(!spinnerPrice.getValue().equals(selectedPrice) ) {
							hibernateDao.updateRow("Product", "price", spinnerPrice.getValue(), "idproduct", selectedId);
						}
						
						//do sprawdzenia
						if(!spinnerDate.getValue().equals(selectedDate)) {
							
							Date temp = (Date)spinnerDate.getValue();
							temp.setTime(temp.getTime()+30000000);
							frame.setTitle(temp.toString());
							hibernateDao.updateRow("Product", "purchaseDate", temp, "idproduct", selectedId);
						}
					}
					else {
						JOptionPane.showMessageDialog(frame, "Nie można zapisać zmian!", "Błąd edycji", JOptionPane.ERROR_MESSAGE);
					}
					
					List<?> resultList =  hibernateDao.getAllRows();
					if(resultList != null) {
						ListIterator<?> iter = resultList.listIterator();
						setDataBaseTableModelFromQueryResult(iter);
					}
					//poprawic wyswietlania daty i wybieranie wiersza po edycji
				}
			});
			
			/*
			 * Zapamietuje wartosci kliknietego wiersza
			 * przy wlaczonoym trybie edycji  klikniecie wiersza wpisuje jego wartosci do  panelu edycji
			 */
			tableExpenses.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(tableExpenses.getRowCount()>0 && tableExpenses.getSelectedRow()>=0) {
						int row = tableExpenses.getSelectedRow();
						selectedId = (long)dataBaseTableModel.getValueAt(row, 0);
						selectedProductName = (String)dataBaseTableModel.getValueAt(row, 1);
						selectedPrice = (double)dataBaseTableModel.getValueAt(row, 2);
						selectedProductDescription = (String)dataBaseTableModel.getValueAt(row, 3);
						selectedDate = (Date)dataBaseTableModel.getValueAt(row, 4);
						selectedShop = (String)dataBaseTableModel.getValueAt(row, 5);
						
						if(editModeOn==true) {
							textProductName.setText(selectedProductName);
							spinnerPrice.setValue(selectedPrice);
							textProductDescription.setText(selectedProductDescription);
							spinnerDate.setValue(selectedDate);
							textShop.setText(selectedShop);
							
						}
						
						
						
					}
					
				}
			});
			
		btnAddProduct.setVisible(false);
		btnUpdateProduct.setVisible(false);
		btnDeleteProduct.setVisible(false);
		JPanel panelInfo = new JPanel();
		tabbedPane.addTab("Podsumowanie", null, panelInfo, null);
		GridBagLayout gbl_panelInfo = new GridBagLayout();
		gbl_panelInfo.columnWidths = new int[]{0, 0};
		gbl_panelInfo.rowHeights = new int[]{0, 0};
		gbl_panelInfo.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelInfo.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelInfo.setLayout(gbl_panelInfo);
		
		JScrollPane scrollPaneInfo = new JScrollPane();
		GridBagConstraints gbc_scrollPaneInfo = new GridBagConstraints();
		gbc_scrollPaneInfo.weighty = 100.0;
		gbc_scrollPaneInfo.weightx = 100.0;
		gbc_scrollPaneInfo.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneInfo.gridx = 0;
		gbc_scrollPaneInfo.gridy = 0;
		panelInfo.add(scrollPaneInfo, gbc_scrollPaneInfo);
		
		tableInfo = new JTable();
		tableInfo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableInfo.setFillsViewportHeight(true);
		tableInfo.setModel(infoTableModel);
		scrollPaneInfo.setViewportView(tableInfo);
		
		/*
		 * klikniecie zakladki panelu
		 * sumuje ceny z miesiecy dla danego roku
		 * wypelnia model tabeli danymi
		 */
		tabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(tabbedPane.getSelectedIndex() == 1) {
					//czysci model podsumowania 
					while(infoTableModel.getRowCount()!=0) {
						infoTableModel.removeRow(infoTableModel.getRowCount()-1);
						
					}
					//tworzy mape na podstawie wynikow wyszukiwania
					List<?> allRows = hibernateDao.getAllRows();
					Map<String, Double> prices = new TreeMap<>();
					ListIterator<?> iter =  allRows.listIterator();
					iter.forEachRemaining(listItem ->{ 
						//System.out.print("\nData zakupu: "+((Product)listItem).getPurchaseDate());
						Product product = (Product)listItem;
						double price = product.getPrice();
						Date purchaseDate = product.getPurchaseDate();
						String key = DateTools.dateToString(purchaseDate, DateTools.Resolution.MONTH);
						prices.merge(key, price, Double::sum);
					});
					//wypelnia model tabeli podsumowania danymi z mapy 
					prices.forEach((dateKey,priceSum)->{ try {
						boolean yearIdExist = false;
						Date tempDate = DateTools.stringToDate(dateKey);
						Instant instant = tempDate.toInstant();
						LocalDate localdate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
						int month = localdate.getMonthValue();
						//System.out.print("\nkey: "+k+" value: "+v+" --- Data: "+tempDate);
						Object[] row = new Object[14];
						row[0] = dateKey.substring(0, 4);
						DecimalFormat priceFormat = new DecimalFormat();
						priceFormat.setMaximumFractionDigits(2);
						row[month] = Double.valueOf(priceFormat.format(priceSum).replace(",","."));
						//sprawdza czy ostatnia kolumna wiersza nie jest null
						if(row[13]==null) {
							row[13] = 0.00;
						}
						//pobiera wartosc z kolumny podsumowania i zwieksza o wartosc z mapy(sume cen z danego miesiaca)
						double summary = (double)row[13];
						row[13] = summary + priceSum;
						//sprawdza czy w modelu istnieje dany rok i aktualizuje lub dodaje  wiersz(pobiera dane z mapy)
						for(int i=0;i<infoTableModel.getRowCount();i++) {
							String yearModel = (String) infoTableModel.getValueAt(i, 0);
							String yearMap = (String) row[0];
							if(yearModel.equals(yearMap)) {
								infoTableModel.setValueAt(row[month], i, month);
								//sumuje  ceny ze wszystkich miesiecy zapisuje w ostatniej kolumnie wiersza w modelku
								for(int j =1;j<13;j++) {
									if(infoTableModel.getValueAt(i, j)!=null) {
										summary += (double)infoTableModel.getValueAt(i, j);
									}	
								}
								infoTableModel.setValueAt(summary,i, 13);
								yearIdExist = true;
								break;
							}
						}
						if(!yearIdExist) {
							infoTableModel.addRow(row);
						}
						
						
						
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}});
					//JOptionPane.showMessageDialog(frame,"wykonano podsumowanie", "podsumowanie", JOptionPane.ERROR_MESSAGE);
					
				}
				
				
			}
		});
		
		
		
		
		
			
		
		
		
	}

}
