package com.javalive09.letterapps;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by peter on 2018/3/14.
 */

public class LetterRecyclerView extends RecyclerView {

    private List<AppGroup> groupDataList;
    private TouchLetterListener touchLetterListener;
    private LetterAdapter letterAdapter;
    private int selectedColor;
    private int normalColor;
    private int firstVisibleItem;
    private int lastVisibleItem;

    public LetterRecyclerView(Context context) {
        super(context);
        init();
    }

    public LetterRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        selectedColor = getResources().getColor(R.color.select, null);
        normalColor = getResources().getColor(R.color.no_select, null);
    }

    public LetterRecyclerView setData(List<AppGroup> groupDataList) {
        this.groupDataList = groupDataList;
        return this;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refresh(List<AppGroup> groupDataList) {
        setData(groupDataList);
        letterAdapter.notifyDataSetChanged();
    }

    LetterRecyclerView setTouchLetterListener(TouchLetterListener mTouchLetterListener) {
        this.touchLetterListener = mTouchLetterListener;
        return this;
    }

    public void refreshItemColor(final int firstVisibleItem, final int lastVisibleItem) {
        if (getChildCount() > 0) {
            refreshSelectView(firstVisibleItem, lastVisibleItem);
            if (this.firstVisibleItem < firstVisibleItem) { // slide down
                refreshNormalView(this.firstVisibleItem, Math.min(firstVisibleItem - 1, this.lastVisibleItem));
            } else if (this.lastVisibleItem > lastVisibleItem) { // slide up
                refreshNormalView(Math.max(this.firstVisibleItem, lastVisibleItem + 1), this.lastVisibleItem);
            }
            this.firstVisibleItem = firstVisibleItem;
            this.lastVisibleItem = lastVisibleItem;
        }
    }

    private void refreshSelectView(int firstVisibleItem, int lastVisibleItem) {
        for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
            View view = getChildAt(i);
            if (view != null) {
                TextView textView = (TextView) view;
                textView.setTextColor(selectedColor);
            }
        }
    }

    private void refreshNormalView(int firstItem, int lastItem) {
        for (int i = firstItem; i <= lastItem; i++) {
            View view = getChildAt(i);
            if (view != null) {
                TextView textView = (TextView) view;
                textView.setTextColor(normalColor);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                triggerListener(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (touchLetterListener != null) {
                    touchLetterListener.onHide();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void triggerListener(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View child = getChildAt(i);
            if (pointInView(child, x, y)) {
                if (touchLetterListener != null) {
                    String letter = groupDataList.get(i).letter;
                    touchLetterListener.onShow(letter, i);
                }
                break;
            }
        }
    }

    private boolean pointInView(View view, float localX, float localY) {
        int left = view.getLeft();
        int right = view.getRight();
        int bottom = view.getBottom();
        int top = view.getTop();
        return localX > left && localX < right && localY > top && localY < bottom;
    }

    public void build(Context context) {
        setAdapter(letterAdapter = new LetterAdapter(groupDataList, normalColor));
        setLayoutManager(new LinearLayoutManager(context));
    }

    private static final class LetterAdapter extends RecyclerView.Adapter<LetterAdapter.Holder> {

        private final List<AppGroup> mGroupDataList;
        private final int normalColor;

        LetterAdapter(List<AppGroup> groupDataList, int normalColor) {
            this.mGroupDataList = groupDataList;
            this.normalColor = normalColor;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.letter_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            String letter = mGroupDataList.get(position).letter;
            holder.letter.setText(letter);
            holder.letter.setTextColor(normalColor);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mGroupDataList.size();
        }

        static class Holder extends RecyclerView.ViewHolder {

            TextView letter;

            Holder(View itemView) {
                super(itemView);
                letter = itemView.findViewById(R.id.letter);
            }
        }

    }

    interface TouchLetterListener {

        void onShow(String currentLetter, int index);

        void onHide();
    }

}
