package com.patrick.cview.dragrecyclerview;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @param <T>
 * @param <T2>
 * @useage eg:
 * <p>
 * mRecyclerView = findViewById(R.id.rv);
 * mAdapter = new AbstractDragRecycleViewAdapter<T,T2>(context, android.support.v7.widget.RecyclerView, List<T>) {
 * @Override public T2 onCreateMyViewHolder(ViewGroup parent, int viewType) {
 * return new T2(LayoutInflater.from(context).inflate(R.layout.recyclerview_item, null));
 * }
 * @Override public void onBindMyViewHolder(T2 holder, int position) {
 * holder.tv.setText(List<T>.get(position).toString());
 * holder.iv.setOnClickListener(new View.OnClickListener() {
 * @Override public void onClick(View view) {
 * Toast.makeText(context, "哈哈哈", Toast.LENGTH_SHORT).show();
 * }
 * });
 * }
 * };
 * <p>
 * mRecyclerView.setAdapter(mAdapter);
 */


public abstract class AbstractDragRecycleViewAdapter<T, T2 extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T2> {
    private Context mContext;
    public List<T> mTempData = new ArrayList<>();

    public AbstractDragRecycleViewAdapter(Context context, RecyclerView recyclerView, List<T> listData) {
        this.mContext = context;
        if (listData != null) {
            mTempData.clear();
            mTempData.addAll(listData);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public T2 onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateMyViewHolder(mContext, parent, viewType);
    }

    @Override
    public void onBindViewHolder(T2 holder, final int position) {
        onBindMyViewHolder(mContext, holder, position);
    }

    @Override
    public int getItemCount() {
        return mTempData.size();
    }

    public void add(T item) {
        int position = mTempData.size();
        mTempData.add(item);
        notifyItemInserted(position);
    }

    public void add(int position, T item) {
        mTempData.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mTempData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mTempData.size());
    }

    public ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlag = 0;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlag, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position
            int currentPosition = viewHolder.getAdapterPosition();
            int targetPosition = target.getAdapterPosition();
            if (currentPosition < targetPosition) {
                for (int i = currentPosition; i < targetPosition; i++) {
                    Collections.swap(mTempData, i, i + 1);
                }
            } else {
                for (int i = currentPosition; i > targetPosition; i--) {
                    Collections.swap(mTempData, i, i - 1);
                }
            }
            notifyItemMoved(currentPosition, targetPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //侧滑时会被调用到；
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        /**
         * 长按选中itemView后
         * @param viewHolder
         * @param actionState
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundColor(Color.RED);
                //震动50毫秒
                if (mContext != null) {
                    Vibrator vib = (Vibrator) mContext.getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(50);
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 拖动释放后刷新,notifyDataSetChanged()防止拖动后删除数据错乱
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(0);
            notifyDataSetChanged();
        }
    });

    protected abstract T2 onCreateMyViewHolder(final Context context, ViewGroup parent, int viewType);

    protected abstract void onBindMyViewHolder(final Context context, T2 holder, int position);

    protected abstract void addAll(List<T> menuItemBeanList);

    protected List<T> getOriginData() {
        return mTempData;
    }

    protected void resetData(List<T> menuItemBeanList) {
        mTempData.clear();
        mTempData.addAll(menuItemBeanList);
        notifyDataSetChanged();
    }
}
