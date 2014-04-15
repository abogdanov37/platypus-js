package com.eas.client.form.published.widgets.model;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.bearsoft.gwt.ui.widgets.grid.builders.ThemedHeaderOrFooterBuilder;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderSplitter;
import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
import com.bearsoft.gwt.ui.widgets.grid.processing.ListMultiSortHandler;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeDataProvider;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeDataProvider.ExpandedCollapsedHandler;
import com.bearsoft.gwt.ui.widgets.grid.processing.TreeMultiSortHandler;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.events.RowsetEvent;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.CrossUpdater;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.grid.FindWindow;
import com.eas.client.form.grid.GridCrossUpdaterAction;
import com.eas.client.form.grid.RowsetPositionSelectionHandler;
import com.eas.client.form.grid.cells.rowmarker.RowMarkerCell;
import com.eas.client.form.grid.columns.CheckServiceColumn;
import com.eas.client.form.grid.columns.ModelGridColumn;
import com.eas.client.form.grid.columns.ModelGridColumnFacade;
import com.eas.client.form.grid.columns.RadioServiceColumn;
import com.eas.client.form.grid.rows.RowChildrenFetcher;
import com.eas.client.form.grid.rows.RowsetDataProvider;
import com.eas.client.form.grid.rows.RowsetTree;
import com.eas.client.form.grid.selection.MultiRowSelectionModel;
import com.eas.client.form.grid.selection.SingleRowSelectionModel;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasOnRender;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.view.client.SelectionModel;

/**
 * Class intended to wrap a grid or tree grid. It also contains grid API.
 * 
 * @author mg
 * 
 */
public class ModelGrid extends Grid<Row> implements HasJsFacade, HasOnRender, HasComponentPopupMenu, HasEventsExecutor {

	public static final int ROWS_HEADER_TYPE_NONE = 0;
	public static final int ROWS_HEADER_TYPE_USUAL = 1;
	public static final int ROWS_HEADER_TYPE_CHECKBOX = 2;
	public static final int ROWS_HEADER_TYPE_RADIOBUTTON = 3;
	//
	public static final int ONE_FIELD_ONE_QUERY_TREE_KIND = 1;
	public static final int FIELD_2_PARAMETER_TREE_KIND = 2;
	public static final int SCRIPT_PARAMETERS_TREE_KIND = 3;
	//
	
	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	//
	protected int treeKind = ONE_FIELD_ONE_QUERY_TREE_KIND;
	protected ModelElementRef unaryLinkField;
	protected ModelElementRef param2GetChildren;
	protected ModelElementRef paramSourceField;
	//
	protected Entity rowsSource;
	protected JavaScriptObject onRender;
	protected PublishedComponent published;
	protected Callback<RowsetEvent, RowsetEvent> crossUpdaterAction;
	protected CrossUpdater crossUpdater;
	protected FindWindow finder;
	protected String groupName = "group-name-" + Document.get().createUniqueId();
	protected int rowsHeaderType = -1;
	protected List<HeaderNode> header = new ArrayList<>();
	// runtime
	protected ListHandler<Row> sortHandler;
	protected HandlerRegistration sortHandlerReg;
	protected HandlerRegistration positionSelectionHandler;
	protected boolean editable;
	protected boolean deletable;
	protected boolean insertable;

	public ModelGrid() {
		super(new RowKeyProvider());
		finder = new FindWindow(this);
		crossUpdaterAction = new GridCrossUpdaterAction(this);
		crossUpdater = new CrossUpdater(crossUpdaterAction);
	}

	public ModelElementRef getUnaryLinkField() {
		return unaryLinkField;
	}

	public void setUnaryLinkField(ModelElementRef aValue) {
		unaryLinkField = aValue;
	}

	public ModelElementRef getParam2GetChildren() {
		return param2GetChildren;
	}

	public void setParam2GetChildren(ModelElementRef aValue) {
		param2GetChildren = aValue;
	}

	public ModelElementRef getParamSourceField() {
		return paramSourceField;
	}

	public void setParamSourceField(ModelElementRef aValue) {
		paramSourceField = aValue;
	}

	public int getTreeKind() {
		return treeKind;
	}

	public void setTreeKind(int aValue) {
		treeKind = aValue;
	}

