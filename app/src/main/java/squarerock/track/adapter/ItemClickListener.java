package squarerock.track.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener itemClickListener;
    private RecyclerView view;

    private static final String TAG = "ItemClickListener";
    
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    GestureDetector gestureDetector;

    public ItemClickListener(Context context, RecyclerView recyclerView, OnItemClickListener listener) {
        itemClickListener = listener;
        view = recyclerView;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: ");
                View childView = view.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && itemClickListener != null){
                    Log.d(TAG, "onLongPress: sending onItemLongClick");
                    itemClickListener.onItemLongClick(childView, view.getChildAdapterPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: "+e.toString());
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && itemClickListener != null && gestureDetector.onTouchEvent(e)) {
            Log.d(TAG, "onInterceptTouchEvent: sending onItemClick: "+e.toString());
            itemClickListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

}
