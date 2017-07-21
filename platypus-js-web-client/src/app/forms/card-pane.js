define([
    '../id',
    '../extend',
    './container',
    './item-event'], function (
        Id,
        extend,
        Container,
        SelectionEvent) {
    function Cards(vgap, hgap) {
        Container.call(this);

        var self = this;

        this.element.style.overflow = 'hidden';
        this.element.style.position = 'relative';

        var cards = new Map();

        var visibleWidget;

        Object.defineProperty(this, 'hgap', {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                hgap = aValue;
                self.forEach(function (w) {
                    var we = w.element;
                    we.style.marginLeft = hgap + 'px';
                    we.style.marginRight = hgap + 'px';
                });
            }
        });

        Object.defineProperty(this, 'vgap', {
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                vgap = aValue;
                self.forEach(function (w) {
                    var we = w.element;
                    we.style.marginTop = vgap + 'px';
                    we.style.marginBottom = vgap + 'px';
                });
            }
        });

        var superChild = this.child;
        function child(indexOrCard) {
            if (isNaN(indexOrCard)) {
                var card = indexOrCard;
                return cards.get(card);
            } else {
                var index = +indexOrCard;
                return superChild(index);
            }
        }
        Object.defineProperty(this, 'child', {
            get: function () {
                return child;
            }
        });

        var superAdd = this.add;
        function add(w, indexOrCard) {
            if (w) {
                if (w.parent === self)
                    throw 'A widget is already added to this container';
                var card;
                var index;
                if(arguments.length < 2){
                    card = 'card - ' + Id.generate();
                    index = self.count;
                } else{
                    if (isNaN(indexOrCard)) {
                        card = indexOrCard;
                        index = self.count;
                    } else {
                        card = 'card-' + Id.generate();
                        index = indexOrCard;
                    }
                }
                if (cards.has(card)) {
                    superRemove(cards.get(card));
                }
                superAdd(w, index);
                cards.set(card, w);
                w['-platypus-ui-card'] = card;
                format(w);
                if (!visibleWidget) {
                    showWidget(w);
                }
                return card;
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        var superClear = this.clear;
        function clear() {
            cards.clear();
            superClear();
        }
        Object.defineProperty(this, 'clear', {
            get: function () {
                return clear;
            }
        });

        var superRemove = this.remove;
        function remove(widgetOrIndex) {
            var removed = superRemove(widgetOrIndex);
            if (removed) {
                removeCard(removed);
                if (visibleWidget === removed) {
                    visibleWidget = null;
                }
            }
            return removed;
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        function removeCard(w) {
            if (w && w['-platypus-ui-card']) {
                cards.remove(w['-platypus-ui-card']);
                delete w['-platypus-ui-card'];
            }
        }

        function showWidget(toBeShown) {
            var oldWidget = visibleWidget;
            visibleWidget = toBeShown;

            if (visibleWidget !== oldWidget) {
                visibleWidget.visible = true;
                visibleWidget.element.classList.add('card-shown');
                visibleWidget.element.classList.remove('card-hidden');

                if (oldWidget) {
                    oldWidget.element.classList.add('card-hidden');
                    oldWidget.element.classList.remove('card-shown');
                    oldWidget.visible = false;
                }
                if (oldWidget !== visibleWidget) {
                    fireSelected();
                }
            }
        }

        function fireSelected() {
            var event = new SelectionEvent(self, visibleWidget);
            selectionHandlers.forEach(function (h) {
                h(event);
            });
        }

        function show(aCardName) {
            if (cards.has(aCardName)) {
                var toShow = cards.get(aCardName);
                showWidget(toShow);
            }
        }

        Object.defineProperty(this, 'show', {
            get: function () {
                return show;
            }
        });

        function format(w) {
            var ws = w.element.style;
            ws.position = 'absolute';
            ws.width = '';
            ws.height = '';
            ws.left = '0px';
            ws.right = '0px';
            ws.top = '0px';
            ws.bottom = '0px';
            ws.marginLeft = hgap + 'px';
            ws.marginRight = hgap + 'px';
            ws.marginTop = vgap + 'px';
            ws.marginBottom = vgap + 'px';
            w.visible = false;
        }

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

        var onItemSelected;
        var selectedReg;
        Object.defineProperty(this, 'onItemSelected', {
            get: function () {
                return onItemSelected;
            },
            set: function (aValue) {
                if (onItemSelected !== aValue) {
                    if (selectedReg) {
                        selectedReg.removeHandler();
                        selectedReg = null;
                    }
                    onItemSelected = aValue;
                    if (onItemSelected) {
                        selectedReg = addSelectionHandler(function (event) {
                            if (onItemSelected) {
                                onItemSelected(event);
                            }
                        });
                    }
                }
            }
        });

        function getTop(aWidget) {
            if (aWidget.parent !== self)
                throw "widget should be a child of this container";
            return 0;
        }

        Object.defineProperty(this, 'getTop', {
            get: function () {
                return getTop;
            }
        });

        function getLeft(aWidget) {
            if (aWidget.parent !== self)
                throw "widget should be a child of this container";
            return 0;
        }
        Object.defineProperty(this, 'getLeft', {
            get: function () {
                return getLeft;
            }
        });
    }
    extend(Cards, Container);
    return Cards;
});