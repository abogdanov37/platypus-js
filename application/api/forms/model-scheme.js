(function() {
    var javaClass = Java.type("com.eas.client.forms.api.components.model.ModelScheme");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ModelScheme(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ModelScheme ModelScheme
     */
    P.ModelScheme = function () {

        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });

        /**
         * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });

        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });

        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });

        /**
         * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });

        /**
         * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });

        /**
         * Vertical coordinate of the component.
         * @property top
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });

        /**
         * Component resized event handler function.
         * @property onComponentResized
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });

        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });

        /**
         * Height of the component.
         * @property height
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });

        /**
         * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });

        /**
         * Component shown event handler function.
         * @property onComponentShown
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });

        /**
         * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });

        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });

        /**
         * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });

        /**
         * Key released event handler function.
         * @property onKeyReleased
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });

        /**
         * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });

        /**
         * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });

        /**
         * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });

        /**
         * Horizontal coordinate of the component.
         * @property left
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });

        /**
         * The background color of this component.
         * @property background
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });

        /**
         * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets name of this component.
         * @property name
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });

        /**
         * Width of the component.
         * @property width
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        /**
         * The font of this component.
         * @property font
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });

        /**
         * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });

        /**
         * Tries to acquire focus for this component.
         * @method focus
         * @memberOf ModelScheme
         */
        Object.defineProperty(this, "focus", {
            get: function() {
                return function() {
                    var value = delegate.focus();
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();