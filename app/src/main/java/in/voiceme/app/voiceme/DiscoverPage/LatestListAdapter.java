package in.voiceme.app.voiceme.DiscoverPage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import in.voiceme.app.voiceme.R;
import in.voiceme.app.voiceme.services.PostsModel;
import in.voiceme.app.voiceme.RecyclerViewDetails.UserHugCounterActivity;
import in.voiceme.app.voiceme.RecyclerViewDetails.UserLikeCounterActivity;
import in.voiceme.app.voiceme.RecyclerViewDetails.UserListenCounterActivity;
import in.voiceme.app.voiceme.RecyclerViewDetails.UserSameCounterActivity;


/**
 * Created by ericbasendra on 02/12/15.
 */
public class LatestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static MyClickListener myClickListener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    public List<PostsModel> dataSet;
    private Context mContext;
    private int mLastPosition = 5;
    private double current_lat, current_long;

    public LatestListAdapter(List<PostsModel> productLists, Context mContext) {
        this.mContext = mContext;
        this.dataSet = productLists;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void animateTo(List<PostsModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }


    private void applyAndAnimateRemovals(List<PostsModel> newModels) {
        for (int i = dataSet.size() - 1; i >= 0; i--) {
            final PostsModel model = dataSet.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }


    private void applyAndAnimateAdditions(List<PostsModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final PostsModel model = newModels.get(i);
            if (!dataSet.contains(model)) {
                addItem(i, model);
            }
        }
    }


    private void applyAndAnimateMovedItems(List<PostsModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final PostsModel model = newModels.get(toPosition);
            final int fromPosition = dataSet.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void addItem(PostsModel item) {
        if (!dataSet.contains(item)) {
            dataSet.add(item);
            notifyItemInserted(dataSet.size() - 1);
        }
    }

    public void addItem(int position, PostsModel model) {
        dataSet.add(position, model);
        notifyItemInserted(position);
    }

    public void removeItem(PostsModel item) {
        int indexOfItem = dataSet.indexOf(item);
        if (indexOfItem != -1) {
            this.dataSet.remove(indexOfItem);
            notifyItemRemoved(indexOfItem);
        }
    }

    public PostsModel removeItem(int position) {
        final PostsModel model = dataSet.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void clearItem() {
        if (dataSet != null)
            dataSet.clear();
    }

    public void moveItem(int fromPosition, int toPosition) {
        final PostsModel model = dataSet.remove(fromPosition);
        dataSet.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public PostsModel getItem(int index) {
        if (dataSet != null && dataSet.get(index) != null) {
            return dataSet.get(index);
        } else {
            throw new IllegalArgumentException("Item with index " + index + " doesn't exist, dataSet is " + dataSet);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_posts_cardview, parent, false);
            vh = new EventViewHolder(itemView);
        } else if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);
            vh = new ProgressViewHolder(v);
        } else {
            throw new IllegalStateException("Invalid type, this type ot items " + viewType + " can't be handled");
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventViewHolder) {
            PostsModel dataItem = dataSet.get(position);
            ((EventViewHolder) holder).user_name.setText(dataItem.getUserNicName());
            ((EventViewHolder) holder).feeling.setText(dataItem.getEmotions());
            ((EventViewHolder) holder).category.setText(dataItem.getCategory());
            ((EventViewHolder) holder).timeStamp.setText(dataItem.getPostTime());
            ((EventViewHolder) holder).postMessage.setText(dataItem.getTextStatus());
            //  ((EventViewHolder) holder).post_audio_duration.setText(((LatestModal)dataItem).getAudioDuration().toString());
            ((EventViewHolder) holder).post_comments.setText("" + dataItem.getComments());
            ((EventViewHolder) holder).like_counter.setText("" + dataItem.getLikes());
            ((EventViewHolder) holder).hug_counter.setText("" + dataItem.getHug());
            ((EventViewHolder) holder).same_counter.setText("" + dataItem.getSame());
            ((EventViewHolder) holder).post_listen.setText("" + dataItem.getListen());

            if (!dataItem.getAvatarPics().equals("")) {
                Picasso.with(mContext)
                        .load(dataItem.getAvatarPics())
                        .resize(75, 75)
                        .centerInside()
                        .into(((EventViewHolder) holder).user_avatar);
            }
            ((EventViewHolder) holder).play_button.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        if (dataSet != null)
            return dataSet.size();
        else
            return 0;
    }


    /**
     * y Custom Item Listener
     */

    public interface MyClickListener {
        void onItemClick(int position, View v);

        void onLikeUnlikeClick(int position, LikeButton v);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnLikeListener {

        //Imageview for avatar and play pause button
        ImageView user_avatar;
        ImageView play_button;

        //username, feeling and category
        TextView user_name;
        TextView isPost;
        TextView feeling;
        TextView category;

        //post data
        TextView timeStamp;
        TextView postMessage;
        TextView postReadMore;
        TextView post_audio_duration;

        //counter numbers
        TextView like_counter;
        TextView hug_counter;
        TextView same_counter;
        TextView post_comments;
        TextView post_listen;

        //emoji for like, hug and same above
        ImageView likeCounterImage;
        ImageView hugCounterImage;
        ImageView sameCounterImage;
        ImageView listenCounterImage;

        //animated buttons
        LikeButton likeButtonMain, HugButtonMain, SameButtonMain;

        View parent_row;

        public EventViewHolder(View itemView) {
            super(itemView);

            //Imageview for avatar and play pause button
            user_avatar = (ImageView) itemView.findViewById(R.id.list_item_posts_avatar);
            play_button = (ImageView) itemView.findViewById(R.id.list_item_posts_play_button);

            //username, feeling and category
            user_name = (TextView) itemView.findViewById(R.id.list_item_post_userNickName);
            isPost = (TextView) itemView.findViewById(R.id.list_item_post_is);
            feeling = (TextView) itemView.findViewById(R.id.list_item_posts_feeling);
            category = (TextView) itemView.findViewById(R.id.list_item_posts_category);

            //post data
            post_audio_duration = (TextView) itemView.findViewById(R.id.list_item_posts_duration_count);
            timeStamp = (TextView) itemView.findViewById(R.id.list_item_posts_timeStamp);
            postMessage = (TextView) itemView.findViewById(R.id.list_item_posts_message);
            postReadMore = (TextView) itemView.findViewById(R.id.list_item_posts_read_more);

            //counter numbers
            like_counter = (TextView) itemView.findViewById(R.id.post_likes_counter);
            hug_counter = (TextView) itemView.findViewById(R.id.post_hugs_counter);
            same_counter = (TextView) itemView.findViewById(R.id.post_same_counter);
            post_comments = (TextView) itemView.findViewById(R.id.post_comment_counter);
            post_listen = (TextView) itemView.findViewById(R.id.post_listen_counter);


            //emoji for like, hug and same above
            likeCounterImage = (ImageView) itemView.findViewById(R.id.emoji_above_like);
            hugCounterImage = (ImageView) itemView.findViewById(R.id.emoji_above_hug);
            sameCounterImage = (ImageView) itemView.findViewById(R.id.emoji_above_same);
            listenCounterImage = (ImageView) itemView.findViewById(R.id.emoji_above_listen);

            //animated buttons
            likeButtonMain = (LikeButton) itemView.findViewById(R.id.list_item_like_button);
            HugButtonMain = (LikeButton) itemView.findViewById(R.id.list_item_hug_button);
            SameButtonMain = (LikeButton) itemView.findViewById(R.id.list_item_same_button);

            parent_row = (View) itemView.findViewById(R.id.parent_row);

            //OnClickListeners
            likeButtonMain.setOnLikeListener(this);
            HugButtonMain.setOnLikeListener(this);
            SameButtonMain.setOnLikeListener(this);

            likeCounterImage.setOnClickListener(this);
            hugCounterImage.setOnClickListener(this);
            sameCounterImage.setOnClickListener(this);
            listenCounterImage.setOnClickListener(this);

            parent_row.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try {
                if (myClickListener != null) {
                    myClickListener.onItemClick(getLayoutPosition(), view);
                } else {
                    Toast.makeText(view.getContext(), "Click Event Null", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Toast.makeText(view.getContext(), "Click Event Null Ex", Toast.LENGTH_SHORT).show();
            }

            int itemId = view.getId();

            if (itemId == R.id.emoji_above_like || itemId == R.id.post_likes_counter) {
                Intent intent = new Intent(view.getContext(), UserLikeCounterActivity.class);
                view.getContext().startActivity(intent);
            } else if (itemId == R.id.emoji_above_hug || itemId == R.id.post_hugs_counter) {
                Intent intent = new Intent(view.getContext(), UserHugCounterActivity.class);
                view.getContext().startActivity(intent);
            } else if (itemId == R.id.emoji_above_same || itemId == R.id.post_same_counter) {
                Intent intent = new Intent(view.getContext(), UserSameCounterActivity.class);
                view.getContext().startActivity(intent);
            } else if (itemId == R.id.emoji_above_listen || itemId == R.id.post_listen_counter) {
                Intent intent = new Intent(view.getContext(), UserListenCounterActivity.class);
                view.getContext().startActivity(intent);
            }

        }

        @Override
        public void liked(LikeButton likeButton) {

            int likeCounter = 0;
            int hugCounter = 0;
            int sameCounter = 0;
            try {
                if (myClickListener != null) {
                    myClickListener.onLikeUnlikeClick(getLayoutPosition(), likeButton);
                } else {
                    Toast.makeText(likeButton.getContext(), "Click Event Null", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Toast.makeText(likeButton.getContext(), "Click Event Null Ex", Toast.LENGTH_SHORT).show();
            }

            if (likeButton == likeButtonMain) {
                likeCounter++;
                like_counter.setText(NumberFormat.getIntegerInstance().format(likeCounter));
                Toast.makeText(likeButton.getContext(), "clicked like button", Toast.LENGTH_SHORT).show();
            } else if (likeButton == HugButtonMain) {
                hugCounter++;
                hug_counter.setText(NumberFormat.getIntegerInstance().format(hugCounter));
                Toast.makeText(likeButton.getContext(), "clicked Hug button", Toast.LENGTH_SHORT).show();
            } else if (likeButton == SameButtonMain) {
                sameCounter++;
                same_counter.setText(NumberFormat.getIntegerInstance().format(sameCounter));
                Toast.makeText(likeButton.getContext(), "clicked same button", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void unLiked(LikeButton likeButton) {
            try {
                if (myClickListener != null) {
                    myClickListener.onLikeUnlikeClick(getLayoutPosition(), likeButton);
                } else {
                    Toast.makeText(likeButton.getContext(), "Click Event Null", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Toast.makeText(likeButton.getContext(), "Click Event Null Ex", Toast.LENGTH_SHORT).show();
            }

            if (likeButton == likeButtonMain) {
                Toast.makeText(likeButton.getContext(), "clicked unlike button", Toast.LENGTH_SHORT).show();
            } else if (likeButton == HugButtonMain) {
                Toast.makeText(likeButton.getContext(), "clicked unHug button", Toast.LENGTH_SHORT).show();
            } else if (likeButton == SameButtonMain) {
                Toast.makeText(likeButton.getContext(), "clicked unsame button", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }
}
