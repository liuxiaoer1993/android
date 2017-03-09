package com.ls.webapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView webList = null;
    private RecyclerViewAdapter adapter = null;

    private static final int gridRowCount = 3;

    private String[] webUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "么么哒", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        });

        initView();
    }

    private void initView() {
        webList = (RecyclerView) findViewById(R.id.web_tab_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this,gridRowCount);
        adapter = new RecyclerViewAdapter();
        webList.setLayoutManager(layoutManager);
        //webList.addItemDecoration(new DividerItemDecoration());
        webList.setAdapter(adapter);
        int[] webImagesId = {R.drawable.taobao,R.drawable.jingdong,R.drawable.baidu};
        String[] webTexts = getResources().getStringArray(R.array.wen_texts);
        adapter.setWebImagesId(webImagesId);
        adapter.setWebTexts(webTexts);
        adapter.setItemClickListener(itemClickListener);

        webUrls = getResources().getStringArray(R.array.web_url);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Intent intent = new Intent(MainActivity.this,WebActivity.class);
            intent.putExtra("url",webUrls[position]);
            startActivity(intent);
        }
    };

    private class RecyclerViewAdapter extends RecyclerView.Adapter<WebViewHolder> {

        private String[] webTexts;
        private int[] webImagesId;

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        private OnItemClickListener itemClickListener;

        @Override
        public int getItemCount() {
            return webTexts.length;
        }

        @Override
        public void onBindViewHolder(WebViewHolder holder, final int position) {
            holder.webImg.setImageResource(webImagesId[position]);
            holder.webText.setText(webTexts[position]);
            holder.webItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v,position);
                }
            });
        }

        @Override
        public WebViewHolder onCreateViewHolder(ViewGroup parent,int position) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.web_tab_list_layout,parent,false);
            WebViewHolder holder = new WebViewHolder(v);
            holder.webImg.setImageResource(webImagesId[position]);
            holder.webText.setText(webTexts[position]);
            return holder;
        }

        public void setWebImagesId(int[] webImages) {
            this.webImagesId = webImages;
        }

        public void setWebTexts(String[] webTexts) {
            this.webTexts = webTexts;
        }
    }

    private class WebViewHolder extends RecyclerView.ViewHolder {

        private ImageView webImg;
        private TextView webText;
        private LinearLayout webItem;

        public WebViewHolder (View v){
            super(v);
            webImg = (ImageView) v.findViewById(R.id.web_img);
            webText = (TextView) v.findViewById(R.id.web_text);
            webItem = (LinearLayout) v.findViewById(R.id.web_item_layout);
        }
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Paint dividerPaint = null;

        public void setDividerPaint(Paint dividerPaint) {
            this.dividerPaint = dividerPaint;
        }

        public DividerItemDecoration() {
            dividerPaint = new Paint();
            dividerPaint.setStyle(Paint.Style.FILL);
            dividerPaint.setStrokeWidth(2);
            dividerPaint.setColor(getResources().getColor(R.color.colorPrimary));
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            //drawHorizontalDivider(c, parent);
            drawVerticalDivider(c, parent);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //outRect.set(0,0,1,1);
        }

        private void drawHorizontalDivider(Canvas c,RecyclerView parent) {
            int childCount = parent.getChildCount();
            int column = childCount / gridRowCount;
            for (int i = 0;i < childCount;i++) {
                View child  = parent.getChildAt(i);
                int left = child.getLeft();
                int right = child.getRight();
                int top = child.getTop();
                int bottom = child.getBottom();

                c.drawLine(left,top,right,top,dividerPaint);
                if ((column - i) == 1) {
                    c.drawLine(left,bottom,right,bottom,dividerPaint);
                }
            }
        }

        private void drawVerticalDivider(Canvas c,RecyclerView parent) {
            int childCount = parent.getChildCount();
            for (int i = 0;i < childCount;i++) {
                View child = parent.getChildAt(i);
                int left = child.getLeft();
                int right = child.getRight();
                int top = child.getTop();
                int bottom = child.getBottom();
                c.drawLine(left,top,left,bottom,dividerPaint);
                if (i == (childCount - 1)) {
                    c.drawLine(right,top,right,bottom,dividerPaint);
                }
            }
        }
    }
}
