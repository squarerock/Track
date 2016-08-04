package squarerock.track.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import squarerock.track.Constants;
import squarerock.track.R;
import squarerock.track.model.Todo;

import static android.view.View.GONE;

/**
 * Created by pranavkonduru on 7/29/16.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder>{

    private List<Todo> todoObjects;

    public TodoAdapter(List<Todo> todoObjects) {
        this.todoObjects = todoObjects;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view, parent, false);

        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        Todo todo = todoObjects.get(position);
        holder.textViewTitle.setText(todo.name);
        holder.imageView.setBackgroundResource(getImageForPriority(todo.priority, todo.status));

        // Show date only if available
        if(!todo.date.isEmpty()) {
            holder.textViewDate.setText(todo.date);
        } else {
            holder.textViewTime.setVisibility(GONE);
        }

        // Show time only if available
        if(!todo.time.isEmpty()){
            holder.textViewTime.setText(todo.time);
        } else {
            holder.textViewTime.setVisibility(GONE);
        }

        // Show "Due by" only if either of time or date are present
        if(todo.date.isEmpty() && todo.time.isEmpty()){
            holder.textViewDueBy.setVisibility(GONE);
        }
    }

    private int getImageForPriority(int priority, String status) {
        // Task is done
        if(Constants.CHECKED.equals(status)){
            return R.drawable.ic_check_blue_700_36dp;
        }

        // Task is pending
        switch (priority){
            case 0:
                return R.drawable.ic_turned_in_green_900_18dp;
            case 1:
                return R.drawable.ic_turned_in_yellow_900_18dp;
            case 2:
                return R.drawable.ic_turned_in_red_900_18dp;
            default:
                return R.drawable.ic_turned_in_grey_700_18dp;
        }
    }

    @Override
    public int getItemCount() {
        return todoObjects.size();
    }

    public void updateData(ArrayList<Todo> todos) {
        todoObjects.clear();
        todoObjects.addAll(todos);
        notifyItemRangeChanged(0, todos.size());
    }

    public void removeItem(int position) {
        todoObjects.remove(position);
        notifyItemRemoved(position);
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewDate, textViewTime;
        TextView textViewDueBy;
        ImageView imageView;

        public TodoViewHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.title);
            textViewDate = (TextView) itemView.findViewById(R.id.date);
            textViewTime = (TextView) itemView.findViewById(R.id.time);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textViewDueBy = (TextView) itemView.findViewById(R.id.due_by);
        }
    }
}
