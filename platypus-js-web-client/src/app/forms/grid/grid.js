/* global Infinity */
define([
    '../../id',
    '../../ui',
    '../../extend',
    '../../invoke',
    '../../logger',
    '../../common-utils/color',
    '../bound',
    '../widget',
    '../key-codes',
    '../events/key-event',
    '../events/item-event',
    '../events/sort-event',
    '../events/blur-event',
    '../events/focus-event',
    '../menu/menu',
    '../menu/check-box-menu-item',
    './section',
    './header/analyzer',
    './header/splitter',
    './columns/marker-service-column',
    './columns/check-box-service-column',
    './columns/radio-button-service-column'
], function (
        Id,
        Ui,
        extend,
        Invoke,
        Logger,
        Color,
        Bound,
        Widget,
        KeyCodes,
        KeyEvent,
        ItemEvent,
        SortEvent,
        BlurEvent,
        FocusEvent,
        Menu,
        CheckBoxMenuItem,
        Section,
        HeaderAnalyzer,
        HeaderSplitter,
        MarkerServiceColumn,
        CheckBoxServiceColumn,
        RadioButtonServiceColumn
        ) {

//public class Grid extends Widget implements HasSelectionHandlers<JavaScriptObject>, HasSelectionLead, HasOnRender, HasBinding, 
//        Focusable, HasFocusHandlers, HasBlurHandlers,
//        HasKeyDownHandlers, HasKeyPressHandlers, HasKeyUpHandlers {

    function Grid() {
        var shell = document.createElement('div');
        Widget.call(this, shell);
        var self = this;

        var cellsStyleElement = document.createElement('style');
        var rowsStyleElement = document.createElement('style');
        var headerCellsStyleElement = document.createElement('style');
        var headerRowsStyleElement = document.createElement('style');

        var oddRowsStyleElement = document.createElement('style');
        var evenRowsStyleElement = document.createElement('style');

        var dynamicCellsClassName = 'p-grid-cell-' + Id.generate();
        var dynamicRowsClassName = 'p-grid-row-' + Id.generate();
        var dynamicHeaderCellsClassName = 'p-grid-header-cell-' + Id.generate();
        var dynamicHeaderRowsClassName = 'p-grid-header-row-' + Id.generate();

        var dynamicOddRowsClassName = 'p-grid-odd-row-' + Id.generate();
        var dynamicEvenRowsClassName = 'p-grid-even-row-' + Id.generate();

        var headerContainer = document.createElement('div');
        var headerLeftContainer = document.createElement('div');
        var headerRightContainer = document.createElement('div');

        var headerLeft = new Section(self, dynamicCellsClassName, dynamicRowsClassName, dynamicHeaderCellsClassName, dynamicHeaderRowsClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName);
        Object.defineProperty(this, 'headerLeft', {
            get: function () {
                return headerLeft;
            }
        });
        headerLeftContainer.appendChild(headerLeft.element);
        var headerRight = new Section(self, dynamicCellsClassName, dynamicRowsClassName, dynamicHeaderCellsClassName, dynamicHeaderRowsClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName);
        Object.defineProperty(this, 'headerRight', {
            get: function () {
                return headerRight;
            }
        });
        headerRightContainer.appendChild(headerRight.element);

        var columnsChevron = document.createElement('div');

        headerContainer.appendChild(headerLeftContainer);
        headerContainer.appendChild(headerRightContainer);
        headerContainer.appendChild(columnsChevron);

        var frozenContainer = document.createElement('div');
        var frozenLeftContainer = document.createElement('div');
        var frozenRightContainer = document.createElement('div');

        var frozenLeft = new Section(self, dynamicCellsClassName, dynamicRowsClassName, dynamicHeaderCellsClassName, dynamicHeaderRowsClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName);
        Object.defineProperty(this, 'frozenLeft', {
            get: function () {
                return frozenLeft;
            }
        });
        frozenLeftContainer.appendChild(frozenLeft.element);
        var frozenRight = new Section(self, dynamicCellsClassName, dynamicRowsClassName, dynamicHeaderCellsClassName, dynamicHeaderRowsClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName);
        Object.defineProperty(this, 'frozenRight', {
            get: function () {
                return frozenRight;
            }
        });
        frozenRightContainer.appendChild(frozenRight.element);
        frozenContainer.appendChild(frozenLeftContainer);
        frozenContainer.appendChild(frozenRightContainer);

        var bodyContainer = document.createElement('div');
        var bodyLeftContainer = document.createElement('div');
        var bodyRightContainer = document.createElement('div');
        var bodyLeft = new Section(self, dynamicCellsClassName, dynamicRowsClassName, dynamicHeaderCellsClassName, dynamicHeaderRowsClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName);
        Object.defineProperty(this, 'bodyLeft', {
            get: function () {
                return bodyLeft;
            }
        });
        bodyLeftContainer.appendChild(bodyLeft.element);
        var bodyRight = new Section(self, dynamicCellsClassName, dynamicRowsClassName, dynamicHeaderCellsClassName, dynamicHeaderRowsClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName);
        Object.defineProperty(this, 'bodyRight', {
            get: function () {
                return bodyRight;
            }
        });
        bodyRightContainer.appendChild(bodyRight.element);
        bodyContainer.appendChild(bodyLeftContainer);
        bodyContainer.appendChild(bodyRightContainer);
        Ui.on(bodyRightContainer, Ui.Events.SCROLL, function (evt) {
            [
                headerRight,
                frozenRight,
                footerRight
            ].forEach(function (section) {
                section.element.style.marginLeft = -bodyRightContainer.scrollLeft + 'px';
            });
        });

        var footerContainer = document.createElement('div');
        var footerLeftContainer = document.createElement('div');
        var footerRightContainer = document.createElement('div');
        var footerLeft = new Section(self, dynamicCellsClassName, dynamicRowsClassName, dynamicHeaderCellsClassName, dynamicHeaderRowsClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName);
        Object.defineProperty(this, 'footerLeft', {
            get: function () {
                return footerLeft;
            }
        });
        footerLeftContainer.appendChild(footerLeft.element);
        var footerRight = new Section(self, dynamicCellsClassName, dynamicRowsClassName, dynamicHeaderCellsClassName, dynamicHeaderRowsClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName);
        Object.defineProperty(this, 'footerRight', {
            get: function () {
                return footerRight;
            }
        });
        footerRightContainer.appendChild(footerRight.element);
        footerContainer.appendChild(footerLeftContainer);
        footerContainer.appendChild(footerRightContainer);
        footerContainer.style.display = 'none';

        var columnNodes = [];

        var columnsFacade = [];
        Object.defineProperty(this, 'columns', {
            get: function () {
                return columnsFacade;
            }
        });
        var sortedColumns = [];
        var headerRowsHeight = 30;
        var rowsHeight = 30;
        var renderingThrottle = 0; // No throttling
        var renderingPadding = 0; // No padding
        var showHorizontalLines = true;
        var showVerticalLines = true;
        var showOddRowsInOtherColor = true;
        var gridColor = null;
        var oddRowsColor = null;
        var evenRowsColor = new Color(241, 241, 241, 255);

        var selectedRows = new Set();
        var selectionLead = null;
        Object.defineProperty(this, 'selected', {
            get: function () {
                return Array.from(selectedRows);
            }
        });

        var frozenColumns = 0;
        var frozenRows = 0;
        var parentField = null;
        var childrenField = null;
        var indent = 20;
        //
        var data = null; // bounded data. this is not rows source. rows source is data['field' property path]
        var sortedRows = []; // rows in view. subject of sorting. subject of collapse / expand in tree.
        var expandedRows = new Set();
        var depths = new Map();
        var field = null;
        var boundToData = null;
        var boundToElements = null;
        var boundToCursor = null;
        var cursorProperty = 'cursor';
        var onRender = null;
        var onAfterRender = null;
        // runtime
        var activeEditor = null;
        var editable = true;
        var deletable = true;
        var insertable = true;
        var draggableRows = false;

        shell.className = 'p-widget p-grid-shell p-scroll p-vertical-scroll-filler p-horizontal-scroll-filler';

        headerContainer.className = 'p-grid-section-header';
        headerLeftContainer.className = 'p-grid-section-header-left';
        headerRightContainer.className = 'p-grid-section-header-right';
        frozenContainer.className = 'p-grid-section-frozen';
        frozenLeftContainer.className = 'p-grid-section-frozen-left';
        frozenRightContainer.className = 'p-grid-section-frozen-right';
        bodyContainer.className = 'p-grid-section-body';
        bodyLeftContainer.className = 'p-grid-section-body-left';
        bodyRightContainer.className = 'p-grid-section-body-right';
        footerContainer.className = 'p-grid-section-footer';
        footerLeftContainer.className = 'p-grid-section-footer-left';
        footerRightContainer.className = 'p-grid-section-footer-right';

        columnsChevron.className = 'p-grid-columns-chevron';

        shell.appendChild(cellsStyleElement);
        shell.appendChild(rowsStyleElement);
        shell.appendChild(headerCellsStyleElement);
        shell.appendChild(headerRowsStyleElement);

        shell.appendChild(oddRowsStyleElement);
        shell.appendChild(evenRowsStyleElement);

        shell.appendChild(headerContainer);
        shell.appendChild(frozenContainer);
        shell.appendChild(bodyContainer);
        shell.appendChild(footerContainer);

        Ui.on(shell, Ui.Events.DRAGSTART, function (event) {
            if (draggableRows) {
                var targetElement = event.target;
                if ('tr' === targetElement.tagName.toLowerCase()) {
                    event.stopPropagation();
                    var dragged = targetElement[Section.JS_ROW_NAME];
                    var rows = discoverRows();
                    var dataIndex = rows.indexOf(dragged);
                    event.dataTransfer.setData('text/p-grid-row',
                            '{"p-grid-name":"' + name + '", "data-row-index": ' + dataIndex + '}');
                }
            }
        });

        var columnsMenu = null;
        (function () {
            function fillColumnsMenu(section, target) {
                for (var i = 0; i < section.columnsCount; i++) {
                    (function () {
                        var column = section.getColumn(i);
                        var miCheck = new CheckBoxMenuItem(column.header.text, column.visible);
                        miCheck.addValueChangeHandler(function (event) {
                            column.visible = !!event.newValue;
                        });
                        target.add(miCheck);
                    }());
                }
            }
            function showColumnsMenu(event) {
                columnsMenu = new Menu();
                fillColumnsMenu(headerLeft, columnsMenu);
                fillColumnsMenu(headerRight, columnsMenu);
                Ui.startMenuSession(columnsMenu);
                var pageX = 'pageX' in event ? event.pageX : event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
                var pageY = 'pageY' in event ? event.pageY : event.clientY + document.body.scrollTop + document.documentElement.scrollTop;
                columnsMenu.showAt(pageX, pageY);
            }
            Ui.on(columnsChevron, Ui.Events.CLICK, function (event) {
                if (columnsMenu) {
                    columnsMenu.close();
                    columnsMenu = null;
                } else {
                    showColumnsMenu(event);
                }
            });
            Ui.on(columnsChevron, Ui.Events.CONTEXTMENU, function (event) {
                event.preventDefault();
                event.stopPropagation();
                showColumnsMenu(event);
            });
        }());

        regenerateDynamicHeaderCellsStyles();
        regenerateDynamicHeaderRowsStyles();
        regenerateDynamicCellsStyles();
        regenerateDynamicRowsStyles();

        regenerateDynamicOddRowsStyles();
        regenerateDynamicEvenRowsStyles();

        Ui.on(shell, Ui.Events.KEYUP, function (event) {
            var rows = discoverRows();
            if (!activeEditor) {
                if (event.keyCode === KeyCodes.KEY_DELETE && deletable) {
                    if (sortedRows.length > 0) {
                        // calculate some view sugar
                        var lastSelectedViewIndex = -1;
                        for (var i = sortedRows.length - 1; i >= 0; i--) {
                            var element = sortedRows[i];
                            if (isSelected(element)) {
                                lastSelectedViewIndex = i;
                                break;
                            }
                        }
                        // actually delete selected elements
                        var deletedAt = -1;
                        for (var i = rows.length - 1; i >= 0; i--) {
                            var row = rows[i];
                            if (isSelected(row)) {
                                rows.splice(i, 1);
                                deletedAt = i;
                            }
                        }
                        var viewIndexToSelect = lastSelectedViewIndex;
                        if (deletedAt > -1) {
                            // TODO: Check if Invoke.Later is an option
                            var vIndex = viewIndexToSelect;
                            if (vIndex >= 0 && sortedRows.length > 0) {
                                if (vIndex >= sortedRows.length) {
                                    vIndex = sortedRows.length - 1;
                                }
                                var toSelect = sortedRows[vIndex];
                                makeVisible(toSelect, true);
                            } else {
                                self.setFocus(true);
                            }
                        }
                    }
                } else if (event.keyCode === KeyCodes.KEY_INSERT && insertable) {
                    var insertAt = -1;
                    var lead = selectionLead;
                    insertAt = rows.indexOf(lead);
                    insertAt++;
                    var elementClass = rows['elementClass'];
                    var inserted = elementClass ? new elementClass()
                            : {};
                    rows.splice(insertAt, 0, inserted);
                    // TODO: Check if Invoke.Later is an option
                    makeVisible(inserted, true);
                }
            }
        });

        function discoverRows() {
            var rows = data && field ? Bound.getPathData(data, field) : data;
            return rows ? rows : [];
        }

        function removedItems(anArray) {
            if (!Array.isArray(anArray))
                anArray = [anArray];
            rebindElements();
            redraw();
        }

        Object.defineProperty(this, 'removed', {
            get: function () {
                return removedItems;
            }
        });

        function addedItems(anArray) {
            if (!Array.isArray(anArray))
                anArray = [anArray];
            rebindElements();
            redraw();
        }

        Object.defineProperty(this, 'added', {
            get: function () {
                return addedItems;
            }
        });

        function changedItems(anArray) {
            if (!Array.isArray(anArray))
                anArray = [anArray];
            redraw();
        }

        Object.defineProperty(this, 'changed', {
            get: function () {
                return changedItems;
            }
        });

        function isSelected(item) {
            return selectedRows.has(item);
        }
        Object.defineProperty(this, 'isSelected', {
            get: function () {
                return isSelected;
            }
        });

        function select(item) {
            selectedRows.add(item);
            selectionLead = item;
            var rows = discoverRows();
            if (cursorProperty)
                rows[cursorProperty] = selectionLead;
            fireSelected(item);
        }
        Object.defineProperty(this, 'select', {
            get: function () {
                return select;
            }
        });

        function selectAll() {
            var rows = discoverRows();
            selectedRows = new Set(rows);
            selectionLead = rows.length > 0 ? rows[0] : null;
            if (cursorProperty)
                rows[cursorProperty] = selectionLead;
            fireSelected(selectionLead);
        }
        Object.defineProperty(this, 'selectAll', {
            get: function () {
                return selectAll;
            }
        });

        function unselect(item) {
            if (selectionLead === item) {
                selectionLead = null;
            }
            fireSelected(null);
            return selectedRows.delete(item);
        }
        Object.defineProperty(this, 'unselect', {
            get: function () {
                return unselect;
            }
        });

        function unselectAll() {
            selectedRows.clear();
            if (selectedRows.has(selectionLead)) {
                selectionLead = null;
            }
            fireSelected(null);
        }
        Object.defineProperty(this, 'unselectAll', {
            get: function () {
                return unselectAll;
            }
        });

        Object.defineProperty(this, 'dynamicCellClassName', {
            get: function () {
                return dynamicCellsClassName;
            }
        });

        Object.defineProperty(this, 'showHorizontalLines', {
            get: function () {
                return showHorizontalLines;
            },
            set: function (aValue) {
                if (showHorizontalLines !== aValue) {
                    showHorizontalLines = !!aValue;
                    regenerateDynamicCellsStyles();
                }
            }
        });

        Object.defineProperty(this, 'showVerticalLines', {
            get: function () {
                return showVerticalLines;
            },
            set: function (aValue) {
                if (showVerticalLines !== aValue) {
                    showVerticalLines = !!aValue;
                    regenerateDynamicCellsStyles();
                }
            }
        });

        function regenerateDynamicCellsStyles() {
            cellsStyleElement.innerHTML =
                    '.' + dynamicCellsClassName + '{' +
                    (showHorizontalLines ? '' : 'border-top-style: none;') +
                    (showHorizontalLines ? '' : 'border-bottom-style: none;') +
                    (showVerticalLines ? '' : 'border-left-style: none;') +
                    (showVerticalLines ? '' : 'border-right-style: none;') +
                    (gridColor ? 'border-color: ' + gridColor.toStyled() + ';' : '') +
                    '}';
        }

        Object.defineProperty(this, 'gridColor', {
            get: function () {
                return gridColor;
            },
            set: function (aValue) {
                if (gridColor !== aValue) {
                    gridColor = aValue;
                    regenerateDynamicCellsStyles();
                }
            }
        });

        function regenerateDynamicOddRowsStyles() {
            if (showOddRowsInOtherColor && oddRowsColor) {
                oddRowsStyleElement.innerHTML =
                        '.' + dynamicOddRowsClassName + '{' +
                        (oddRowsColor ? 'background-color: ' + oddRowsColor.toStyled() + ';' : '') +
                        '}';
            } else {
                oddRowsStyleElement.innerHTML = '';
            }
        }

        Object.defineProperty(this, 'oddRowsColor', {
            get: function () {
                return oddRowsColor;
            },
            set: function (aValue) {
                if (oddRowsColor !== aValue) {
                    oddRowsColor = aValue;
                    regenerateDynamicOddRowsStyles();
                }
            }
        });
        function regenerateDynamicEvenRowsStyles() {
            if (showOddRowsInOtherColor && evenRowsColor) {
                evenRowsStyleElement.innerHTML =
                        '.' + dynamicEvenRowsClassName + '{' +
                        (evenRowsColor ? 'background-color: ' + evenRowsColor.toStyled() + ';' : '') +
                        '}';
            } else {
                evenRowsStyleElement.innerHTML = '';
            }
        }

        Object.defineProperty(this, 'evenRowsColor', {
            get: function () {
                return evenRowsColor;
            },
            set: function (aValue) {
                if (evenRowsColor !== aValue) {
                    evenRowsColor = aValue;
                    regenerateDynamicEvenRowsStyles();
                }
            }
        });

        function regenerateDynamicRowsStyles() {
            rowsStyleElement.innerHTML =
                    '.' + dynamicRowsClassName + '{' +
                    ' height: ' + rowsHeight + 'px;' +
                    '}';
        }

        Object.defineProperty(this, 'rowsHeight', {
            get: function () {
                return rowsHeight;
            },
            set: function (aValue) {
                if (rowsHeight !== aValue && aValue >= 10) {
                    rowsHeight = aValue;
                    regenerateDynamicRowsStyles();
                    bodyLeft.rowsHeight = rowsHeight;
                    bodyRight.rowsHeight = rowsHeight;
                }
            }
        });
        Object.defineProperty(this, 'renderingThrottle', {
            get: function () {
                return renderingThrottle;
            },
            set: function (aValue) {
                renderingThrottle = aValue;
                bodyRight.renderingThrottle = renderingThrottle;
            }
        });
        Object.defineProperty(this, 'renderingPadding', {
            get: function () {
                return renderingPadding;
            },
            set: function (aValue) {
                renderingPadding = aValue;
                bodyRight.renderingPadding = renderingPadding;
            }
        });

        function regenerateDynamicHeaderCellsStyles() {
            headerCellsStyleElement.innerHTML =
                    '.' + dynamicHeaderCellsClassName + '{' +
                    '}';
        }

        function regenerateDynamicHeaderRowsStyles() {
            headerCellsStyleElement.innerHTML =
                    '.' + dynamicHeaderRowsClassName + '{' +
                    ' height: ' + headerRowsHeight + 'px;' +
                    '}';
        }

        Object.defineProperty(this, 'headerRowsHeight', {
            get: function () {
                return headerRowsHeight;
            },
            set: function (aValue) {
                if (headerRowsHeight !== aValue && aValue >= 10) {
                    headerRowsHeight = aValue;
                    regenerateDynamicHeaderRowsStyles();
                }
            }
        });

        Object.defineProperty(this, 'headerVisible', {
            get: function () {
                return 'none' !== headerLeft.element.style.display
                        && 'none' !== headerRight.element.style.display;
            },
            set: function (aValue) {
                if (aValue) {
                    headerContainer.style.display = '';
                } else {
                    headerContainer.style.display = 'none';
                }
            }
        });

        Object.defineProperty(this, 'frozenColumns', {
            get: function () {
                return frozenColumns;
            },
            set: function (aValue) {
                if (aValue >= 0 && aValue <= getColumnsCount() && frozenColumns !== aValue) {
                    frozenColumns = aValue;
                    applyColumnsNodes();
                }
            }
        });

        Object.defineProperty(this, 'frozenRows', {
            get: function () {
                return frozenRows;
            },
            set: function (aValue) {
                if (aValue >= 0 && frozenRows !== aValue) {
                    frozenRows = aValue;
                    setupRanges();
                }
            }
        });

        Object.defineProperty(this, 'showOddRowsInOtherColor', {
            get: function () {
                return showOddRowsInOtherColor;
            },
            set: function (aValue) {
                if (showOddRowsInOtherColor !== aValue) {
                    showOddRowsInOtherColor = aValue;
                    regenerateDynamicOddRowsStyles();
                    regenerateDynamicEvenRowsStyles();
                }
            }
        });

        Object.defineProperty(this, 'draggableRows', {
            get: function () {
                return draggableRows;
            },
            set: function (aValue) {
                if (draggableRows !== aValue) {
                    draggableRows = aValue;
                    [frozenLeft, frozenRight, bodyLeft, bodyRight].forEach(function (section) {
                        section.draggableRows = aValue;
                    });
                }
            }
        });

        Object.defineProperty(this, 'activeEditor', {
            get: function () {
                return activeEditor;
            },
            set: function (aWidget) {
                activeEditor = aWidget;
            }
        });

        Object.defineProperty(this, 'onRender', {
            get: function () {
                return onRender;
            },
            set: function (aValue) {
                onRender = aValue;
            }
        });

        Object.defineProperty(this, 'onAfterRender', {
            get: function () {
                return onAfterRender;
            },
            set: function (aValue) {
                onAfterRender = aValue;
            }
        });

        Object.defineProperty(this, 'rows', {
            get: function () {
                return discoverRows();
            }
        });
        Object.defineProperty(this, 'viewRows', {
            get: function () {
                return sortedRows;
            }
        });
        Object.defineProperty(this, 'cursorProperty', {
            get: function () {
                return cursorProperty;
            },
            set: function (aValue) {
                if (aValue && cursorProperty !== aValue) {
                    unbind();
                    cursorProperty = aValue;
                    bind();
                }
            }
        });

        Object.defineProperty(this, 'editable', {
            get: function () {
                return editable;
            },
            set: function (aValue) {
                editable = aValue;
            }
        });

        Object.defineProperty(this, 'deletable', {
            get: function () {
                return deletable;
            },
            set: function (aValue) {
                deletable = aValue;
            }
        });

        Object.defineProperty(this, 'insertable', {
            get: function () {
                return insertable;
            },
            set: function (aValue) {
                insertable = aValue;
            }
        });

        var serviceColumnsRedrawQueued = null;

        function enqueueServiceColumnsRedraw() {
            function redrawServiceColumns() {
                if (serviceColumnsRedrawQueued === redrawServiceColumns) {
                    serviceColumnsRedrawQueued = null;
                    for (var i = 0; i < getColumnsCount(); i++) {
                        var col = getColumn(i);
                        if (col instanceof MarkerServiceColumn) {
                            if (i < frozenColumns) {
                                frozenLeft.redrawColumn(i);
                                bodyLeft.redrawColumn(i);
                            } else {
                                frozenRight.redrawColumn(i - frozenColumns);
                                bodyRight.redrawColumn(i - frozenColumns);
                            }
                        }
                    }
                }
            }
            Invoke.later(redrawServiceColumns);
            serviceColumnsRedrawQueued = redrawServiceColumns;
        }

        var redrawQueued = null;

        function enqueueRedraw() {
            function callredraw() {
                if (redrawQueued === callredraw) {
                    redrawQueued = null;
                    redraw();
                }
            }
            Invoke.later(callredraw);
            redrawQueued = callredraw;
        }

        function depthOf(item) {
            return isTreeConfigured() ? depths.get(item) : 0;
        }
        Object.defineProperty(this, 'depthOf', {
            get: function () {
                return depthOf;
            }
        });

        function isExpanded(anElement) {
            return isTreeConfigured() && expandedRows.has(anElement);
        }

        Object.defineProperty(this, 'expanded', {
            get: function () {
                return isExpanded;
            }
        });

        function expand(anElement) {
            if (isTreeConfigured()) {
                if (!expandedRows.has(anElement)) {
                    var children = getChildrenOf(anElement);
                    if (children && children.length > 0) {
                        expandedRows.add(anElement);
                        applyRows(false);
                        setupRanges(true);
                        fireExpanded(anElement);
                    }
                }
            }
        }

        Object.defineProperty(this, 'expand', {
            get: function () {
                return expand;
            }
        });

        function collapse(anElement) {
            if (isTreeConfigured()) {
                if (expandedRows.has(anElement)) {
                    expandedRows.delete(anElement);
                    applyRows(false);
                    setupRanges(true);
                    fireCollapsed(anElement);
                }
            }
        }

        Object.defineProperty(this, 'collapse', {
            get: function () {
                return collapse;
            }
        });

        function toggle(anElement) {
            if (isTreeConfigured()) {
                if (isExpanded(anElement)) {
                    collapse(anElement);
                } else {
                    expand(anElement);
                }
            }
        }

        Object.defineProperty(this, 'toggle', {
            get: function () {
                return toggle;
            }
        });

        // Tree structure
        function isLeaf(anElement) {
            return !hasRowChildren(anElement);
        }
        Object.defineProperty(this, 'isLeaf', {
            get: function () {
                return isLeaf;
            }
        });

        function hasRowChildren(parent) {
            var children = findChildren(parent);
            return children && children.length > 0;
        }

        function findChildren(aParent) {
            if (aParent) {
                return aParent[childrenField];
            } else {
                var rows = discoverRows();
                var roots = [];
                for (var i = 0; i < rows.length; i++) {
                    var item = rows[i];
                    if (item && !item[parentField]) {
                        roots.push(item);
                    }
                }
                return roots;
            }
        }

        function getParentOf(anElement) {
            var parent = anElement[parentField];
            return parent == null ? null : parent; // undefined -> null
        }

        function getChildrenOf(anElement) {
            var found = findChildren(anElement);
            return found ? found : [];
        }

        /**
         * Builds path to specified element if the element belongs to the model.
         *
         * @param anElement Element to build path to.
         * @return Array of elements comprising the path, excluding
         * root null. So for the roots of the forest path will be a list with one
         * element.
         */
        function buildPathTo(anElement) {
            var path = [];
            if (anElement) {
                var currentParent = anElement;
                var added = new Set();
                path.push(currentParent);
                added.add(currentParent);
                while (currentParent) {
                    currentParent = getParentOf(currentParent);
                    if (currentParent && !added.has(currentParent)) {
                        path.unshift(currentParent);
                        added.add(currentParent);
                    } else {
                        break;
                    }
                }
            }
            return path;
        }
        Object.defineProperty(this, 'buildPathTo', {
            get: function () {
                return buildPathTo;
            }
        });

        function makeVisible(anElement, aNeedToSelect) {
            // TODO: refactor indexof to something else
            // TODO: think about tree data model and path to item expanding
            //IndexOfProvider<JavaScriptObject> indexOfProvider = (IndexOfProvider<JavaScriptObject>) dataProvider;
            var index = -1;//indexOfProvider.indexOf(anElement);
            if (index > -1) {
                if (index >= 0 && index < frozenRows) {
                    var leftCell = frozenLeft.getViewCell(index, 0);
                    if (leftCell) {
                        leftCell.scrollIntoView();
                    } else {
                        var rightCell = frozenRight.getViewCell(index, 0);
                        if (rightCell) {
                            rightCell.scrollIntoView();
                        }
                    }
                } else {
                    var leftCell = bodyLeft.getViewCell(index, 0);
                    if (leftCell) {
                        leftCell.scrollIntoView();
                    } else {
                        var rightCell = bodyRight.getViewCell(index, 0);
                        if (rightCell) {
                            rightCell.scrollIntoView();
                        }
                    }
                }
                if (aNeedToSelect) {
                    unselectAll();
                    select(anElement);
                    if (index >= 0 && index < frozenRows) {
                        frozenLeft.keyboardSelectedRow = index;
                        frozenRight.keyboardSelectedRow = index;
                    } else {
                        bodyLeft.keyboardSelectedRow = index - frozenRows;
                        bodyRight.keyboardSelectedRow = index - frozenRows;
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        Object.defineProperty(this, 'makeVisible', {
            get: function () {
                return makeVisible;
            }
        });

        function rebind() {
            unbind();
            bind();
        }

        function bind() {
            if (data) {
                if (field) {
                    boundToData = Bound.observePath(data, field, function (anEvent) {
                        rebind();
                        redraw();
                    });
                }
                bindElements();
                bindCursor();
                applyRows(false);
                setupRanges(true);
            }
        }

        function bindElements() {
            var rows = discoverRows();
            boundToElements = Bound.observeElements(rows, function (anEvent) {
                redraw();
            });
        }

        function unbindElements() {
            if (boundToElements) {
                boundToElements.removeHandler();
                boundToElements = null;
            }
        }

        function rebindElements() {
            unbindElements();
            bindElements();
        }

        function unbind() {
            selectedRows.clear();
            expandedRows.clear();
            if (boundToData) {
                boundToData.removeHandler();
                boundToData = null;
            }
            unbindElements();
            unbindCursor();
            applyRows(false);
            setupRanges(true);
        }

        function bindCursor() {
            if (data) {
                var rows = discoverRows();
                if (cursorProperty) {
                    boundToCursor = Bound.observePath(rows, cursorProperty, function (anEvent) {
                        enqueueServiceColumnsRedraw();
                    });
                }
            }
        }

        function unbindCursor() {
            if (boundToCursor) {
                boundToCursor.removeHandler();
                boundToCursor = null;
            }
        }

        Object.defineProperty(this, 'data', {
            get: function () {
                return data;
            },
            set: function (aValue) {
                if (data !== aValue) {
                    unbind();
                    data = aValue;
                    bind();
                }
            }
        });

        Object.defineProperty(this, 'field', {
            get: function () {
                return field;
            },
            set: function (aValue) {
                if (field !== aValue) {
                    unbind();
                    field = aValue;
                    bind();
                }
            }
        });

        Object.defineProperty(this, 'parentField', {
            get: function () {
                return parentField;
            },
            set: function (aValue) {
                if (parentField !== aValue) {
                    var wasTree = isTreeConfigured();
                    parentField = aValue;
                    var isTree = isTreeConfigured();
                    if (wasTree !== isTree) {
                        expandedRows.clear();
                        applyRows(false);
                        setupRanges(true);
                    }
                }
            }
        });

        Object.defineProperty(this, 'indent', {
            get: function () {
                return indent;
            },
            set: function (aValue) {
                if (indent !== aValue) {
                    indent = aValue;
                    applyRows(true);
                }
            }
        });

        Object.defineProperty(this, 'childrenField', {
            get: function () {
                return childrenField;
            },
            set: function (aValue) {
                if (childrenField !== aValue) {
                    var wasTree = isTreeConfigured();
                    childrenField = aValue;
                    var isTree = isTreeConfigured();
                    if (wasTree !== isTree) {
                        expandedRows.clear();
                        applyRows(false);
                        setupRanges(true);
                    }
                }
            }
        });

        function isTreeConfigured() {
            return !!parentField && !!childrenField;
        }

        function setupRanges(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            frozenContainer.style.display = frozenRows > 0 ? '' : 'none';
            frozenLeft.setDataRange(0, sortedRows.length >= frozenRows ? frozenRows : sortedRows.length, needRedraw);
            frozenRight.setDataRange(0, sortedRows.length >= frozenRows ? frozenRows : sortedRows.length, needRedraw);

            bodyContainer.style.display = sortedRows.length - frozenRows > 0 ? '' : 'none';
            bodyLeft.setDataRange(frozenRows, sortedRows.length, needRedraw);
            bodyRight.setDataRange(frozenRows, sortedRows.length, needRedraw);
            bodyRight.onDrawBody = function (rendering) {
                bodyLeft.setDataRange(rendering.dataStart, rendering.dataEnd);
                bodyLeft.element.style.marginTop = -rendering.scrolled + 'px';
            };
        }

        function updateSectionsWidth() {
            var leftColumnsWidth = 0;
            for (var c = 0; c < headerLeft.columnsCount; c++) {
                var lcolumn = headerLeft.getColumn(c);
                if (lcolumn.visible) {
                    leftColumnsWidth += lcolumn.width + lcolumn.padding;
                }
            }
            [
                headerLeft,
                frozenLeft,
                bodyLeft,
                footerLeft
            ].forEach(function (section) {
                section.element.style.width = leftColumnsWidth + 'px';
            });
            var rightColumnsWidth = 0;
            for (var c = 0; c < headerRight.columnsCount; c++) {
                var rcolumn = headerRight.getColumn(c);
                if (rcolumn.visible) {
                    rightColumnsWidth += rcolumn.width + rcolumn.padding;
                }
            }
            [
                headerRight,
                frozenRight,
                bodyRight,
                footerRight
            ].forEach(function (section) {
                section.element.style.width = rightColumnsWidth + 'px';
            });
        }
        Object.defineProperty(this, 'updateSectionsWidth', {
            get: function () {
                return updateSectionsWidth;
            }
        });

        var treeIndicatorColumn;
        function lookupTreeColumn() {
            var found = null;
            if (isTreeConfigured()) {
                var c = 0;
                while (c < getColumnsCount()) {
                    var column = getColumn(c);
                    if (column instanceof MarkerServiceColumn || column instanceof RadioButtonServiceColumn
                            || column instanceof CheckBoxServiceColumn) {
                        c++;
                    } else {
                        found = column;
                        break;
                    }
                }
            }

            if (treeIndicatorColumn !== found) {
                if (treeIndicatorColumn) {
                    treeIndicatorColumn.padding = 0;
                }
                treeIndicatorColumn = found;
            }
        }
        Object.defineProperty(this, 'treeIndicatorColumn', {
            get: function () {
                return treeIndicatorColumn;
            }
        });


        function clearColumnsNodes(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            function clearHeaders(forest) {
                forest.forEach(function (node) {
                    node.column.grid = null;
                    node.column.headers.splice(0, node.column.headers.length);
                    clearHeaders(node.children);
                });
            }
            clearHeaders(columnNodes);
            columnsFacade = [];
            sortedColumns = [];
            for (var i = getColumnsCount() - 1; i >= 0; i--) {
                var toDel = getColumn(i);
                var column = toDel;
                if (column === treeIndicatorColumn) {
                    treeIndicatorColumn.padding = 0;
                    treeIndicatorColumn = null;
                }
                column.headers.splice(0, column.headers.length);
            }
            headerLeft.clearColumnsAndHeader(needRedraw);
            headerRight.clearColumnsAndHeader(needRedraw);
            frozenLeft.clearColumnsAndHeader(needRedraw);
            frozenRight.clearColumnsAndHeader(needRedraw);
            bodyLeft.clearColumnsAndHeader(needRedraw);
            bodyRight.clearColumnsAndHeader(needRedraw);
            footerLeft.clearColumnsAndHeader(needRedraw);
            footerRight.clearColumnsAndHeader(needRedraw);
        }
        Object.defineProperty(this, 'clearColumnsNodes', {
            get: function () {
                return clearColumnsNodes;
            }
        });

        function applyColumnsNodes() {
            clearColumnsNodes(false);

            function injectHeaders(forest) {
                forest.forEach(function (node) {
                    node.column.grid = self;
                    node.column.headers.push(node.view);
                    injectHeaders(node.children);
                });
            }

            var maxDepth = HeaderAnalyzer.analyzeDepth(columnNodes);
            leftHeader = HeaderSplitter.split(columnNodes, 0, frozenColumns - 1);
            injectHeaders(leftHeader);
            HeaderAnalyzer.analyzeLeaves(leftHeader);
            headerLeft.setHeaderNodes(leftHeader, maxDepth, false);
            var rightHeader = HeaderSplitter.split(columnNodes, frozenColumns, Infinity);
            injectHeaders(rightHeader);
            HeaderAnalyzer.analyzeLeaves(rightHeader);
            headerRight.setHeaderNodes(rightHeader, maxDepth, false);

            var leftLeaves = HeaderAnalyzer.toLeaves(leftHeader);
            var rightLeaves = HeaderAnalyzer.toLeaves(rightHeader);
            leftLeaves.forEach(function (leaf) { // linear list of column header nodes
                addColumnToSections(leaf.column);
            });
            rightLeaves.forEach(function (leaf) { // linear list of column header nodes
                addColumnToSections(leaf.column);
            });
            [
                headerLeftContainer,
                frozenLeftContainer,
                bodyLeftContainer,
                footerLeftContainer
            ].forEach(function (section) {
                section.style.display = frozenColumns > 0 ? '' : 'none';
            });
            lookupTreeColumn();
            updateSectionsWidth();
            redraw();
        }

        Object.defineProperty(this, 'header', {
            get: function () {
                return columnNodes;
            },
            set: function (aHeader) {
                if (columnNodes !== aHeader) {
                    columnNodes = aHeader;
                    applyColumnsNodes();
                }
            }
        });

        Object.defineProperty(this, 'applyColumnsNodes', {
            get: function () {
                return applyColumnsNodes;
            }
        });

        function closeColumnMenu() {
            if (columnsMenu) {
                columnsMenu.close();
                columnsMenu = null;
            }
        }

        function removeColumnNode(aNode) {
            closeColumnMenu();
            var nodeIndex = columnNodes.indexOf(aNode);
            if (nodeIndex !== -1) {
                removeColumnNodeAt(nodeIndex);
            } else {
                return false;
            }
        }
        Object.defineProperty(this, 'removeColumnNode', {
            get: function () {
                return removeColumnNode;
            }
        });

        function removeColumnNodeAt(nodeIndex) {
            closeColumnMenu();
            if (nodeIndex >= 0 && nodeIndex < columnNodes.length) {
                var node = columnNodes[nodeIndex];
                columnNodes.splice(nodeIndex, 1);
                if (treeIndicatorColumn === node.column) {
                    treeIndicatorColumn.padding = 0;
                    treeIndicatorColumn = null;
                }
                applyColumnsNodes();
                return true;
            } else {
                return false;
            }
        }
        Object.defineProperty(this, 'removeColumnNodeAt', {
            get: function () {
                return removeColumnNode;
            }
        });


        function addColumnNode(aNode) {
            closeColumnMenu();
            columnNodes.push(aNode);
            applyColumnsNodes();
        }

        Object.defineProperty(this, 'addColumnNode', {
            get: function () {
                return addColumnNode;
            }
        });

        function insertColumnNode(aIndex, aNode) {
            closeColumnMenu();
            columnNodes.splice(aIndex, 0, aNode);
            applyColumnsNodes();
        }

        Object.defineProperty(this, 'insertColumnNode', {
            get: function () {
                return insertColumnNode;
            }
        });

        Object.defineProperty(this, 'columnNodesCount', {
            get: function () {
                return columnNodes.length;
            }
        });

        function getColumnNode(nodeIndex) {
            if (nodeIndex >= 0 && nodeIndex < columnNodes.length) {
                return columnNodes[nodeIndex];
            }
        }

        Object.defineProperty(this, 'getColumnNode', {
            get: function () {
                return getColumnNode;
            }
        });

        function insertBeforeColumnNode(subject, insertBefore) {
            closeColumnMenu();
            if (subject && insertBefore && subject.parent === insertBefore.parent) {
                var neighbours = subject.parent ? subject.parent.children : columnNodes;
                var neighbourIndex = neighbours.indexOf(subject);
                neighbours.splice(neighbourIndex, 1);
                var insertAt = neighbours.indexOf(insertBefore);
                neighbours.splice(insertAt, 0, subject);
                applyColumnsNodes();
            }
        }
        Object.defineProperty(this, 'insertBeforeColumnNode', {
            get: function () {
                return insertBeforeColumnNode;
            }
        });

        function insertAfterColumnNode(subject, insertAfter) {
            closeColumnMenu();
            if (subject && insertAfter && subject.parent === insertAfter.parent) {
                var neighbours = subject.parent ? subject.parent.children : columnNodes;
                var neighbourIndex = neighbours.indexOf(subject);
                neighbours.splice(neighbourIndex, 1);
                var insertAt = neighbours.indexOf(insertAfter);
                neighbours.splice(insertAt + 1, 0, subject);
                applyColumnsNodes();
            }
        }
        Object.defineProperty(this, 'insertAfterColumnNode', {
            get: function () {
                return insertAfterColumnNode;
            }
        });

        function addColumnToSections(column) {
            columnsFacade.push(column);
            if (headerLeft.columnsCount < frozenColumns) {
                headerLeft.addColumn(column, false);
                frozenLeft.addColumn(column, false);
                bodyLeft.addColumn(column, false);
                footerLeft.addColumn(column, false);
            } else {
                headerRight.addColumn(column, false);
                frozenRight.addColumn(column, false);
                bodyRight.addColumn(column, false);
                footerRight.addColumn(column, false);
            }
        }

        function redrawRow(index) {
            frozenLeft.redrawRow(index);
            frozenRight.redrawRow(index);
            bodyLeft.redrawRow(index);
            bodyRight.redrawRow(index);
        }
        Object.defineProperty(this, 'redrawRow', {
            get: function () {
                return redrawRow;
            }
        });

        function redraw() {
            headerLeft.redraw();
            headerRight.redraw();
            frozenLeft.redraw();
            frozenRight.redraw();
            bodyLeft.redraw();
            bodyRight.redraw();
            footerLeft.redraw();
            footerRight.redraw();
        }
        Object.defineProperty(this, 'redraw', {
            get: function () {
                return redraw;
            }
        });

        function redrawHeaders() {
            headerLeft.redrawHeaders();
            headerRight.redrawHeaders();
        }
        Object.defineProperty(this, 'redrawHeaders', {
            get: function () {
                return redrawHeaders;
            }
        });

        function redrawFooters() {
            footerLeft.redrawFooters();
            footerRight.redrawFooters();
        }
        Object.defineProperty(this, 'redrawFooters', {
            get: function () {
                return redrawFooters;
            }
        });

        function getColumnsCount() {
            return (headerLeft ? headerLeft.columnsCount : 0)
                    + (headerRight ? headerRight.columnsCount : 0);
        }
        Object.defineProperty(this, 'columnsCount', {
            get: function () {
                return getColumnsCount();
            }
        });

        function getColumn(aIndex) {
            if (aIndex >= 0 && aIndex < getColumnsCount()) {
                return aIndex >= 0 && aIndex < headerLeft.columnsCount ? headerLeft.getColumn(aIndex)
                        : headerRight.getColumn(aIndex - headerLeft.columnsCount);
            } else {
                return null;
            }
        }
        Object.defineProperty(this, 'getColumn', {
            get: function () {
                return getColumn;
            }
        });

        function getCell(aRow, aCol) {
            var targetSection;
            if (aRow < frozenRows) {
                if (aCol < frozenColumns) {
                    targetSection = frozenLeft;
                } else {
                    targetSection = frozenRight;
                }
            } else {
                if (aCol < frozenColumns) {
                    targetSection = bodyLeft;
                } else {
                    targetSection = bodyRight;
                }
            }
            return targetSection.getViewCell(aRow, aCol);
        }
        Object.defineProperty(this, 'getCell', {
            get: function () {
                return getCell;
            }
        });

        function focusCell(aRow, aCol) {
            var targetSection;
            if (aRow < frozenRows) {
                if (aCol < frozenColumns) {
                    targetSection = frozenLeft;
                } else {
                    targetSection = frozenRight;
                }
            } else {
                if (aCol < frozenColumns) {
                    targetSection = bodyLeft;
                } else {
                    targetSection = bodyRight;
                }
            }
            targetSection.focusCell(aRow, aCol);
        }
        Object.defineProperty(this, 'focusCell', {
            get: function () {
                return focusCell;
            }
        });

        function sort() {
            applyRows(true);
        }
        Object.defineProperty(this, 'sort', {
            get: function () {
                return sort;
            }
        });

        function addSortedColumn(column) {
            var idx = sortedColumns.indexOf(column);
            if (idx === -1) {
                sortedColumns.push(column);
            }
            sort();
        }
        Object.defineProperty(this, 'addSortedColumn', {
            get: function () {
                return addSortedColumn;
            }
        });

        function removeSortedColumn(column) {
            var idx = sortedColumns.indexOf(column);
            if (idx !== -1) {
                sortedColumns.splice(idx, 1);
            }
            sort();
        }
        Object.defineProperty(this, 'removeSortedColumn', {
            get: function () {
                return removeSortedColumn;
            }
        });

        function unsort(apply) {
            if (arguments.length < 1)
                apply = true;
            sortedColumns = [];
            columnsFacade.forEach(function (column) {
                column.unsort(false);
            });
            if (apply) {
                applyRows(true);
            }
        }
        Object.defineProperty(this, 'unsort', {
            get: function () {
                return unsort;
            }
        });

        function regenerateFront() {
            depths.clear();
            var rows = discoverRows();
            if (isTreeConfigured()) {
                sortedRows = [];
                var roots = getChildrenOf(null);
                var stack = [];
                var parents = [null];
                var maxPathLength = 1;
                Array.prototype.unshift.apply(stack, roots);
                while (stack.length > 0) {
                    var item = stack.shift();
                    if (parents[parents.length - 1] !== getParentOf(item)) {
                        parents.pop();
                    }
                    depths.set(item, parents.length);
                    sortedRows.push(item);
                    if (expandedRows.has(item)) {
                        var children = getChildrenOf(item);
                        if (children.length > 0) {
                            parents.push(item);
                            if (maxPathLength < parents.length) {
                                maxPathLength = parents.length;
                            }
                            Array.prototype.unshift.apply(stack, children);
                        }
                    }
                }
                treeIndicatorColumn.padding = maxPathLength * indent;
            } else {
                sortedRows = rows.slice(0, rows.length);
            }
        }

        function sortFront() {
            if (sortedColumns.length > 0) {
                sortedRows.sort(function (o1, o2) {
                    if (isTreeConfigured() && getParentOf(o1) !== getParentOf(o2)) {
                        var path1 = buildPathTo(o1);
                        var path2 = buildPathTo(o2);
                        if (path2.indexOf(o1) !== -1) {
                            // o1 is parent of o2
                            return -1;
                        }
                        if (path1.indexOf(o2) !== -1) {
                            // o2 is parent of o1
                            return 1;
                        }
                        for (var p = 0; p < Math.min(path1.length, path2.length); p++) {
                            if (path1[p] !== path2[p]) {
                                o1 = path1[p];
                                o2 = path2[p];
                                break;
                            }
                        }
                    }
                    var res = 0;
                    var index = 0;
                    while (res === 0 && index < sortedColumns.length) {
                        var column = sortedColumns[index++];
                        if (column.comparator) {
                            res = column.comparator.compare(o1, o2);
                        }
                    }
                    return res;
                });
            }
            [
                frozenLeft, frozenRight,
                bodyLeft, bodyRight
            ].forEach(function (section) {
                section.data = sortedRows;
            });

            fireRowsSort();
        }

        function applyRows(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            lookupTreeColumn();
            regenerateFront();
            sortFront();
            if (needRedraw) {
                redraw();
            }
        }

        var tabIndex = 0;

        function calcFocusedElement() {
            var focusedEelement = bodyRight.getKeyboardSelectedElement();
            if (!focusedEelement) {
                focusedEelement = bodyLeft.getKeyboardSelectedElement();
            }
            if (!focusedEelement) {
                focusedEelement = frozenLeft.getKeyboardSelectedElement();
            }
            if (!focusedEelement) {
                focusedEelement = frozenRight.getKeyboardSelectedElement();
            }
            if (!focusedEelement) {
                focusedEelement = getElement();
            }
            return focusedEelement;
        }

        Object.defineProperty(this, 'tabIndex', {
            get: function () {
                return tabIndex;
            },
            set: function (index) {
                tabIndex = index;
                focusedElement = calcFocusedElement();
                if (focusedElement)
                    focusedElement.tabIndex = tabIndex;
            }
        });

        function setFocus(focused) {
            var focusedElement = calcFocusedElement();
            focusedElement.tabIndex = tabIndex;
            if (focused) {
                focusedElement.focus();
            } else {
                focusedElement.blur();
            }
        }

        var expandListeners = new Set();
        function addExpandHandler(h) {
            expandListeners.add(h);
            return {
                removeHandler: function () {
                    expandListeners.delete(h);
                }
            };
        }
        Object.defineProperty(this, 'addExpandHandler', {
            get: function () {
                return addExpandHandler;
            }
        });

        function fireExpanded(anElement) {
            fireRowsSort();
            var event = new ItemEvent(self, anElement);
            expandListeners.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onExpand;
        var expandedReg;
        Object.defineProperty(this, 'onExpand', {
            get: function () {
                return onExpand;
            },
            set: function (aValue) {
                if (onExpand !== aValue) {
                    if (expandedReg) {
                        expandedReg.removeHandler();
                        expandedReg = null;
                    }
                    onExpand = aValue;
                    if (onExpand) {
                        expandedReg = addExpandHandler(function (event) {
                            if (onExpand) {
                                onExpand(event);
                            }
                        });
                    }
                }
            }
        });

        var collapseHandlers = new Set();
        function addCollapseHandler(h) {
            collapseHandlers.add(h);
            return {
                removeHandler: function () {
                    collapseHandlers.delete(h);
                }
            };
        }
        Object.defineProperty(this, 'addCollapseHandler', {
            get: function () {
                return addCollapseHandler;
            }
        });

        function fireCollapsed(anElement) {
            fireRowsSort();
            var event = new ItemEvent(self, anElement);
            collapseHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var onCollapse;
        var collapseReg;
        Object.defineProperty(this, 'onCollapse', {
            get: function () {
                return onCollapse;
            },
            set: function (aValue) {
                if (onCollapse !== aValue) {
                    if (collapseReg) {
                        collapseReg.removeHandler();
                        collapseReg = null;
                    }
                    onCollapse = aValue;
                    if (onCollapse) {
                        collapseReg = addCollapseHandler(function (event) {
                            if (onCollapse) {
                                onCollapse(event);
                            }
                        });
                    }
                }
            }
        });

        var sortHandlers = new Set();
        function addSortHandler(handler) {
            sortHandlers.add(handler);
            return  {
                removeHandler: function () {
                    sortHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addSortHandler', {
            get: function () {
                return addSortHandler;
            }
        });

        function fireRowsSort() {
            var event = new SortEvent(this);
            sortHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
            [
                frozenLeft, frozenRight,
                bodyLeft, bodyRight
            ].forEach(function (section) {
                section.data = sortedRows;
            });
        }

        var onSort;
        var sortedReg;
        Object.defineProperty(this, 'onSort', {
            get: function () {
                return onSort;
            },
            set: function (aValue) {
                if (onSort !== aValue) {
                    if (sortedReg) {
                        sortedReg.removeHandler();
                        sortedReg = null;
                    }
                    onSort = aValue;
                    if (onSort) {
                        sortedReg = addSortHandler(function (event) {
                            if (onSort) {
                                onSort(event);
                            }
                        });
                    }
                }
            }
        });

        var selectionHandlers = new Set();
        function addSelectionHandler(handler) {
            selectionHandlers.add(handler);
            return {
                removeHandler: function () {
                    selectionHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addSelectionHandler', {
            get: function () {
                return addSelectionHandler;
            }
        });

        function fireSelected(item) {
            var event = new ItemEvent(self, item);
            selectionHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var focusHandlers = new Set();
        function addFocusHandler(handler) {
            focusHandlers.add(handler);
            return {
                removeHandler: function () {
                    focusHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addFocusHandler', {
            get: function () {
                return addFocusHandler;
            }
        });

        function fireFocus() {
            var event = new FocusEvent(self);
            focusHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var blurHandlers = new Set();
        function addBlurHandler(handler) {
            blurHandlers.add(handler);
            return {
                removeHandler: function () {
                    blurHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addBlurHandler', {
            get: function () {
                return addBlurHandler;
            }
        });

        function fireBlur() {
            var event = new BlurEvent(self);
            blurHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var keyUpHandlers = new Set();
        function addKeyUpHandler(handler) {
            keyUpHandlers.add(handler);
            return {
                removeHandler: function () {
                    keyUpHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addKeyUpHandler', {
            get: function () {
                return addKeyUpHandler;
            }
        });

        function fireKeyUp(nevent) {
            var event = new KeyEvent(self, nevent);
            keyUpHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var keyDownHandlers = new Set();
        function addKeyDownHandler(handler) {
            keyDownHandlers.add(handler);
            return {
                removeHandler: function () {
                    keyDownHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addKeyDownHandler', {
            get: function () {
                return addKeyDownHandler;
            }
        });

        function fireKeyDown(nevent) {
            var event = new KeyEvent(self, nevent);
            keyDownHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }

        var keyPressHandlers = new Set();
        function addKeyPressHandler(handler) {
            keyPressHandlers.add(handler);
            return {
                removeHandler: function () {
                    keyPressHandlers.delete(handler);
                }
            };
        }
        Object.defineProperty(this, 'addKeyPressHandler', {
            get: function () {
                return addKeyPressHandler;
            }
        });

        function fireKeyPress(nevent) {
            var event = new KeyEvent(this, nevent);
            keyPressHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }
    }
    extend(Grid, Widget);
    return Grid;
});