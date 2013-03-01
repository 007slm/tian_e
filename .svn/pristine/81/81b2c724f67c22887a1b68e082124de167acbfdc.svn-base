
package com.orange.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class OpenContextMenuPage extends Activity {
    private ListView lv;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.context_menu_layout);
        lv = (ListView) findViewById(R.id.menu_list);
        String[] content = getIntent().getStringArrayExtra("menus");
        final int[] menuIds = getIntent().getIntArrayExtra("menuids");
        lv.setAdapter(new MenuAdapter(content, this));
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                setResult(RESULT_OK, new Intent().putExtra("id", menuIds[arg2]));
                finish();
            }
        });

    }

    class MenuAdapter extends BaseAdapter {
        private String[] mContents;
        private Context mContext;

        public MenuAdapter(String[] mContents, Context mContext) {
            this.mContents = mContents;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mContents.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater flater = LayoutInflater.from(mContext);
            View view = flater.inflate(R.layout.context_menu_item, null);
            TextView tv = (TextView) view.findViewById(R.id.menu_item);
            tv.setText(mContents[position]);
            return view;
        }

    }
}
