package com.github.salimkayabasi.dialogspinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogSpinner<T> extends AppCompatSpinner {

  private static final Handler HANDLER = new Handler(Looper.getMainLooper());

  @ColorInt
  public int titleColor = R.attr.md_title_color;
  public String hint = "";
  public String titleString = "";
  public String negativeText = "";
  public String positiveText = "";
  public String noItemErrorText = "";

  private List<T> items = new ArrayList<>();
  private T selectedItem;
  private List<T> selectedItems = new ArrayList<>();
  private SpinnerType type = SpinnerType.SINGLE;

  public DialogSpinner(Context context) {
    super(context);
    initialize(context, null, 0);
  }

  public DialogSpinner(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(context, attrs, 0);
  }

  public DialogSpinner(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize(context, attrs, defStyle);
  }

  private void initialize(final Context context, AttributeSet attrs, int defStyle) {
    if (attrs != null) {
      TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.DialogSpinner, defStyle, 0);

      if (types.hasValue(R.styleable.DialogSpinner_dspinner_title)) {
        titleString = types.getString(R.styleable.DialogSpinner_dspinner_title);
      }
      if (types.hasValue(R.styleable.DialogSpinner_dspinner_title_color)) {
        titleColor = types.getColor(R.styleable.DialogSpinner_dspinner_title_color, titleColor);
      }

      if (types.hasValue(R.styleable.DialogSpinner_dspinner_negative_text)) {
        negativeText = types.getString(R.styleable.DialogSpinner_dspinner_negative_text);
      }

      if (types.hasValue(R.styleable.DialogSpinner_dspinner_positive_text)) {
        positiveText = types.getString(R.styleable.DialogSpinner_dspinner_positive_text);
      }

      if (types.hasValue(R.styleable.DialogSpinner_dspinner_hint)) {
        hint = types.getString(R.styleable.DialogSpinner_dspinner_hint);
      } else if (!TextUtils.isEmpty(titleString)) {
        hint = titleString;
      } else {
        hint = getContext().getString(R.string.dspinner_please_select);
      }

      if (types.hasValue(R.styleable.DialogSpinner_dspinner_no_items_error_text)) {
        noItemErrorText = types.getString(R.styleable.DialogSpinner_dspinner_no_items_error_text);
      }

      if (types.hasValue(R.styleable.DialogSpinner_dspinner_type)) {
        type
            = SpinnerType.valueByString(types.getString(R.styleable
            .DialogSpinner_dspinner_type));
      }

      types.recycle();
    }

    setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
          event.setAction(MotionEvent.ACTION_CANCEL);
          if (items != null && !items.isEmpty()) {
            showDialog(context);
          }
        }
        return true;
      }
    });
  }

  public List<T> getItems() {
    return items;
  }

  public void setItems(List<T> items) {
    this.items = items;
    clear();
  }

  @Override
  public T getSelectedItem() {
    return selectedItem;
  }

  public void setSelectedItem(T selected) {
    if ((type == SpinnerType.SINGLE || type == SpinnerType.RADIO)
        && selected != null
        && items.contains(selected)) {
      selectedItem = selected;
      updateAdapter(selectedItem.toString());
    } else {
      clear();
    }
  }

  public List<T> getSelectedItems() {
    return selectedItems;
  }

  public void setSelectedItems(List<T> selectedItems) {
    if (type == SpinnerType.CHECKBOX && selectedItems.size() > 0) {
      this.selectedItems = selectedItems;
      List<String> list = new ArrayList<>();
      for (T item : selectedItems) {
        list.add(item.toString());
      }
      updateAdapter(TextUtils.join(",", list));
    } else {
      clear();
    }
  }

  private void setError(View v, int errorSourceId) {
    setError(v, getContext().getString(errorSourceId));
  }

  public void setError() {
    setError(R.string.dspinner_required_field);
  }

  public void setError(int errorSourceId) {
    View v = getSelectedView();
    if (v != null) {
      setError(v, errorSourceId);
    }
  }

  public void setError(View v, String text) {
    ((TextView) v).setError(text);
  }

  private void showDialog(Context context) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
    if (items != null && items.size() > 0) {
      builder.items(getItemDescriptions(items));
    } else if (!TextUtils.isEmpty(noItemErrorText)) {
      builder.content(noItemErrorText);
      if (!TextUtils.isEmpty(titleString)) {
        builder.title(titleString).titleColor(titleColor);
      }
      if (!TextUtils.isEmpty(positiveText)) {
        builder.positiveText(positiveText);
      }
      builder.show();
      return;
    }
    if (type == SpinnerType.SINGLE) {
      builder
          .itemsCallback(
              new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog d,
                                        View view, int i,
                                        CharSequence t) {
                  setSelectedItem(items.get(i));
                }
              });
    } else if (type == SpinnerType.RADIO) {
      builder
          .itemsCallbackSingleChoice(
              getSelectedItemIndex(),
              new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog d,
                                           View view, int i,
                                           CharSequence t) {
                  setSelectedItem(items.get(i));
                  return true;
                }
              });
    } else if (type == SpinnerType.CHECKBOX) {
      builder
          .itemsCallbackMultiChoice(getSelectedItemsIndex(),
              new MaterialDialog.ListCallbackMultiChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog,
                                           Integer[] which,
                                           CharSequence[] text) {
                  selectedItems.clear();
                  for (Integer choice : which) {
                    selectedItems.add(items.get(choice));
                  }
                  setSelectedItems(selectedItems);
                  return true;
                }
              });
    }
    builder.callback(new MaterialDialog.ButtonCallback() {
      @Override
      public void onNegative(MaterialDialog dialog) {
        clear();
      }

      @Override
      public void onPositive(MaterialDialog dialog) {
        dialog.hide();
      }
    });
    if (!TextUtils.isEmpty(titleString)) {
      builder.title(titleString).titleColor(titleColor);
    }
    if (!TextUtils.isEmpty(negativeText)) {
      builder.negativeText(negativeText);
    }
    if (!TextUtils.isEmpty(positiveText)) {
      builder.positiveText(positiveText);
    } else if (type == SpinnerType.RADIO) {
      builder.alwaysCallSingleChoiceCallback();
    }
    builder.show();
  }

  private int getSelectedItemIndex() {
    if (selectedItem == null) {
      return -1;
    }
    return items.indexOf(selectedItem);
  }

  private Integer[] getSelectedItemsIndex() {
    Integer[] result = new Integer[selectedItems.size()];
    for (int i = 0; i < selectedItems.size(); i++) {
      result[i] = items.indexOf(selectedItems.get(i));
    }
    Arrays.sort(result);
    return result;
  }

  private String[] getItemDescriptions(List list) {
    if (list == null) {
      return new String[0];
    }
    final String[] choices = new String[list.size()];
    for (int i = 0; i < list.size(); i++) {
      choices[i] = list.get(i).toString();
    }
    return choices;
  }

  public void clear() {
    this.selectedItem = null;
    this.selectedItems.clear();
    updateAdapter(hint);
  }

  public void hide() {
    setVisibility(View.GONE);
    reset();
  }

  public void reset() {
    clear();
    items.clear();
  }

  private void updateAdapter(final List<String> list) {
    HANDLER.post(new Runnable() {
      public void run() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
            android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        superSetAdapter(adapter);
      }
    });
  }

  private void updateAdapter(String description) {
    List<String> list = new ArrayList<>();
    list.add(description);
    updateAdapter(list);
  }

  private void superSetAdapter(ArrayAdapter adapter) {
    super.setAdapter(adapter);
  }

  public enum SpinnerType {
    SINGLE(0), CHECKBOX(1), RADIO(2);

    private final int id;

    private SpinnerType(final int id) {
      this.id = id;
    }

    public int getId() {
      return id;
    }

    public static SpinnerType valueOf(int id) {
      for (SpinnerType m : values()) {
        if (m.getId() == id) {
          return m;
        }
      }
      return SINGLE;
    }

    public static SpinnerType valueByString(String id) {
      return valueOf(Integer.parseInt(id));
    }
  }

}