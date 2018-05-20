package jonasmohrpedersen.todolist;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View cardForeground = ((TodoHolder) viewHolder).cardForeground;
            getDefaultUIUtil().onSelected(cardForeground);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View cardForeground = ((TodoHolder) viewHolder).cardForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, cardForeground, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View cardForeground = ((TodoHolder) viewHolder).cardForeground;
        getDefaultUIUtil().clearView(cardForeground);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View cardForeground = ((TodoHolder) viewHolder).cardForeground;
        final View cardDeleteBackground = ((TodoHolder) viewHolder).cardDeleteBackground;
        final View cardCompleteBackground = ((TodoHolder) viewHolder).cardCompleteBackground;

        if(dX > 0){
            cardDeleteBackground.setVisibility(View.GONE);
            cardCompleteBackground.setVisibility(View.VISIBLE);
        } else {
            cardDeleteBackground.setVisibility(View.VISIBLE);
            cardCompleteBackground.setVisibility(View.GONE);
        }

        getDefaultUIUtil().onDraw(c, recyclerView, cardForeground, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
