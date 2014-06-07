package info.breezes.wordman.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;


import android.widget.TextView;
import info.breezes.orm.QueryAble;
import info.breezes.wordman.db.ClassTable;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class LessonsFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

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
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
                convertView.setTag(holder);
            }
            holder = (Holder) convertView.getTag();
            ClassTable classTable = getItem(position);
            holder.textView.setText(classTable.name);
            holder.textView1.setText(classTable.finished + "");
            holder.textView2.setText(classTable.learned + "");
            Log.d("LessonsFragment", "getView cost:" + (System.currentTimeMillis() - st));
            return convertView;
        }

        class Holder {
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
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LessonsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new LessonsAdapter(getActivity().getLayoutInflater()));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("Lesson", "onAttach");
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, final long id) {
        final ClassTable classTable = ((LessonsAdapter) l.getAdapter()).getItem(position);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
