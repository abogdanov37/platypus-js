define([
    '../../ui',
    '../../invoke',
    '../key-codes'
], function (
        Ui,
        Invoke,
        KeyCodes
        ) {

    var JS_ROW_NAME = 'js-row';

    function Section(grid, dynamicCellsClassName,
            dynamicRowsClassName,
            dynamicHeaderCellsClassName,
            dynamicHeaderRowsClassName,
            dynamicOddRowsClassName,
            dynamicEvenRowsClassName) {
        var self = this;

        var table = document.createElement('table');
        table.className = 'p-grid-section';
        var colgroup = document.createElement('colgroup');
        var rowsHeight = 30;
        var headerNodes = [];
        var headerMaxDepth = 0;
        var keyboardSelectedElement = null;
        var draggableRows = false;
        var keyboardSelectedRow = -1;
        var keyboardSelectedColumn = -1;
        var columns = [];
        var data = []; // Already sorted data
        var dataRangeStart = 0; // Inclusive
        var dataRangeEnd = 0; // Exclusive
        var renderedRangeStart = 0; // Inclusive
        var renderedRangeEnd = 0; // Exclusive
        var renderingThrottle = 0; // No throttling
        var renderingPadding = 0; // No padding
        var onDrawBody = null;

        var thead;
        recreateHead();
        var tbody;
        recreateBody();
        var tfoot;
        recreateFoot();
        var bodyFiller = document.createElement('div');
        bodyFiller.className = 'p-grid-body-filler';

        Ui.on(table, Ui.Events.KEYDOWN, function (event) {
            var oldRow = self.keyboardSelectedRow;
            var oldColumn = self.keyboardSelectedColumn;
            var keyCode = event.keyCode;
            if (keyCode === KeyCodes.KEY_LEFT) {
            } else if (keyCode === KeyCodes.KEY_RIGHT) {
            } else if (keyCode === KeyCodes.KEY_UP) {
            } else if (keyCode === KeyCodes.KEY_DOWN) {
            } else if (keyCode === KeyCodes.KEY_PAGEUP) {
            } else if (keyCode === KeyCodes.KEY_PAGEDOWN) {
            } else if (keyCode === KeyCodes.KEY_HOME) {
            } else if (keyCode === KeyCodes.KEY_END) {
            }
        });

        Object.defineProperty(this, 'element', {
            get: function () {
                return table;
            }
        });
        Object.defineProperty(this, 'data', {
            get: function () {
                return data;
            },
            set: function (aValue) {
                if (data !== aValue) {
                    data = aValue;
                }
            }
        });
        Object.defineProperty(this, 'rowsHeight', {
            get: function () {
                return rowsHeight;
            },
            set: function (aValue) {
                if (rowsHeight !== aValue) {
                    rowsHeight = aValue;
                    redrawBody();
                }
            }
        });
        Object.defineProperty(this, 'renderingThrottle', {
            get: function () {
                return renderingThrottle;
            },
            set: function (aValue) {
                renderingThrottle = aValue;
            }
        });
        Object.defineProperty(this, 'renderingPadding', {
            get: function () {
                return renderingPadding;
            },
            set: function (aValue) {
                renderingPadding = aValue;
            }
        });
        Object.defineProperty(this, 'onDrawBody', {
            get: function () {
                return onDrawBody;
            },
            set: function (aValue) {
                onDrawBody = aValue;
            }
        });
        Object.defineProperty(this, 'draggableRows', {
            get: function () {
                return draggableRows;
            },
            set: function (aValue) {
                if (draggableRows !== aValue) {
                    draggableRows = aValue;
                }
            }
        });
        Object.defineProperty(this, 'keyboardSelectedColumn', {
            get: function () {
                return keyboardSelectedColumn;
            },
            set: function (aValue) {
                if (keyboardSelectedColumn !== aValue) {
                    keyboardSelectedColumn = aValue;
                }
            }
        });
        Object.defineProperty(this, 'keyboardSelectedRow', {
            get: function () {
                return keyboardSelectedRow;
            },
            set: function (aValue) {
                if (keyboardSelectedRow !== aValue) {
                    keyboardSelectedRow = aValue;
                }
            }
        });

        function insertColumn(index, aColumn, needRedraw) {
            if (arguments.length < 3)
                needRedraw = true;
            if (index >= 0 && index <= columns.length) { // It is all about insertBefore
                if (index < columns.length) { // It is all about insertBefore
                    columns.splice(index, 0, aColumn);
                    var col = colgroup.getChild(index);
                    colgroup.insertBefore(aColumn.addCol(), col);
                } else {
                    columns.push(aColumn);
                    colgroup.appendChild(aColumn.addCol());
                }
                table.parentElement.parentElement.appendChild(aColumn.columnRule);
                if (needRedraw) {
                    redraw();
                }
            }
        }
        Object.defineProperty(this, 'insertColumn', {
            get: function () {
                return insertColumn;
            }
        });
        function addColumn(aColumn, needRedraw) {
            if (arguments.length < 2)
                needRedraw = true;
            insertColumn(columns.length, aColumn, needRedraw);
        }
        Object.defineProperty(this, 'addColumn', {
            get: function () {
                return addColumn;
            }
        });

        function removeColumn(index, needRedraw) {
            if (arguments.length < 2)
                needRedraw = true;
            if (index >= 0 && index < columns.length) {
                var removed = columns.splice(index, 1)[0];
                removed.elements.forEach(function (col) {
                    col.parentElement.removeChild(col);
                });
                removed.elements.splice(0, removed.elements.length);
                if (removed.columnRule.parentElement)
                    removed.columnRule.parentElement.removeChild(removed.columnRule);
                if (needRedraw) {
                    redraw();
                }
                return removed;
            } else {
                return null;
            }
        }
        Object.defineProperty(this, 'removeColumn', {
            get: function () {
                return removeColumn;
            }
        });

        Object.defineProperty(this, 'columnsCount', {
            get: function () {
                return columns.length;
            }
        });

        function getColumn(index) {
            return index >= 0 && index < columns.length ? columns[index] : null;
        }
        Object.defineProperty(this, 'getColumn', {
            get: function () {
                return getColumn;
            }
        });

        function getColumnIndex(column) {
            return columns.indexOf(column);
        }

        Object.defineProperty(this, 'rowsCount', {
            get: function () {
                return tbody.rows.length;
            }
        });

        function getViewCell(aRow, aCol) {
            var viewRows = tbody.rows;
            if (aRow - renderedRangeStart >= 0 && aRow - renderedRangeStart < viewRows.length) {
                var viewRow = viewRows[aRow];
                var cells = viewRow.cells;
                if (aCol >= 0 && aCol < cells.length) {
                    return cells[aCol];
                }
            }
            return null;
        }
        Object.defineProperty(this, 'getViewCell', {
            get: function () {
                return getViewCell;
            }
        });

        function focusCell(aRow, aCol) {
            var cell = getViewCell(aRow, aCol);
            if (cell) {
                keyboardSelectedColumn = aCol;
                keyboardSelectedRow = aRow;
                cell.focus();
            }
        }
        Object.defineProperty(this, 'focusCell', {
            get: function () {
                return focusCell;
            }
        });

        Object.defineProperty(this, 'keyboardSelectedElement', {
            get: function () {
                return keyboardSelectedElement;
            }
        });

        function setHeaderNodes(aHeader, maxDepth, needRedraw) {
            if (arguments.length < 2)
                needRedraw = false;
            headerNodes = aHeader;
            headerMaxDepth = maxDepth;
            if (needRedraw) {
                redrawHeaders();
            }
        }
        Object.defineProperty(this, 'setHeaderNodes', {
            get: function () {
                return setHeaderNodes;
            }
        });

        function clearColumnsAndHeader(needRedraw) {
            if (arguments.length < 1)
                needRedraw = true;
            columns.forEach(function (removed) {
                removed.elements.forEach(function (col) {
                    col.parentElement.removeChild(col);
                });
                removed.elements.splice(0, removed.elements.length);
                if (removed.columnRule.parentElement)
                    removed.columnRule.parentElement.removeChild(removed.columnRule);
            });
            columns = [];
            headerNodes = [];
            if (needRedraw) {
                redraw();
            }
        }
        Object.defineProperty(this, 'clearColumnsAndHeader', {
            get: function () {
                return clearColumnsAndHeader;
            }
        });
        function redraw() {
            redrawHeaders();
            redrawBody();
            redrawFooters();
        }
        Object.defineProperty(this, 'redraw', {
            get: function () {
                return redraw;
            }
        });

        function recreateHead() {
            if (thead && thead.parentElement)
                table.removeChild(thead);
            if (colgroup && colgroup.parentElement)
                table.removeChild(colgroup);
            thead = document.createElement('thead');
            table.insertBefore(thead, tbody);
            table.insertBefore(colgroup, thead);
        }

        function redrawHeaders() {
            recreateHead();
            drawHeaders();
        }
        Object.defineProperty(this, 'redrawHeaders', {
            get: function () {
                return redrawHeaders;
            }
        });

        function drawHeaders() {
            if (columns.length > 0) {
                var r = 0;
                var nextLayer = headerNodes;
                while (nextLayer.length > 0) {
                    nextLayer = drawHeaderRow(nextLayer);
                    r++;
                }
                while (r < headerMaxDepth) {
                    drawHeaderRow([]);
                    r++;
                }
            }
        }

        function drawHeaderRow(layer) {
            var children = [];
            var tr = document.createElement('tr');
            tr.className = 'p-grid-header-row ' + dynamicHeaderRowsClassName;
            layer.forEach(function (node) {
                tr.appendChild(node.view.element);
                if (node.column.comparator) {
                    node.view.element.className = node.column.comparator.ascending ? 'p-grid-header-sorted-asc ' : 'p-grid-header-sorted-desc ';
                } else {
                    node.view.element.className = '';
                }
                node.view.element.className += 'p-grid-header-cell ' + dynamicHeaderCellsClassName; // reassign classes
                node.view.element.classList.add(node.column.styleName);
                Array.prototype.push.apply(children, node.children);
            });
            thead.appendChild(tr);
            return children;
        }

        function recreateBody() {
            if (tbody && tbody.parentElement)
                table.removeChild(tbody);
            tbody = document.createElement('tbody');
            table.insertBefore(tbody, tfoot);
        }

        function drawBody() {
            var rowsCount = dataRangeEnd - dataRangeStart;

            var viewportHeight = table.parentElement.clientHeight;
            var contentHeight = rowsCount * rowsHeight;
            var topPadding = Math.floor(viewportHeight * renderingPadding);
            topPadding = Math.max(topPadding, 0);
            var bottomPadding = Math.floor(viewportHeight * renderingPadding);
            bottomPadding = Math.max(bottomPadding, 0);

            var startY = table.parentElement.scrollTop - topPadding;
            startY = Math.max(startY, 0);
            var startRenderedRow = Math.floor(startY / rowsHeight);

            var endY = table.parentElement.scrollTop + viewportHeight + bottomPadding;
            endY = Math.min(endY, contentHeight - 1);
            var endRenderedRow = Math.floor(endY / rowsHeight) + 1; // Range is right exclusive 
            endRenderedRow = Math.min(endRenderedRow, rowsCount);

            var renderedRowsCount = endRenderedRow - startRenderedRow;
            var fillerHeight = rowsHeight * (rowsCount - renderedRowsCount);
            bodyFiller.style.height = fillerHeight + 'px';
            bodyFiller.style.display = fillerHeight === 0 ? 'none' : '';

            renderRange(startRenderedRow + dataRangeStart, endRenderedRow + dataRangeStart);

            table.style.top = (startRenderedRow * rowsHeight) + 'px';
            if (onDrawBody)
                onDrawBody({
                    dataStart: startRenderedRow + dataRangeStart,
                    dataEnd: endRenderedRow + dataRangeStart,
                    scrolled: table.parentElement.scrollTop - startRenderedRow * rowsHeight
                });
        }

        function redrawBody() {
            renderedRangeStart = renderedRangeEnd = 0;
            drawBody();
        }
        Object.defineProperty(this, 'redrawBody', {
            get: function () {
                return redrawBody;
            }
        });


        function drawBodyPortion(start, end) {
            if (end - start > 0 && columns.length > 0) {
                for (var i = start; i < end; i++) {
                    var dataRow = data[i];
                    var viewRow = document.createElement('tr');
                    if (draggableRows) {
                        viewRow.draggable = draggableRows;
                    }
                    viewRow.className = 'p-grid-row ' + dynamicRowsClassName;
                    if ((i + 1) % 2 === 0) {
                        viewRow.classList.add(dynamicEvenRowsClassName);
                    } else {
                        viewRow.classList.add(dynamicOddRowsClassName);
                    }
                    if (grid.isSelected(dataRow))
                        viewRow.classList.add('p-grid-selected-row');
                    viewRow[JS_ROW_NAME] = dataRow;
                    if (i < renderedRangeStart) {
                        // insertFirst ...
                        if (tbody.firstElementChild)
                            tbody.insertBefore(viewRow, tbody.firstElementChild);
                        else
                            tbody.appendChild(viewRow);
                        // ... insertFirst
                    } else {
                        tbody.appendChild(viewRow);
                    }
                    for (var c = 0; c < columns.length; c++) {
                        var column = columns[c];
                        var viewCell = document.createElement('td');
                        // TODO: Check alignment of the cell
                        // TODO: Check image decoration of the cell and decoration styles
                        viewCell.className = 'p-grid-cell ' + dynamicCellsClassName + ' ' + column.styleName;
                        viewRow.appendChild(viewCell);
                        column.render(i, dataRow, viewCell);
                    }
                }
            }
        }

        var throttle = (function () {
            var watchdog = null;
            function throttle(action, timeout) {
                if (timeout < 1) // ms
                    action();
                else {
                    function invoked() {
                        watchdog = null;
                        action();
                    }
                    if (!watchdog) {
                        Invoke.delayed(timeout, invoked);
                        watchdog = invoked;
                    }
                }
            }
            return throttle;
        }());

        function setDataRange(start, end, needRedraw) {
            if (arguments.length < 3)
                needRedraw = true;
            if (!bodyFiller.parentElement) {
                table.parentElement.appendChild(bodyFiller);
                Ui.on(table.parentElement, Ui.Events.SCROLL, function (event) {
                    throttle(drawBody, renderingThrottle);
                });
            }
            dataRangeStart = start;
            dataRangeEnd = end;
            if (needRedraw) {
                redrawBody();
            }
        }
        Object.defineProperty(this, 'setDataRange', {
            get: function () {
                return setDataRange;
            }
        });

        function renderRange(start, end) {
            if (start !== renderedRangeStart || end !== renderedRangeEnd) {
                if (start >= renderedRangeEnd || end < renderedRangeStart) {
                    recreateBody();
                    renderedRangeStart = start;
                    renderedRangeEnd = end;
                    drawBodyPortion(renderedRangeStart, renderedRangeEnd);
                } else {
                    if (start < renderedRangeStart) {
                        drawBodyPortion(start, renderedRangeStart);
                    } else if (start > renderedRangeStart) {
                        for (var i1 = renderedRangeStart; i1 < start; i1++) {
                            tbody.removeChild(tbody.rows[0]);
                        }
                    }
                    renderedRangeStart = start;
                    if (end < renderedRangeEnd) {
                        for (var i2 = renderedRangeEnd - 1; i2 >= end; i2--) {
                            tbody.removeChild(tbody.rows[tbody.rows.length - 1]);
                        }
                    } else if (end > renderedRangeEnd) {
                        drawBodyPortion(renderedRangeEnd, end);
                    }
                    renderedRangeEnd = end;
                }
            }
        }

        function recreateFoot() {
            if (tfoot && tfoot.parentElement)
                table.removeChild(tfoot);
            tfoot = document.createElement('tfoot');
            table.appendChild(tfoot);
        }

        function redrawFooters() {
            recreateFoot();
            drawFooters();
        }
        Object.defineProperty(this, 'redrawFooters', {
            get: function () {
                return redrawFooters;
            }
        });

        function drawFooters() {
            if (columns.length > 0) {
            }
        }

        /*
         function redrawRow(index) {
         if (index - startRow >= 0 && index - startRow < tbody.rows.length) {
         var viewRow = tbody.rows[index - startRow];
         var dataRow = data[index];
         for (var c = 0; c < columns.length; c++) {
         var column = columns[c];
         var viewCell = viewRow.cells[c];
         viewCell.className = 'p-grid-cell ' + dynamicCellsClassName + ' ' + column.styleName;
         column.render(index, dataRow, viewCell);
         }
         } // if the row is not rendered then we have nothing to do
         }
         
         function redrawColumn(index) {
         if (index >= 0 && index < columns.length) {
         var column = columns[index];
         for (var i = startRow; i < endRow; i++) {
         var dataRow = data[i];
         var viewRow = tbody.rows[i - startRow];
         var viewCell = viewRow.cells[index];
         viewCell.className = 'p-grid-cell ' + dynamicCellsClassName + ' ' + column.styleName;
         while (viewCell.firstElementChild) {
         viewCell.removeChild(viewCell.firstElementChild);
         }
         column.render(i, dataRow, viewCell);
         }
         }
         }
         */
    }
    return Section;
});