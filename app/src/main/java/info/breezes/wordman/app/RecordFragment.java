package info.breezes.wordman.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import info.breezes.orm.utils.CursorUtils;
import info.breezes.wordman.db.StudyRecord;
import info.breezes.wordman.utils.DateUtils;

import java.util.Date;


public class RecordFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    class StudyRecordAdapter extends BaseAdapter {

        private Cursor cursor;
        private Handler handler;
        private LayoutInflater layoutInflater;

        public StudyRecordAdapter(LayoutInflater layoutInflater, Context context) {
            this.layoutInflater = layoutInflater;
            handler = new Handler(context.getMainLooper());
            cursor = WordmanApplication.current.getDbHelper().query(StudyRecord.class).orderBy("date", "desc").execute().getCursor();
            cursor.registerContentObserver(new ContentObserver(handler) {
                @Override
                public void onChange(boolean selfChange) {
                    notifyDataSetChanged();
                }
            });
            cursor.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public StudyRecord getItem(int position) {
            cursor.moveToPosition(position);
            return CursorUtils.readCurrentEntity(StudyRecord.class, cursor);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = layoutInflater.inflate(R.layout.record_item, null);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
                holder.textView3 = (TextView) convertView.findViewById(R.id.textView3);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            StudyRecord studyRecord = getItem(position);
            holder.textView.setText(studyRecord.date);
            holder.textView1.setText(DateUtils.format(new Date(studyRecord.time), "hh:mm:ss"));
            holder.textView2.setText("学习："+studyRecord.studyCount);
            holder.textView3.setText("温习："+studyRecord.reviewCount);
            return convertView;
        }

        class Holder {
            TextView textView;
            TextView textView1;
            TextView textView2;
            TextView textView3;
        }
    }

    public RecordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, null);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(R.color.red, R.color.green, R.color.blue, R.color.yellow);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new StudyRecordAdapter(inflater, getActivity()));
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}
