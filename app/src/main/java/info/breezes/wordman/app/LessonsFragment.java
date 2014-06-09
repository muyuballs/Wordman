package info.breezes.wordman.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.ListFragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import info.breezes.orm.QueryAble;
import info.breezes.wordman.db.ClassTable;


public class LessonsFragment extends Fragment implements AdapterView.OnItemClickListener {



    class LessonsAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private QueryAble<ClassTable> queryAble;

        public LessonsAdapter(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
            queryAble = WordmanApplication.current.getDbHelper().query(ClassTable.class).orderBy("id", "asc").execute();
        }

        @Override
        public int getCount() {
            return queryAble.size();
        }

        @Override
        public ClassTable getItem(int position) {
            return queryAble.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            long st = System.currentTimeMillis();
            Holder holder;
            if (convertView == null) {
                long st2 = System.currentTimeMillis();
                convertView = layoutInflater.inflate(R.layout.lesson_item, null);
                Log.d("LessonsFragment", "inflate cost:" + (System.currentTimeMillis() - st2));
                holder = new Holder();
                holder.imageView=(ImageView)convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
                convertView.setTag(holder);
            }
            holder = (Holder) convertView.getTag();
            ClassTable classTable = getItem(position);
            if(classTable.learned<1) {
                holder.imageView.setColorFilter(Color.argb(200,0xa2,0xa2,0xa2));
            }else{
                holder.imageView.clearColorFilter();
            }
            holder.textView.setText(classTable.name);
            holder.textView1.setText(classTable.finished + "");
            holder.textView2.setText(classTable.learned + "");
            Log.d("LessonsFragment", "getView cost:" + (System.currentTimeMillis() - st));
            return convertView;
        }

        class Holder {
            ImageView imageView;
            TextView textView;
            TextView textView1;
            TextView textView2;
        }

        @Override
        protected void finalize() throws Throwable {
            if (queryAble != null) {
                queryAble.close();
            }
            super.finalize();
        }

        public void reload(){
            queryAble=queryAble.execute();
            notifyDataSetChanged();
        }
    }


    private GridView gridView;

    public LessonsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_lessons,null);
        gridView=(GridView)view.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(new LessonsAdapter(getActivity().getLayoutInflater()));
        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("Lesson", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        ((LessonsAdapter)gridView.getAdapter()).reload();
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ClassTable classTable = ((LessonsAdapter) gridView.getAdapter()).getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(classTable.name);
        builder.setTitle("提示");
        if (classTable.learned < classTable.size) {
            builder.setPositiveButton("学习", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), StudyActivity.class);
                    intent.putExtra("CLASS", classTable.id);
                    intent.putExtra("TYPE", StudyType.STUDY);
                    startActivity(intent);
                }
            });
        }
        if (classTable.learned > 0) {
            builder.setNegativeButton("复习", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), StudyActivity.class);
                    intent.putExtra("CLASS", classTable.id);
                    intent.putExtra("TYPE", StudyType.REVIEW);
                    startActivity(intent);
                }

            });
        }
        builder.create().show();
    }

}