	public ListHandler<Row> getSortHandler() {
		return sortHandler;
	}

	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
	}

	@Override
	public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu;
	}

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {

					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
			}
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public List<HeaderNode> getHeader() {
		return header;
	}

	public void setHeader(List<HeaderNode> aHeader) {
		if (header != aHeader) {
			header = aHeader;
			applyHeader();
		}
	}

	/*
	 * Indicates that subsequent changes will take no effect in general columns
	 * collection and header. They will affect only underlying grid sections
	 */
	protected boolean columnsAjusting;

	public boolean isColumnsAjusting() {
		return columnsAjusting;
	}

	public void setColumnsAjusting(boolean aValue) {
		columnsAjusting = aValue;
	}

	@Override
	protected void refreshColumns() {
		columnsAjusting = true;
		try {
			super.refreshColumns();
		} finally {
			columnsAjusting = false;
		}
		applyHeader();
	}

	public int getRowsHeaderType() {
		return rowsHeaderType;
	}

	public void setRowsHeaderType(int aValue) {
		if (rowsHeaderType != aValue) {
			if (rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX || rowsHeaderType == ROWS_HEADER_TYPE_RADIOBUTTON || rowsHeaderType == ROWS_HEADER_TYPE_USUAL) {
				header.remove(0);
				super.removeColumn(0);
			}
			boolean needRefreshColumns = getDataColumnCount() > 0;
			rowsHeaderType = aValue;
			SelectionModel<Row> sm;
			if (rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX) {
				sm = new MultiRowSelectionModel(this);
				Header<String> colHeader = new TextHeader(" ");
				super.addColumn(true, 0, new CheckServiceColumn(sm), "20px", colHeader, null, false);
				header.add(0, new HeaderNode(colHeader));
			} else if (rowsHeaderType == ROWS_HEADER_TYPE_RADIOBUTTON) {
				sm = new SingleRowSelectionModel(this);
				Header<String> colHeader = new TextHeader(" ");
				super.addColumn(true, 0, new RadioServiceColumn(groupName, sm), "20px", colHeader, null, false);
				header.add(0, new HeaderNode(colHeader));
			} else if (rowsHeaderType == ROWS_HEADER_TYPE_USUAL) {
				sm = new MultiRowSelectionModel(this);
				Header<String> colHeader = new TextHeader(" ");
				IdentityColumn<Row> col = new IdentityColumn<>(new RowMarkerCell(rowsSource.getRowset()));
				super.addColumn(true, 0, col, "20px", colHeader, null, false);
				header.add(0, new HeaderNode(colHeader));
				/*
				 * Header<?> identityHeader = getColumnHeader(0);
				 * ((DraggableHeader<?>) identityHeader).setMoveable(false);
				 * ((DraggableHeader<?>) identityHeader).setResizable(false);
				 * setColumnWidth(col, 20, Style.Unit.PX);
				 */
			} else {
				sm = new MultiRowSelectionModel(this);
			}
			setSelectionModel(sm);
			if (needRefreshColumns) {
				refreshColumns();
				applyHeader();
			}
		}
	}

	private boolean isLazyTreeConfigured() {
		return param2GetChildren.isCorrect() && param2GetChildren.field != null && paramSourceField.isCorrect() && paramSourceField.field != null;
	}

	private boolean isTreeConfigured() throws Exception {
		return rowsSource != null && unaryLinkField.isCorrect() && unaryLinkField.field != null && (treeKind == ONE_FIELD_ONE_QUERY_TREE_KIND || treeKind == FIELD_2_PARAMETER_TREE_KIND);
	}

	@Override
	public void addColumn(boolean forceRefreshColumns, int aIndex, Column<Row, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		Column<Row, ?> shifted = aIndex >= 0 && aIndex < getDataColumnCount() ? getDataColumn(aIndex) : null;
		if (shifted != null && !(shifted instanceof ModelGridColumn<?>)) {
			// won't shift service column
			aIndex++;
			shifted = getDataColumn(aIndex);
		}
		super.addColumn(forceRefreshColumns, aIndex, aColumn, aWidth, aHeader, aFooter, hidden);
		if (aColumn instanceof ModelGridColumn<?> && !columnsAjusting) {
			ModelGridColumn<?> mInserted = (ModelGridColumn<?>) aColumn;
			mInserted.setGrid(this);
			if (shifted != null) {
				assert shifted instanceof ModelGridColumn<?>;
				ModelGridColumn<?> mShifted = (ModelGridColumn<?>) shifted;
				HeaderNode hParent = mShifted.getHeaderNode().getParent();
				if (hParent != null) {
					int hIndex = hParent.getChildren().indexOf(mShifted.getHeaderNode());
					hParent.getChildren().add(hIndex, mInserted.getHeaderNode());
					mInserted.getHeaderNode().setParent(hParent);
				} else {
					int hIndex = header.indexOf(mShifted.getHeaderNode());
					header.add(hIndex, mInserted.getHeaderNode());
				}
			} else {
				header.add(mInserted.getHeaderNode());
			}
			applyHeader();
		}
	}

	public void addColumn(ModelGridColumn<?> aColumn) {
		addColumn(getDataColumnCount(), aColumn);
	}

	public void addColumn(int aIndex, ModelGridColumn<?> aColumn) {
		addColumn(aIndex, aColumn, aColumn.getWidth() + "px", aColumn.getHeaderNode().getHeader(), null, aColumn.isVisible());
	}

	protected ModelGridColumn<?> treeIndicatorColumn;

	@Override
	public void addColumn(int aIndex, Column<Row, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		super.addColumn(aIndex, aColumn, aWidth, aHeader, aFooter, hidden);
		if (treeIndicatorColumn == null) {
			int treeIndicatorIndex = rowsHeaderType == ROWS_HEADER_TYPE_NONE ? 0 : 1;
			if (treeIndicatorIndex < getDataColumnCount()) {
				Column<Row, ?> indicatorColumn = getDataColumn(treeIndicatorIndex);
				if (indicatorColumn instanceof ModelGridColumn<?>) {
					treeIndicatorColumn = (ModelGridColumn<?>) indicatorColumn;
					if (dataProvider instanceof TreeDataProvider<?> && treeIndicatorColumn.getCell() instanceof TreeExpandableCell<?, ?>) {
						TreeExpandableCell<Row, ?> treeCell = (TreeExpandableCell<Row, ?>) treeIndicatorColumn.getCell();
						treeCell.setDataProvider((TreeDataProvider<Row>) dataProvider);
					}
				}
			}
		}
	}

	@Override
	public void removeColumn(int aIndex) {
		Column<Row, ?> toDel = getDataColumn(aIndex);
		if (toDel instanceof ModelGridColumn<?>) {
			ModelGridColumn<?> mCol = (ModelGridColumn<?>) toDel;
			if (mCol == treeIndicatorColumn) {
				TreeExpandableCell<Row, ?> treeCell = (TreeExpandableCell<Row, ?>) mCol.getCell();
				if (treeCell.getDataProvider() != null) {
					treeCell.setDataProvider(null);
				}
				treeIndicatorColumn = null;
			}
			super.removeColumn(aIndex);
			if (!columnsAjusting) {
				HeaderNode colNode = mCol.getHeaderNode();
				HeaderNode parent = mCol.getHeaderNode().getParent();
				while (parent != null) {
					parent.getChildren().remove(colNode);
					colNode.setParent(null);
					if (!parent.getChildren().isEmpty())
						break;
					colNode = parent;
					parent = parent.getParent();
				}
				if (parent == null)
					header.remove(colNode);
				mCol.setGrid(null);
				applyHeader();
			}
		} else {
			// won't remove service columns
		}
	}

	@Override
	public void setColumnWidth(Column<Row, ?> aColumn, double aWidth, Unit aUnit) {
		super.setColumnWidth(aColumn, aWidth, aUnit);
		if (aColumn instanceof ModelGridColumnFacade) {
			ModelGridColumnFacade colFacade = (ModelGridColumnFacade) aColumn;
			colFacade.updateWidth(aWidth);
		}
	}

	protected boolean headerAjusting;

	public boolean isHeaderAjusting() {
		return headerAjusting;
	}

	public void setHeaderAjusting(boolean aValue) {
		headerAjusting = aValue;
	}

	public void applyHeader() {
		if (!headerAjusting) {
			ThemedHeaderOrFooterBuilder<Row> leftBuilder = (ThemedHeaderOrFooterBuilder<Row>) headerLeft.getHeaderBuilder();
			ThemedHeaderOrFooterBuilder<Row> rightBuilder = (ThemedHeaderOrFooterBuilder<Row>) headerRight.getHeaderBuilder();
			List<HeaderNode> leftHeader = HeaderSplitter.split(header, 0, frozenColumns);
			leftBuilder.setHeaderNodes(leftHeader);
			List<HeaderNode> rightHeader = HeaderSplitter.split(header, frozenColumns, getDataColumnCount());
			rightBuilder.setHeaderNodes(rightHeader);
			redrawHeaders();
		}
	}

	@Override
	public void setSelectionModel(SelectionModel<Row> aValue) {
		assert aValue != null : "Selection model can't be null.";
		SelectionModel<? super Row> oldValue = getSelectionModel();
		if (aValue != oldValue) {
			if (positionSelectionHandler != null)
				positionSelectionHandler.removeHandler();
			super.setSelectionModel(aValue);
			positionSelectionHandler = aValue.addSelectionChangeHandler(new RowsetPositionSelectionHandler(rowsSource, aValue));
		}
	}

	protected void applyColorsFontCursor() {
		if (published.isBackgroundSet() && published.isOpaque())
			ControlsUtils.applyBackground(this, published.getBackground());
		if (published.isForegroundSet())
			ControlsUtils.applyForeground(this, published.getForeground());
		if (published.isFontSet())
			ControlsUtils.applyFont(this, published.getFont());
		if (published.isCursorSet())
			ControlsUtils.applyCursor(this, published.getCursor());
	}

	public Callback<RowsetEvent, RowsetEvent> getCrossUpdaterAction() {
		return crossUpdaterAction;
	}

	public void addUpdatingTriggerEntity(Entity aTrigger) {
		crossUpdater.add(aTrigger);
	}

	public Entity getRowsSource() {
		return rowsSource;
	}

	/**
	 * Sets entity instance, that have to be used as rows source. Configures
	 * tree if needed.
	 * 
	 * @param aValue
	 * @throws Exception
	 */
	public void setRowsSource(Entity aValue) throws Exception {
		if (rowsSource != aValue) {
			if(sortHandlerReg != null)
				sortHandlerReg.removeHandler();
			rowsSource = aValue;
			if (rowsSource != null) {
				Runnable onResize = new Runnable() {
					@Override
					public void run() {
						setupVisibleRanges();
					}

				};
				Runnable onSort = new Runnable() {
					@Override
					public void run() {
						if(dataProvider instanceof IndexOfProvider<?>)
							((IndexOfProvider<?>)dataProvider).rescan();
					}

				};
				if (isTreeConfigured()) {
					RowsetTree tree = new RowsetTree(rowsSource.getRowset(), unaryLinkField.field);
					TreeDataProvider<Row> treeDataProvider;
					if (isLazyTreeConfigured()) {
						treeDataProvider = new TreeDataProvider<>(tree, onResize, new RowChildrenFetcher(rowsSource, (Parameter) param2GetChildren.field, (Parameter) paramSourceField.field));
					} else
						treeDataProvider = new TreeDataProvider<>(tree, onResize);
					setDataProvider(treeDataProvider);
					sortHandler = new TreeMultiSortHandler<>(treeDataProvider, onSort);
					treeDataProvider.addExpandedCollapsedHandler(new ExpandedCollapsedHandler<Row>(){

						@Override
                        public void expanded(Row anElement) {
							ColumnSortEvent.fire(ModelGrid.this, sortList);
                        }

						@Override
                        public void collapsed(Row anElement) {
							ColumnSortEvent.fire(ModelGrid.this, sortList);
                        }
						
					});
				} else {
					setDataProvider(new RowsetDataProvider(rowsSource.getRowset(), onResize));
					sortHandler = new ListMultiSortHandler<>(dataProvider.getList(), onSort);
				}
				sortHandlerReg = addColumnSortHandler(sortHandler);
			}
		}
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	public void setPublished(JavaScriptObject aValue) {
		published = aValue != null ? aValue.<PublishedComponent> cast() : null;
		if (published != null) {
			// Here were a cycle setting published to each column and publish
			// call
		}
	}

	public JavaScriptObject getOnRender() {
		return onRender;
	}

	public void setOnRender(JavaScriptObject aValue) {
		onRender = aValue;
	}

	public void selectRow(Row aRow) {
		getSelectionModel().setSelected(aRow, true);
	}

	public void unselectRow(Row aRow) {
		getSelectionModel().setSelected(aRow, false);
	}

	public List<JavaScriptObject> getJsSelected() throws Exception {
		List<JavaScriptObject> result = new ArrayList<>();
		for (Row row : dataProvider.getList()) {
			if (getSelectionModel().isSelected(row))
				result.add(Entity.publishRowFacade(row, rowsSource));
		}
		return result;
	}

	public void clearSelection() {
		SelectionModel<? super Row> sm = getSelectionModel();
		for (Row row : dataProvider.getList()) {
			if (getSelectionModel().isSelected(row)) {
				sm.setSelected(row, false);
			}
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean aValue) {
		editable = aValue;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean aValue) {
		deletable = aValue;
	}

	public boolean isInsertable() {
		return insertable;
	}

	public void setInsertable(boolean aValue) {
		insertable = aValue;
	}

	public boolean makeVisible(Row aRow, boolean needToSelect) {
		return false;
	}

	public void find() {
		finder.show();
		finder.toFront();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		finder.close();
	}

	public PublishedStyle complementPublishedStyle(PublishedStyle aStyle) {
		PublishedStyle complemented = aStyle;
		if (published.isBackgroundSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setBackground(published.getBackground());
		}
		if (published.isForegroundSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setForeground(published.getForeground());
		}
		if (published.isFontSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setFont(published.getFont());
		}
		return complemented;
	}
}
