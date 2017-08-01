package com.eas.widgets;

import com.eas.bound.JsArrayList;
import java.util.List;

import com.eas.client.IdGenerator;
import com.eas.client.converters.StringValueConverter;
import com.eas.core.Logger;
import com.eas.core.Utils;
import com.eas.ui.CommonResources;
import com.eas.ui.JavaScriptObjectKeyProvider;
import com.eas.ui.PublishedCell;
import com.eas.widgets.WidgetsUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;

public class DropDownListDecoratorField extends ValueDecoratorField {

    protected static final String CUSTOM_DROPDOWN_CLASS = "combo-field-custom-dropdown";
    protected JavaScriptObjectKeyProvider rowKeyProvider = new JavaScriptObjectKeyProvider();
    protected String keyForNullValue = String.valueOf(IdGenerator.genId());
    protected String emptyText;
    protected JavaScriptObject displayList;
    protected String displayField;
    protected HandlerRegistration boundToList;
    protected HandlerRegistration boundToListElements;
    protected Runnable onRedraw;
    protected InputElement nonListMask = Document.get().createTextInputElement();
    protected OptionElement nullOption;

    protected boolean list = true;

    public DropDownListDecoratorField() {
        super(new DropDownList());
        DropDownList box = (DropDownList) decorated;
        box.addItem("...", keyForNullValue, null, "");
        nullOption = box.getItem(0);
        decorated.element.classList.add(CUSTOM_DROPDOWN_CLASS);
        decorated.element.style.setOverflow(Style.Overflow.HIDDEN);
        CommonResources.INSTANCE.commons().ensureInjected();
        box.element.classList.add(CommonResources.INSTANCE.commons().withoutDropdown());
        nonListMask.setReadOnly(true);
        nonListMask.classList.add(CommonResources.INSTANCE.commons().borderSized());
        nonListMask.getStyle().position = 'absolute';
        nonListMask.getStyle().display ='none');
        nonListMask.getStyle().top =0+ 'px');
        nonListMask.getStyle().setLeft(0+ 'px');
        nonListMask.getStyle().width =100 + '%');
        nonListMask.getStyle().height =100 + '%');
        nonListMask.getStyle().setFloat(Style.Float.RIGHT); // Same as decorated within decorator

        getElement().insertFirst(nonListMask);

        btnSelect.classList.add("decorator-select-combo");
        btnClear.classList.add("decorator-clear-combo");
    }

    @Override
    public void setFocus(boolean focused) {
        if (list) {
            super.setFocus(focused);
        } else {
            nonListMask.focus();
        }
    }

    public Runnable getOnRedraw() {
        return onRedraw;
    }

    public void setOnRedraw(Runnable aValue) {
        onRedraw = aValue;
    }

    @Override
    public void setValue(Object value) {
        Object oldValue = getValue();
        super.setValue(value);
        if (oldValue != value) {
            nonListMask.setValue(calcLabel((JavaScriptObject) value));
        }
    }

    @Override
    protected void rebind() {
        super.rebind();
        rebindList();
    }

    protected void rebindList() {
        try {
            DropDownList listBox = (DropDownList) decorated;
            listBox.setSelectedIndex(-1);
            listBox.clear();
            listBox.addItem(calcLabel(null), keyForNullValue, null, "");
            listBox.setSelectedIndex(0);
            if (list) {
                boolean valueMet = false;
                if (displayList != null) {
                    JavaScriptObject value = (JavaScriptObject) getValue();
                    List<JavaScriptObject> jsoList = new JsArrayList(displayList);
                    for (int i = 0; i < jsoList.size(); i++) {
                        JavaScriptObject item = jsoList.get(i);
                        if (item != null) {
                            String itemLabel = calcLabel(item);
                            listBox.addItem(itemLabel, item.hashCode() + "", item, "");
                            if (value == item) {
                                valueMet = true;
                                listBox.setSelectedIndex(listBox.getCount() - 1);
                            }
                        }
                    }
                }
                if (!valueMet) {
                    clearValue();
                }
            }
            nonListMask.setValue(calcLabel((JavaScriptObject) getValue()));
            if (onRedraw != null) {
                onRedraw.run();
            }
        } catch (Exception ex) {
            Logger.severe(ex);
        }
    }

    public String calcLabel(JavaScriptObject aValue) {
        String nullText = emptyText != null && !emptyText.isEmpty() ? emptyText : "...";
        String labelText = aValue != null
                ? new StringValueConverter().convert(Utils.getPathData(aValue, displayField))
                : nullText;
        PublishedCell cell = WidgetsUtils.calcValuedPublishedCell(published, onRender, aValue,
                labelText != null ? labelText : "", null);
        if (cell != null && cell.getDisplay() != null && !cell.getDisplay().isEmpty()) {
            labelText = cell.getDisplay();
        }
        return labelText;
    }

    public String getText() {
        if (list) {
            int selectedOption = ((DropDownList) decorated).getSelectedIndex();
            return ((DropDownList) decorated).getItem(selectedOption).getInnerText();
        } else {
            return nonListMask.getValue()/* value in nonListMask is exactly text */;
        }
    }

    @Override
    public void setText(String text) {
    }

    @Override
    public String getEmptyText() {
        return emptyText;
    }

    @Override
    public void setEmptyText(String aValue) {
        emptyText = aValue;
        nullOption.setInnerText(aValue);
        WidgetsUtils.applyEmptyText(getElement(), emptyText);
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean aValue) {
        if (list != aValue) {
            list = aValue;
            DropDownList listBox = (DropDownList) decorated;
            if (list) {
                listBox.element.classList.add(CUSTOM_DROPDOWN_CLASS);
                listBox.element.style.clearVisibility();
                nonListMask.getStyle().display ='none');
                btnSelect.classList.add("decorator-select-combo");
                btnClear.classList.add("decorator-clear-combo");
                nonListMask.classList.remove("form-control");
            } else {
                listBox.element.classList.remove(CUSTOM_DROPDOWN_CLASS);
                listBox.element.style.setVisibility(Style.Visibility.HIDDEN);
                nonListMask.getStyle().display ='inline-block');
                btnSelect.classList.remove("decorator-select-combo");
                btnClear.classList.remove("decorator-clear-combo");
                nonListMask.classList.add("form-control");
            }
            rebindList();
        }
    }

    public JavaScriptObject getDisplayList() {
        return displayList;
    }

    protected Scheduler.ScheduledCommand changesQueued;

    protected void enqueueListChanges() {
        changesQueued = new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                if (changesQueued == this) {
                    changesQueued = null;
                    rebindList();
                }
            }
        };
        Scheduler.get().scheduleDeferred(changesQueued);
    }

    protected boolean readdQueued;

    private void enqueueListReadd() {
        readdQueued = true;
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                if (readdQueued) {
                    readdQueued = false;
                    if (boundToListElements != null) {
                        boundToListElements.removeHandler();
                        boundToListElements = null;
                    }
                    if (displayList != null) {
                        boundToListElements = Utils.listenElements(displayList, new Utils.OnChangeHandler() {

                            @Override
                            public void onChange(JavaScriptObject anEvent) {
                                enqueueListChanges();
                            }
                        });
                    }
                    rebindList();
                }
            }
        });
    }

    protected void bindList() {
        if (displayList != null) {
            boundToList = Utils.listenPath(displayList, "length", new Utils.OnChangeHandler() {

                @Override
                public void onChange(JavaScriptObject anEvent) {
                    enqueueListReadd();
                }
            });
            enqueueListReadd();
        }
    }

    protected void unbindList() {
        if (boundToList != null) {
            boundToList.removeHandler();
            boundToList = null;
            enqueueListReadd();
        }
    }

    public void setDisplayList(JavaScriptObject aValue) {
        if (displayList != aValue) {
            unbindList();
            displayList = aValue;
            bindList();
        }
    }

    public String getDisplayField() {
        return displayField;
    }

    public void setDisplayField(String aValue) {
        if (displayField != null ? !displayField.equals(aValue) : aValue != null) {
            unbindList();
            displayField = aValue;
            bindList();
        }
    }

    @Override
    public void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(DropDownListDecoratorField aWidget, JavaScriptObject aPublished)/*-{
        var B = @com.eas.core.Predefine::boxing;
            aPublished.redraw = function() {
            aWidget.@com.eas.bound.ModelCombo::rebind()();
        };
        Object.defineProperty(aPublished, "emptyText", {
            get : function() {
               return aWidget.@com.eas.ui.HasEmptyText::getEmptyText()();
            },
            set : function(aValue) {
               aWidget.@com.eas.ui.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
        Object.defineProperty(aPublished, "value", {
            get : function() {
                return B.boxAsJs(aWidget.@com.eas.bound.ModelCombo::getJsValue()());
            },
            set : function(aValue) {
                if (aValue != null) {
                    aWidget.@com.eas.bound.ModelCombo::setJsValue(Ljava/lang/Object;)(B.boxAsJava(aValue));
                } else {
                    aWidget.@com.eas.bound.ModelCombo::setJsValue(Ljava/lang/Object;)(null);
                }
            }
        });
        Object.defineProperty(aPublished, "text", {
            get : function() {
                return aWidget.@com.eas.bound.ModelCombo::getText()();
            }
        });
        Object.defineProperty(aPublished, "displayList", {
            get : function() {
                return aWidget.@com.eas.bound.ModelCombo::getDisplayList()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelCombo::setDisplayList(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "displayField", {
            get : function() {
                return aWidget.@com.eas.bound.ModelCombo::getDisplayField()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelCombo::setDisplayField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(aPublished, "list", {
            get : function() {
                return aWidget.@com.eas.bound.ModelCombo::isList()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelCombo::setList(Z)(false != aValue);
            }
        });
    }-*/;
}