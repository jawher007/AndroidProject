package com.example.devappmobile;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DraftFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = MainActivity.class.getSimpleName();
    // TODO: Customize parameters
    private int mColumnCount = 1;
    MaterialCardView mcv ;
    private OnListFragmentInteractionListener mListener;
    private List<Draft> list = new ArrayList<>();
    private List<Draft> listcopie ;

    private String token= "";//getArguments().getString("token");;
    public  MyDraftRecyclerViewAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    SharedPreferences prefs;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://process.isiforge.tn/")
            .addConverterFactory(GsonConverterFactory.create());
    Retrofit retrofit= builder.build();
    UserClient userClient= retrofit.create(UserClient.class);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DraftFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DraftFragment newInstance(int columnCount) {
        DraftFragment fragment = new DraftFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draft_list, container, false);
        prefs= this.getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String restoredText = prefs.getString("token", null);
        if (restoredText != null) {
            token = prefs.getString("token", "No token defined");
            //System.out.println(token);

        }


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyDraftRecyclerViewAdapter(list, mListener);

            recyclerView.setAdapter(mAdapter);

            prepareData();

        }

        return view;
    }

    public Boolean verifyToken() {
        String dateString = prefs.getString("Expires_in", "Error token!!");
        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {

            Date date = formatter.parse(dateString);

            Calendar calendar = Calendar.getInstance();
            Date datenow = calendar.getTime();

            return datenow.before(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }


    private void refresh_Token() {

        String refreshToken = prefs.getString("refresh", "Error token!!");

        Login auth = new Login(refreshToken);


        Call<User> call = userClient.login(auth);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                System.out.println("i m in refresh response");


                if (response.isSuccessful()) {


                    Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
                    System.out.println("dateBEFORE" + calendar.getTime().toString());
                    calendar.add(Calendar.SECOND, Integer.parseInt(response.body().getExpires_in()));

                    SharedPreferences.Editor sh = getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                    sh.putString("token", "Bearer " + response.body().getAccess_token());
                    sh.putString("refresh", response.body().getRefresh_token());
                    sh.putString("Expires_in",calendar.getTime().toString()) ;
                    sh.commit();

                    prepareData();

                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("failed refresh");
            }
        });
    }
    public void doAdapter(){

        if (verifyToken()){
            prepareData();
        }else {
            System.out.println("I m in refresh tokeen ");
            refresh_Token();
        }


    }
    private void prepareData() {

        System.out.println(token);
        Call<List<Draft>> cal = userClient.getDraft(token);
        cal.enqueue(new Callback<List<Draft>>() {
            @Override
            public void onResponse(Call<List<Draft>> call, Response<List<Draft>> response) {
                list.clear();
                if( response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++)
                        list.add(response.body().get(i));
                    mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<Draft>> call, Throwable t) {
                Toast.makeText(getContext(), "cant find token", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MyDraftRecyclerViewAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = list.get(viewHolder.getAdapterPosition()).getApp_pro_title();

            // backup of removed item for undo purpose
            final Draft deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(getView(), name + " removed from Draft", Snackbar.LENGTH_LONG);
            snackbar.setAction("Restore ? ", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListDraftFragmentInteraction(Draft item);
    }
}
