package jonasmohrpedersen.todolist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class TodoHolder extends RecyclerView.ViewHolder {

    //Local variables
    private String mUid;
    private String mTitle;
    private String mDescription;
    private int mColor, mTextColor;

    //View variables
    private TextView tvTitle, tvDescription;

    //View variables
    public CardView cardForeground, cardDeleteBackground, cardCompleteBackground;

    /**
     * Creates a TodoHolder which holds a view to be displayed as an item in
     * a RecyclerView
     * @param itemView
     * @param isCompleted
     */
    public TodoHolder(View itemView, boolean isCompleted) {
        super(itemView);
        this.tvTitle = itemView.findViewById(R.id.taskTitle);
        this.tvDescription = itemView.findViewById(R.id.taskDescription);
        this.cardDeleteBackground = itemView.findViewById(R.id.card_delete_background);
        this.cardForeground = itemView.findViewById(R.id.card_foreground);
        this.cardCompleteBackground = itemView.findViewById(R.id.card_complete_background);

        if(!isCompleted) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, TodoActivity.class);
                    intent.putExtra("title", mTitle);
                    intent.putExtra("description", mDescription);
                    intent.putExtra("color", mColor);
                    intent.putExtra("textcolor", mTextColor);
                    intent.putExtra("uid", mUid);
                    context.startActivity(intent);
                }
            });

        }

    }

    /**
     * Binds the current TodoObject to this TodoHolder
     * @param currentTodo   The current Todo that the adapter traverses over
     */
    public void bind(Todo currentTodo){

        //Retrieves uid, text- & background colors from Todo
        mUid = currentTodo.getUid();
        mColor = currentTodo.getColor();
        mTextColor = currentTodo.getTextColor();
        mTitle = currentTodo.getTitle();
        mDescription = currentTodo.getDescription();


        //Sets text & colors in TodoHolder
        cardForeground.setCardBackgroundColor(mColor);
        tvTitle.setText(mTitle);
        tvTitle.setTextColor(mTextColor);
        tvDescription.setText(mDescription);
        tvDescription.setTextColor(mTextColor);

        //Hides description from Todoholder if it is empty
        if(mDescription.isEmpty()){
            tvDescription.setVisibility(View.GONE);
        }
    }
}
