package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ShortcutsUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.view.Editor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements ItemTouchCallBack.OnItemTouchListener {
    public static final int ADAPTER_MODE_NORMAL = 0;
    public static final int ADAPTER_MODE_SORT = 1;

    protected Context mContext;
    private Editor mEditor;
    List<AnywhereEntity> items;
    int mode;

    BaseAdapter(Context context) {
        this.mContext = context;
        this.items = new ArrayList<>();
        this.mode = ADAPTER_MODE_NORMAL;
    }

    public void setItems(List<AnywhereEntity> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void openAnywhereActivity(AnywhereEntity item) {
        String cmd = TextUtils.getItemCommand(item);
        if (!cmd.isEmpty()) {
            MainFragment.getViewModelInstance().getCommand().setValue(cmd);
        }
    }

    void openEditor(AnywhereEntity item, int type, int position) {
        Editor.OnEditorListener listener = new Editor.OnEditorListener() {
            @Override
            public void onDelete() {
                deleteAnywhereActivity(mEditor, item, position);
            }

//            @Override
//            public void onChange() {
//                notifyItemChanged(position);
//            }
        };

        mEditor = new Editor(MainActivity.getInstance(), type)
                .item(item)
                .isEditorMode(true)
                .isShortcut(item.getShortcutType() == AnywhereType.SHORTCUTS)
                .setOnEditorListener(listener)
                .build();

        mEditor.show();
    }

    private void deleteAnywhereActivity(Editor editor, AnywhereEntity ae, int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(mContext.getString(R.string.dialog_delete_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    MainFragment.getViewModelInstance().delete(ae);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        ShortcutsUtil.removeShortcut(ae);
                    }
                    editor.dismiss();
                    notifyItemRemoved(position);
                })
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> editor.show())
                .show();
    }

    public void updateSortedList() {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();

            for (int iter = 0; iter < items.size(); iter++) {
                AnywhereEntity item = items.get(iter);
                AnywhereEntity ae = new AnywhereEntity(item.getId(), item.getAppName(), item.getParam1(),
                        item.getParam2(), item.getParam3(), item.getDescription(), item.getType(),
                        startTime - iter * 100 + "");
                MainFragment.getViewModelInstance().update(ae);
            }
        }).start();
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(int position) {

    }
}