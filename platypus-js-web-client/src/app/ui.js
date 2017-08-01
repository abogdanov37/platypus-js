define([
    'common-utils/color',
    'common-utils/cursor',
    'common-utils/font',
    './forms/key-codes'], function (
        Color,
        Cursor,
        Font,
        KeyCodes) {
    var global = window;
    var Events = {
        BLUR: 'blur',
        CANPLAYTHROUGH: 'canplaythrough',
        CHANGE: 'change',
        CLICK: 'click',
        CONTEXTMENU: 'contextmenu',
        DBLCLICK: 'dblclick',
        DRAG: 'drag',
        DRAGEND: 'dragend',
        DRAGENTER: 'dragenter',
        DRAGLEAVE: 'dragleave',
        DRAGOVER: 'dragover',
        DRAGSTART: 'dragstart',
        DROP: 'drop',
        ENDED: 'ended',
        ERROR: 'error',
        FOCUS: 'focus',
        FOCUSIN: 'focusin',
        FOCUSOUT: 'focusout',
        GESTURECHANGE: 'gesturechange',
        GESTUREEND: 'gestureend',
        GESTURESTART: 'gesturestart',
        INPUT: 'input',
        KEYDOWN: 'keydown',
        KEYPRESS: 'keypress',
        KEYUP: 'keyup',
        LOAD: 'load',
        LOADEDMETADATA: 'loadedmetadata',
        LOSECAPTURE: 'losecapture',
        MOUSEDOWN: 'mousedown',
        MOUSEMOVE: 'mousemove',
        MOUSEOUT: 'mouseout',
        MOUSEOVER: 'mouseover',
        MOUSEUP: 'mouseup',
        MOUSEWHEEL: 'mousewheel',
        PROGRESS: 'progress',
        SCROLL: 'scroll',
        TOUCHCANCEL: 'touchcancel',
        TOUCHEND: 'touchend',
        TOUCHMOVE: 'touchmove',
        TOUCHSTART: 'touchstart'
    };

    function absoluteLeft(elem) {
        var left = 0;
        var curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent) {
            left -= curr.scrollLeft;
            curr = curr.parentNode;
        }
        while (elem) {
            left += elem.offsetLeft;
            elem = elem.offsetParent;
        }
        return left;
    }

    function absoluteTop(elem) {
        var top = 0;
        var curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent) {
            top -= curr.scrollTop;
            curr = curr.parentNode;
        }
        while (elem) {
            top += elem.offsetTop;
            elem = elem.offsetParent;
        }
        return top;
    }

    function on(element, eventName, handler) {
        element.addEventListener(eventName, handler, false);
        return {
            removeHandler: function () {
                element.removeEventListener(eventName, handler, false);
            }
        };
    }

    function mask(element) {
    }
    function unmask(element) {
    }
    function disabledMask(element) {
    }

    var Orientation = {HORIZONTAL: 0, VERTICAL: 1};
    var VerticalPosition = {CENTER: 0, TOP: 1, BOTTOM: 3};
    var HorizontalPosition = {CENTER: 0, LEFT: 2, RIGHT: 4};
    var FontStyle = Font.Style;
    var ScrollBarPolicy = {ALLWAYS: 32, NEVER: 31, AUTO: 30};

    function selectFile(aCallback, aFileFilter) {
        /*@com.eas.ui.JsUi::*/jsSelectFile(aCallback, aFileFilter);
    }

    function selectColor(aCallback, aOldValue) {
        /*@com.eas.ui.JsUi::*/jsSelectColor(aOldValue ? aOldValue + '' : '', aCallback);
    }

    function Icon() {
    }
    Object.defineProperty(Icon, "load", {
        value: function (aIconName, aOnSuccess, aOnFailure) {
            /*@com.eas.ui.PlatypusImageResource::*/jsLoad(aIconName ? '' + aIconName : null, aOnSuccess, aOnFailure);
        }
    });

    function readWidgetElement(widgetRootElement, aModel) {
        var uiReader = new /*@com.eas.ui.*/DefaultUiReader(widgetRootElement, aModel);
        uiReader.parse();
        var target = {};
        uiReader.widgets.forEach(function (widget) {
            target[widget.name] = widget;
        });
        return target;
    }

    function loadWidgets(aModuleName, aModel) {
        var layoutDoc = /*@com.eas.client.AppClient::*/getFormDocument(aModuleName);
        if (layoutDoc) {
            var rootElement = layoutDoc.documentElement;
            var widgetRootElement = aModuleName ? /*@com.eas.ui.JsUi::*/findLayoutElementByBundleName(rootElement, aModuleName) : rootElement;
            return readWidgetElement(widgetRootElement, aModel);
        } else {
            throw "UI definition for module '" + aModuleName + "' is not found";
        }
    }

    function readWidgets(aContent, aModel) {
        var layoutDoc = new DOMParser().parse(aContent + "");
        var rootElement = layoutDoc.documentElement;
        return readWidgetElement(rootElement, aModel);
    }

    var module = {};
    Object.defineProperty(module, 'loadWidgets', {
        enumerable: true,
        value: loadWidgets
    });
    Object.defineProperty(module, 'readWidgets', {
        enumerable: true,
        value: readWidgets
    });
    Object.defineProperty(module, 'Colors', {
        enumerable: true,
        value: Color
    });
    Object.defineProperty(module, 'Color', {
        enumerable: true,
        value: Color
    });
    Object.defineProperty(module, 'Cursor', {
        enumerable: true,
        value: Cursor
    });
    Object.defineProperty(module, 'Icon', {
        enumerable: true,
        value: Icon
    });
    Object.defineProperty(module, 'Icons', {
        enumerable: true,
        value: Icon
    });
    Object.defineProperty(module, 'Font', {
        enumerable: true,
        value: Font
    });
    Object.defineProperty(module, 'VK_ALT', {
        enumerable: true,
        value: KeyCodes.KEY_ALT
    });
    Object.defineProperty(module, 'VK_BACKSPACE', {
        enumerable: true,
        value: KeyCodes.KEY_BACKSPACE
    });
    Object.defineProperty(module, 'VK_BACKSPACE', {
        enumerable: true,
        value: KeyCodes.KEY_BACKSPACE
    });
    Object.defineProperty(module, 'VK_DELETE', {
        enumerable: true,
        value: KeyCodes.KEY_DELETE
    });
    Object.defineProperty(module, 'VK_DOWN', {
        enumerable: true,
        value: KeyCodes.KEY_DOWN
    });
    Object.defineProperty(module, 'VK_END', {
        enumerable: true,
        value: KeyCodes.KEY_END
    });
    Object.defineProperty(module, 'VK_ENTER', {
        enumerable: true,
        value: KeyCodes.KEY_ENTER
    });
    Object.defineProperty(module, 'VK_ESCAPE', {
        enumerable: true,
        value: KeyCodes.KEY_ESCAPE
    });
    Object.defineProperty(module, 'VK_HOME', {
        enumerable: true,
        value: KeyCodes.KEY_HOME
    });
    Object.defineProperty(module, 'VK_LEFT', {
        enumerable: true,
        value: KeyCodes.KEY_LEFT
    });
    Object.defineProperty(module, 'VK_PAGEDOWN', {
        enumerable: true,
        value: KeyCodes.KEY_PAGEDOWN
    });
    Object.defineProperty(module, 'VK_PAGEUP', {
        enumerable: true,
        value: KeyCodes.KEY_PAGEUP
    });
    Object.defineProperty(module, 'VK_RIGHT', {
        enumerable: true,
        value: KeyCodes.KEY_RIGHT
    });
    Object.defineProperty(module, 'VK_SHIFT', {
        enumerable: true,
        value: KeyCodes.KEY_SHIFT
    });
    Object.defineProperty(module, 'VK_TAB', {
        enumerable: true,
        value: KeyCodes.KEY_TAB
    });
    Object.defineProperty(module, 'VK_UP', {
        enumerable: true,
        value: KeyCodes.KEY_UP
    });
    Object.defineProperty(module, 'selectFile', {
        enumerable: true,
        value: selectFile
    });
    Object.defineProperty(module, 'selectColor', {
        enumerable: true,
        value: selectColor
    });
    Object.defineProperty(module, 'msgBox', {
        enumerable: true,
        value: global.alert
    });
    Object.defineProperty(module, 'error', {
        enumerable: true,
        value: global.alert
    });
    Object.defineProperty(module, 'warn', {
        enumerable: true,
        value: global.alert
    });
    Object.defineProperty(module, 'HorizontalPosition', {
        enumerable: true,
        value: HorizontalPosition
    });
    Object.defineProperty(module, 'VerticalPosition', {
        enumerable: true,
        value: VerticalPosition
    });
    Object.defineProperty(module, 'Orientation', {
        enumerable: true,
        value: Orientation
    });
    Object.defineProperty(module, 'ScrollBarPolicy', {
        enumerable: true,
        value: ScrollBarPolicy
    });
    Object.defineProperty(module, 'FontStyle', {
        enumerable: true,
        value: FontStyle
    });

    Object.defineProperty(module, 'on', {
        get: function () {
            return on;
        }
    });
    Object.defineProperty(module, 'Events', {
        get: function () {
            return Events;
        }
    });
    Object.defineProperty(module, 'absoluteLeft', {
        get: function () {
            return absoluteLeft;
        }
    });
    Object.defineProperty(module, 'absoluteTop', {
        get: function () {
            return absoluteTop;
        }
    });
    Object.defineProperty(module, 'mask', {
        get: function () {
            return mask;
        }
    });
    Object.defineProperty(module, 'unmask', {
        get: function () {
            return unmask;
        }
    });
    Object.defineProperty(module, 'disabledMask', {
        get: function () {
            return disabledMask;
        }
    });
    return module;
});