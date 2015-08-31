package br.com.mobilesaude.multiselectasgmail;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends ListFragment {

    private static final String TAG = "MainActivityFragment";

    private MyListAdapter myListAdapter;
    private List<ItemTO> listItens;
    private boolean isOnMultiAction = false;
    private android.support.v7.view.ActionMode mMode;
    private MultiActionMode multiActionMode;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listItens = Arrays
                .asList(new ItemTO("Primeiro"), new ItemTO("Segundo"), new ItemTO("Terceiro"), new ItemTO("Quarto"), new ItemTO("Quinto")
                        , new ItemTO("Sexto"), new ItemTO("Primeiro"), new ItemTO("Segundo"), new ItemTO("Terceiro"), new ItemTO
                        ("Quarto"), new ItemTO("Quinto"), new ItemTO("Sexto"), new ItemTO("Primeiro"), new ItemTO("Segundo"), new ItemTO
                        ("Terceiro"), new ItemTO("Quarto"), new ItemTO("Quinto"), new ItemTO("Sexto"));
        myListAdapter = new MyListAdapter();
        setListAdapter(myListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //        super.onListItemClick(l, v, position, id);
        ItemTO clicado = (ItemTO) l.getItemAtPosition(position);
        if (!isOnMultiAction) {
            Toast.makeText(getActivity(), clicado.getDescricao(), Toast.LENGTH_SHORT).show();
        } else {
            v.findViewById(R.id.imageview).performClick();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDrawSelectorOnTop(true);
    }

    class MyListAdapter extends ArrayAdapter<ItemTO> {

        private final Animation animation1;
        private final Animation animation2;
        public int checkedCount = 0;

        public MyListAdapter() {
            super(getActivity(), View.NO_ID, listItens);
            animation1 = AnimationUtils.loadAnimation(getContext(), R.anim.to_middle);
            animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.from_middle);

            isOnMultiAction = false;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // LayoutInflater class is used to instantiate layout XML file into its corresponding View objects.
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.ly_item, null);
            }
            final ItemTO item = getItem(position);
            ((TextView) convertView.findViewById(R.id.textview)).setText(item.getDescricao());
            ImageView image = (ImageView) convertView.findViewById(R.id.imageview);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getListView().setItemChecked(position, false);
                    Log.d(TAG, "Start selected: " + getListView().isItemChecked(position));
                    item.setChecked(!item.getChecked());
                    //animate
                    animateIcon(item, (ImageView) v);
                    //seta a linha
                    if (isOnMultiAction) {
                        getListView().setItemChecked(position, item.getChecked());
                    }
                    //ajusta barra
                    setCheckedCount(item);
                    if (isOnMultiAction) {
                        getListView().setItemChecked(position, item.getChecked());
                    }
                    getListView().requestLayout();
                    Log.d(TAG, "End selected: " + getListView().isItemChecked(position));
                }
            });
            if (item.getChecked()) {
                image.setImageResource(R.drawable.ic_checked);
            } else {
                image.setImageResource(R.drawable.ic_avatar);
            }

            return convertView;
        }

        private void setCheckedCount(ItemTO itemTO) {

            // Set selected count
            if (itemTO.getChecked()) {
                checkedCount++;
            } else {
                if (checkedCount != 0) {
                    checkedCount--;
                }
            }

            // Show/Hide action mode
            if (checkedCount > 0) {
                if (!isOnMultiAction) {
                    multiActionMode = new MultiActionMode(listItens);
                    mMode = ((ActionBarActivity) getActivity()).startSupportActionMode(multiActionMode);
                    isOnMultiAction = true;
                    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                }
            } else {
                isOnMultiAction = false;
                if (mMode != null) {
                    mMode.finish();
                    multiActionMode.finishMultiAction();
                }
            }

            // Set action mode title
            if (mMode != null) {
                mMode.setTitle(String.valueOf(checkedCount));
            }
            notifyDataSetChanged();
        }

        private void animateIcon(final ItemTO itemTO, final ImageView imagem) {

            imagem.clearAnimation();
            imagem.setAnimation(animation1);
            imagem.startAnimation(animation1);

            Animation.AnimationListener animListner = new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    if (animation == animation1) {
                        if (itemTO.getChecked()) {
                            imagem.setImageResource(R.drawable.ic_avatar);
                        } else {
                            imagem.setImageResource(R.drawable.ic_checked);
                        }
                        imagem.clearAnimation();
                        imagem.setAnimation(animation2);
                        imagem.startAnimation(animation2);
                    } else {
                        if (itemTO.getChecked()) {
                            imagem.setImageResource(R.drawable.ic_checked);
                        } else {
                            imagem.setImageResource(R.drawable.ic_avatar);
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {

                }

                @Override
                public void onAnimationEnd(Animation arg0) {

                }
            };

            animation1.setAnimationListener(animListner);
            animation2.setAnimationListener(animListner);

        }

    }

    class MultiActionMode implements ActionMode.Callback {

        private List<ItemTO> listItens;

        public MultiActionMode(List<ItemTO> listItens) {
            this.listItens = listItens;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            final MenuItem itemMove = menu.add("Move");
            final MenuItem itemRemove = menu.add("Remove star");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                itemMove.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                itemRemove.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }

            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            Toast toast = null;

            ArrayList<ItemTO> selectedListItems = new ArrayList<>();

            StringBuilder selectedItems = new StringBuilder();

            // get items selected
            for (ItemTO i : listItens) {
                if (i.getChecked()) {
                    selectedListItems.add(i);
                    selectedItems.append(i.getDescricao()).append(", ");
                }
            }

            if (item.getTitle().equals("Move")) {
                toast = Toast.makeText(getActivity(), "Move: " + selectedItems.toString(), Toast.LENGTH_SHORT);
            } else if (item.getTitle().equals("Remove star")) {
                toast = Toast.makeText(getActivity(), "Remove star: " + selectedItems.toString(), Toast.LENGTH_SHORT);
            }
            if (toast != null) {
                toast.show();
            }
            finishMultiAction();
            mMode.finish();
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            // Action mode is finished reset the listItens and 'checked count' also
            // set all the listItens items checked states to false
            finishMultiAction();
            Toast.makeText(getActivity(), "Action mode closed", Toast.LENGTH_SHORT).show();
        }

        private void finishMultiAction() {
            myListAdapter.checkedCount = 0;
            isOnMultiAction = false;
            // set listItens items states to false
            for (ItemTO item : listItens) {
                item.setChecked(false);
            }
            for (int i = 0; i < getListView().getCount(); i++) {
                getListView().setItemChecked(i, false);
            }
            //http://stackoverflow.com/questions/9754170/listview-selection-remains-persistent-after-exiting-choice-mode
            getListView().post(new Runnable() {
                @Override
                public void run() {
                    getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
                }
            });
            myListAdapter.notifyDataSetChanged();
        }
    }
}
