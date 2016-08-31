package com.example.hoon.educastcourselistdemo.scenes.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.hoon.educastcourselistdemo.R;
import com.example.hoon.educastcourselistdemo.models.Course;
import com.example.hoon.educastcourselistdemo.network.VolleyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimin on 16. 8. 30.
 */
public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainViewHolder> {

    private ArrayList<MainModel.CourseList.SuccessViewModel.DisplayedCourse> courseList;

    private Context context;

    public MainRecyclerViewAdapter(Context context) {
        this(context, null);
    }

    public MainRecyclerViewAdapter(Context context, ArrayList<MainModel.CourseList.SuccessViewModel.DisplayedCourse> list) {
        if(list == null){
            this.courseList = new ArrayList<>();
        } else {
            this.courseList = list;
        }
        this.context = context;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_course_row_layout, parent, false);
        MainViewHolder holder = new MainViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, int position) {
        MainModel.CourseList.SuccessViewModel.DisplayedCourse course = courseList.get(position);

        // thumbnail
        VolleyManager.get(context).getImageLoader().get(
                course.thumbnail,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.thumbnail.setImageBitmap(response.getBitmap());
                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        // name
        holder.name.setText(course.name);
        // channel name
        holder.channelName.setText(course.channelName);
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    public void addAll(List<MainModel.CourseList.SuccessViewModel.DisplayedCourse> courses){
        if(courses != null && !courses.isEmpty()) {
            courseList.addAll(courses);
            notifyDataSetChanged();
        }
    }

    public void clear(){
        courseList.clear();
        notifyDataSetChanged();
    }

    public ArrayList<MainModel.CourseList.SuccessViewModel.DisplayedCourse> getList(){
        return courseList;
    }
}
