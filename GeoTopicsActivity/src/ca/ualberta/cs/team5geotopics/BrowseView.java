package ca.ualberta.cs.team5geotopics;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.team5geotopics.R;

/**
 * BrowswView is responsible for loading the view of the comments.
 */

public class BrowseView  extends ArrayAdapter<CommentModel> implements AView<AModel> {
	
	private List<CommentModel> mCommentList;
	private int mLayoutResourceId;
	private Context mContext;
	private DateFormat dateFormat; 
	private DateFormat timeFormat;
	private Cache mCache;
	private User mUser;
	
	/**
	 * Constructor for a new comment browse view.
	 *
	 * @param  context	The activity context for which the view is being built
	 * @param	layoutResourceId	The resource ID of the layout the view should use
	 * @param	mCommentList	The list of comments the view should display
	 * @return   The browseview
	 */
	public BrowseView(Context context, int layoutResourceId, List<CommentModel> mCommentList){
		super(context, layoutResourceId, mCommentList);
		this.mLayoutResourceId = layoutResourceId;
		this.mContext = context;
		this.mCommentList = mCommentList;
		this.dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
		this.timeFormat = android.text.format.DateFormat.getTimeFormat(mContext);
		
		//Testing this idea, registering with the cache so we know when new comments are added.
		mCache = Cache.getInstance();
		mCache.addView(this);
		mUser = User.getInstance();
	}
	
	/*
	 * holder class
	 * http://developer.android.com/training/improving-layouts/smooth-scrolling.html
	 * http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
	 */
	
	/**
	 * Basic holder for the BrowseView containing comment parameteres.
	 */
	public static class Holder{
		CommentModel comment;
		TextView title;
		TextView author;
		TextView body;
		TextView date;
		TextView time;
		ImageView picture;
		ImageView bookmark;
		ImageView favourite;
	}
	
	/**
	 * This method returns the view associated with the row of the ListView the adapter 
	 * is registered to. Basically it fills our ListView with the appropriate widgets.
	 *
	 *@author http://stackoverflow.com/questions/5177056/overriding-android-arrayadapter
	 *http://blog.ghatasheh.com/2012/11/android-array-adapter-viewholder.html
	 *
	 * @param  position	The position of the view
	 * @param	converView	The view we are converting
	 * @param	parent	The views parent
	 * @return  A view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		boolean isTopLevel = false;
		View view = convertView;
		Holder holder = null;
		if(mCommentList.get(position).isTopLevel()){
			isTopLevel = true;
		}
		// we need to call the findViewById to get the views
		if(view == null){
			Log.w("NewComment", "View is null");
				// fill row with TopLevelComment layout
				view = LayoutInflater.from(mContext).inflate(R.layout.comment_list_item,
															null, false);
				holder = new Holder();
				holder.comment = mCommentList.get(position);
				holder.title = (TextView)view.findViewById(R.id.top_level_title_list_item);
				holder.author = (TextView)view.findViewById(R.id.top_level_author_list_item);
				holder.body = (TextView)view.findViewById(R.id.top_level_body_list_item);
				holder.date = (TextView)view.findViewById(R.id.top_level_date_list_item);
				holder.time = (TextView)view.findViewById(R.id.top_level_time_list_item);
				holder.picture = (ImageView)view.findViewById(R.id.top_level_thumbnail);
				holder.bookmark = (ImageView)view.findViewById(R.id.top_level_bookmark);
				holder.favourite = (ImageView)view.findViewById(R.id.top_level_favourite);
				view.setTag(holder);
		}
		// we don't need to call findViewById to get views, because we already did.
		else{
			holder = (Holder) view.getTag();
		}
		CommentModel comment = mCommentList.get(position);
		// errorrr is right here.....
		Date date = comment.getDate();
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(mContext);

		//TODO Reference: 
		//Need to align body up higher on non top level
		//http://stackoverflow.com/questions/4638832/how-to-programmatically-set-the-layout-align-parent-right-attribute-of-a-button
		if(isTopLevel){
			holder.title.setVisibility(View.VISIBLE);
			holder.title.setText(comment.getmTitle());
		}else{
			holder.title.setVisibility(View.GONE);
		}
		
		holder.body.setText(comment.getmBody());
		holder.author.setText("By " + comment.getmAuthor());
		holder.date.setText(dateFormat.format(date));
		holder.time.setText(timeFormat.format(date));
		if(comment.getPicture() != null)
			holder.picture.setImageBitmap(comment.getPicture());
		else
			holder.picture.setImageResource(R.drawable.ic_action_uploadedphoto);
		//Set the bookmark flag to the right color
		if(mUser.inBookmarks(comment)){
			holder.bookmark.setImageResource(R.drawable.ic_notification_bookmark_b);
		}else{
			holder.bookmark.setImageResource(R.drawable.ic_notification_bookmark);
		}
		//Set the favourites flag to the right color
		if(mUser.inFavourites(comment)){
			holder.favourite.setImageResource(R.drawable.ic_action_favorite_b);
		}else{
			holder.favourite.setImageResource(R.drawable.ic_action_favorite);
		}
			
		return view;
		
	}

	/**
	 * The method a model will call if this view is registered with it.
	 *
	 * @param  model	The model that has changed
	 */
	@Override
	public void update(AModel model) {
		this.notifyDataSetChanged();
	}
}
