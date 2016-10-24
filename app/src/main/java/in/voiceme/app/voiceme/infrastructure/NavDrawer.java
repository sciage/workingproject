package in.voiceme.app.voiceme.infrastructure;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.voiceme.app.voiceme.R;

public class NavDrawer {
    protected BaseActivity activity;
    protected DrawerLayout drawerLayout;
    protected ViewGroup navDrawerView;
    private ArrayList<NavDrawerItem> items;
    private NavDrawerItem selectedItem;

    public NavDrawer(BaseActivity activity) {
        this.activity = activity;
        items = new ArrayList<>();
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        navDrawerView = (ViewGroup) activity.findViewById(R.id.nav_drawer);

        if (drawerLayout == null || navDrawerView == null)
            throw new RuntimeException("To use this class, you must have views with the ids of drawer_layout and nav_drawer");

        Toolbar toolbar = activity.getToolbar();
        toolbar.setNavigationIcon(R.mipmap.ic_action_navigation_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOpen(!isOpen());
            }
        });

        activity.getVoicemeApplication().getBus().register(this);
    }

    public void destroy() {
        activity.getVoicemeApplication().getBus().unregister(this);
    }

    public void addItem(NavDrawerItem item) {
        items.add(item);
        item.navDrawer = this;
    }

    public boolean isOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void setOpen(boolean isOpen) {
        if (isOpen)
            drawerLayout.openDrawer(GravityCompat.START);
        else
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void setSelectedItem(NavDrawerItem item) {
        if (selectedItem != null)
            selectedItem.setSelected(false);

        selectedItem = item;
        selectedItem.setSelected(true);
    }

    public void create() {
        LayoutInflater inflater = activity.getLayoutInflater();
        for (NavDrawerItem item : items) {
            item.inflate(inflater, navDrawerView);
        }
    }

    public static abstract class NavDrawerItem {
        protected NavDrawer navDrawer;

        public abstract void inflate(LayoutInflater inflater, ViewGroup container);

        public abstract void setSelected(boolean isSelected);
    }

    public static class BasicNavDrawerItem extends NavDrawerItem implements View.OnClickListener {
        private String text;
        private String badge;
        private int iconDrawable;
        private int containerId;

        private ImageView icon;
        private TextView textView;
        private TextView badgeTextView;
        private View view;
        private int defaultTextColor;

        public BasicNavDrawerItem(String text, String badge, int iconDrawable, int containerId) {
            this.text = text;
            this.badge = badge;
            this.iconDrawable = iconDrawable;
            this.containerId = containerId;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawerView) {
            ViewGroup container = (ViewGroup) navDrawerView.findViewById(containerId);
            if (container == null)
                throw new RuntimeException("Nav drawer item " + text + " could not be attached to ViewGroup. View not found.");

            view = inflater.inflate(R.layout.list_item_nav_drawer, container, false);
            container.addView(view);

            view.setOnClickListener(this);

            icon = (ImageView) view.findViewById(R.id.list_item_nav_drawer_icon);
            textView = (TextView) view.findViewById(R.id.list_item_nav_drawer_text);
            badgeTextView = (TextView) view.findViewById(R.id.list_item_nav_drawer_badge);
            defaultTextColor = textView.getCurrentTextColor();

            icon.setImageResource(iconDrawable);
            textView.setText(text);

            if (badge != null)
                badgeTextView.setText(badge);
            else
                badgeTextView.setVisibility(View.GONE);
        }

        @Override
        public void setSelected(boolean isSelected) {
            if (isSelected) {
                view.setBackgroundResource(R.drawable.list_item_nav_drawer_selected_item_background);
                textView.setTextColor(navDrawer.activity.getResources().getColor(R.color.list_item_nav_drawer_selected_item_text_color));
            } else {
                view.setBackground(null);
                textView.setTextColor(defaultTextColor);
            }
        }

        public void setText(String text) {
            this.text = text;

            if (view != null)
                textView.setText(text);
        }

        public void setBadge(String badge) {
            this.badge = badge;

            if (view != null) {
                if (badge != null)
                    badgeTextView.setVisibility(View.VISIBLE);
                else
                    badgeTextView.setVisibility(View.GONE);
            }
        }

        public void setIcon(int iconDrawable) {
            this.iconDrawable = iconDrawable;

            if (view != null) {
                icon.setImageResource(iconDrawable);
            }
        }

        @Override
        public void onClick(View v) {
            navDrawer.setSelectedItem(this);
        }
    }

    public static class ActivityNavDrawerItem extends BasicNavDrawerItem {
        private final Class targetActivity;

        public ActivityNavDrawerItem(Class targetActivity, String text, String badge, int iconDrawable, int containerId) {
            super(text, badge, iconDrawable, containerId);
            this.targetActivity = targetActivity;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawer) {
            super.inflate(inflater, navDrawer);

            if (this.navDrawer.activity.getClass() == targetActivity) {
                this.navDrawer.setSelectedItem(this);
            }
        }

        @Override
        public void onClick(View view) {
            navDrawer.setOpen(false);
            final BaseActivity activity = navDrawer.activity;

            if (activity.getClass() == targetActivity)
                return;

            super.onClick(view);

            activity.fadeOut(new BaseActivity.FadeOutListener() {
                @Override
                public void onFadeOutEnd() {
                    activity.startActivity(new Intent(activity, targetActivity));
                    activity.finish();
                }
            });
        }
    }

}
