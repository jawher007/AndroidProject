package com.example.devappmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import android.net.Uri;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FormFragment extends Fragment {
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://process.isiforge.tn/")
            .addConverterFactory(GsonConverterFactory.create(gson));
    Retrofit retrofit = builder.build();
    UserClient userClient = retrofit.create(UserClient.class);


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "formJsonString";
    private static final String ARG_PARAM2 = "pro_uid";
    private static final String ARG_PARAM3 = "tas_uid";
    private static final String ARG_PARAM4 = "token";

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private TextView mDisplayTime;
    private TextView mDisplayTime1;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private static final String TAG = "erreur";
    // TODO: Rename and change types of parameters
    private List<Object> mParam1;
    private String pro_uid;
    private String tas_uid;
    private String token;


    private JSONObject apiPostFormObject;
    private OnFragmentInteractionListener mListener;


    public FormFragment() {

    }
    public static FormFragment newInstance(String param1, String param2) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("c bn creation saret ");

        if (getArguments() != null) {
            mParam1 = (List<Object>) getArguments().getSerializable(ARG_PARAM1);
            pro_uid = getArguments().getString(ARG_PARAM2);
            tas_uid = getArguments().getString(ARG_PARAM3);
            token = getArguments().getString(ARG_PARAM4);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final LinearLayout myLayout = new LinearLayout(getActivity());
        myLayout.setOrientation(LinearLayout.VERTICAL);
        myLayout.setPadding(48, 32, 48, 0);


        apiPostFormObject = new JSONObject();
        for (int i = 0; i < mParam1.size(); i++) {

            JSONObject formItem = null;

            try {
                formItem = new JSONObject( new Gson().toJson(mParam1.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (formItem.has("type")) {
                    String type = formItem.getString("type");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    params.setMargins(0, 32, 0, 0);
                    switch (type) {
                        case "title":
                            TextView title = new TextView(getActivity());

                            String test = formItem.getString("label");
                            TextView title1 = new TextView(getActivity());
                            title1.setText("Deposer une demande de double correction");
                            title1.setTextAppearance(getActivity(), R.style.TextAppearance_MaterialComponents_Headline5);
                            title1.setPadding(0, 40, 0, 32);
                            myLayout.addView(title1);

                            title.setText(formItem.getString("label"));
                            title.setTextAppearance(getActivity(), R.style.TextAppearance_MaterialComponents_Headline5);
                            title.setPadding(0, 32, 0, 32);
                            myLayout.addView(title);
                            break;

                        case "text":

                            TextInputLayout inputLayout = new TextInputLayout(getActivity(), null, R.attr.myInputStyle);
                            inputLayout.setLayoutParams(params);
                            TextInputEditText editText = new TextInputEditText(getActivity());
                            editText.setHint(formItem.getString("label"));
                            editText.setTag(formItem.getString("variable"));
                            apiPostFormObject.put(formItem.getString("variable"), "");
                            inputLayout.addView(editText);
                            myLayout.addView(inputLayout);
                            break;

                        case "radio":
                            TextView txtRadio = new TextView(getActivity());
                            txtRadio.setText(formItem.getString("label"));
                            txtRadio.setTextColor(Color.parseColor("#000000"));
                            txtRadio.setTextSize(20);

                            RadioGroup radioGroup = new RadioGroup(getActivity(), null);
                            radioGroup.setTag(formItem.getString("variable"));

                            JSONArray options = new JSONArray(formItem.getString("options"));
                            for (int c = 0; c < options.length(); c++) {
                                RadioButton radioBtn = new RadioButton(getActivity(), null);

                                radioBtn.setText(options.getJSONObject(c).optString("value"));
                                radioBtn.setTextSize(20);
                                radioGroup.addView(radioBtn);
                            }
                            apiPostFormObject.put(formItem.getString("variable"), "");
                            myLayout.addView(txtRadio);
                            myLayout.addView(radioGroup);
                            break;

                        case "subtitle":
                            TextView subtitle = new TextView(getActivity(), null);
                            subtitle.setText(formItem.getString("label"));
                            subtitle.setTextColor(Color.parseColor("#1313D2"));
                            subtitle.setTextSize(20);

                            myLayout.addView(subtitle);
                            break;
                        case "textarea":
                            TextInputLayout inputLayout1 = new TextInputLayout(getActivity(), null, R.attr.myInputStyle);
                            inputLayout1.setLayoutParams(params);
                            TextInputEditText editText1 = new TextInputEditText(getActivity());
                            editText1.setHint(formItem.getString("label"));
                            editText1.setTag(formItem.getString("variable"));
                            apiPostFormObject.put(formItem.getString("variable"), "");
                            inputLayout1.addView(editText1);
                            myLayout.addView(inputLayout1);
                            break;

                        case "grid":
                            GridLayout gl = new GridLayout(getActivity());
                            gl.setColumnCount(3);
                            gl.setRowCount(3);


                            for (int m = 0; m < 9; m++) {
                                TextView tv = new TextView(getActivity());
                                tv.setText("delta *********");
                                tv.setBackground(getResources().getDrawable(
                                        R.drawable.rect));

                                gl.addView(tv);

                            }
                            myLayout.addView(gl);
                            break;

                        case "dropdown":

                            params.setMargins(0, 32, 0, 0);
                            Spinner sp = new Spinner(getActivity(), null);
                            sp.setLayoutParams(params);
                            sp.setTag(formItem.getString("variable"));

                            JSONArray optionss = new JSONArray(formItem.getString("options"));
                            String[] objet = new String[optionss.length()];
                            for (int m = 0; m < optionss.length(); m++) {
                                objet[m] = optionss.getJSONObject(m).getString("value");
                            }
                            ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, objet);
                            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sp.setAdapter(aa);
                            apiPostFormObject.put(formItem.getString("variable"), "");
                            myLayout.addView(sp);
                            break;

                        case "datetime":
                            String tested = formItem.getString("variable");
                            params.setMargins(0, 32, 0, 0);
                            apiPostFormObject.put(formItem.getString("variable"), "");
                            if (tested.equals("date")) {

                                mDisplayDate = new TextView(getActivity());
                                mDisplayDate.setTag(formItem.getString("variable"));


                                mDisplayDate.setLayoutParams(params);
                                mDisplayDate.setText("Date");
                                mDisplayDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                mDisplayDate.setBackground(getResources().getDrawable(
                                        R.drawable.rect));

                                mDisplayDate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Calendar cal = Calendar.getInstance();
                                        int year = cal.get(Calendar.YEAR);
                                        int month = cal.get(Calendar.MONTH);
                                        int day = cal.get(Calendar.DAY_OF_MONTH);

                                        DatePickerDialog dialog = new DatePickerDialog(
                                                getActivity(),
                                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                                mDateSetListener,
                                                year, month, day);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.show();
                                    }
                                });

                                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        month = month + 1;


                                        String date = month + "/" + day + "/" + year;
                                        mDisplayDate.setText(date);
                                    }
                                };
                                apiPostFormObject.put(formItem.getString("variable"), "");

                                myLayout.addView(mDisplayDate);
                            } else if (tested.equals("heure")) {


                                mDisplayTime = new TextView(getActivity());
                                mDisplayTime.setTag(formItem.getString("variable"));


                                mDisplayTime.setLayoutParams(params);
                                mDisplayTime.setText("De");
                                mDisplayTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                mDisplayTime.setBackground(getResources().getDrawable(
                                        R.drawable.rect));
                                mDisplayTime.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Calendar c = Calendar.getInstance();
                                        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                                        int minute = c.get(Calendar.MINUTE);
                                        TimePickerDialog dialoge = new TimePickerDialog(
                                                getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                                mDisplayTime.setText("Hour: " + hourOfDay + " Minute: " + minute);
                                            }
                                        }, hourOfDay, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
                                        dialoge.show();
                                    }
                                });

                                myLayout.addView(mDisplayTime);
                            } else {
                                mDisplayTime1 = new TextView(getActivity());
                                mDisplayTime1.setTag(formItem.getString("variable"));
                                mDisplayTime1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                mDisplayTime1.setLayoutParams(params);
                                mDisplayTime1.setText("à");
                                mDisplayTime1.setBackground(getResources().getDrawable(
                                        R.drawable.rect));

                                mDisplayTime1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Calendar c = Calendar.getInstance();
                                        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                                        int minute = c.get(Calendar.MINUTE);
                                        TimePickerDialog dialogee = new TimePickerDialog(
                                                getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                                mDisplayTime1.setText("Hour: " + hourOfDay + " Minute: " + minute);
                                            }
                                        }, hourOfDay, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
                                        dialogee.show();
                                    }
                                });
                                myLayout.addView(mDisplayTime1);
                            }
                            break;

                        case "button":
                            params.setMargins(0, 32, 0, 0);
                            MaterialButton button1 = new MaterialButton(getActivity());
                            button1.setText(formItem.getString("label"));
                            button1.setLayoutParams(params);
                            myLayout.addView(button1);
                            break;
                        case "image":
                            ImageView iv = new ImageView(getActivity());
                            new DownloadImageTask(iv).execute(formItem.getString("src"));
                            myLayout.addView(iv);

                            break;

                        case "submit":
                            params.setMargins(0, 32, 0, 0);
                            MaterialButton button = new MaterialButton(getActivity());
                            button.setText(formItem.getString("label"));
                            button.setLayoutParams(params);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Iterator<String> temp = apiPostFormObject.keys();

                                    for (int i = 0; i < myLayout.getChildCount(); i++) {


                                        View view = myLayout.getChildAt(i);
                                        Log.d(TAG, "onClick: " + view.getClass().getSimpleName());
                                        switch (view.getClass().getSimpleName()) {
                                            case "TextInputLayout":
                                                String value = ((TextInputLayout) view).getEditText().getText().toString();
                                                String key = temp.next();
                                                Log.d(TAG, "textinp: " + key);

                                                try {
                                                    apiPostFormObject.put(key, value);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case "TextView":
                                                TextView textView = (TextView) view;
                                                if (textView.getTag() != null) {
                                                    String value5 = textView.getText().toString();
                                                    String key5 = temp.next();
                                                    Log.d(TAG, "textview: " + key5);
                                                    try {
                                                        apiPostFormObject.put(key5, value5);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                break;
                                            case "Spinner":
                                                String value1;
                                                try {
                                                    value1 = ((Spinner) view).getSelectedItem().toString();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    value1 = "";
                                                }
                                                String key1 = temp.next();
                                                Log.d(TAG, "spinner: " + key1);

                                                try {
                                                    apiPostFormObject.put(key1, value1);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case "RadioGroup":
                                                String value2 = ((RadioButton) getActivity().findViewById(((RadioGroup) view).getCheckedRadioButtonId())).getText().toString();
                                                String key2 = temp.next();
                                                Log.d(TAG, "radgp: " + key2);

                                                try {
                                                    apiPostFormObject.put(key2, value2);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;


                                        }
                                    }

                                    JSONArray variables = new JSONArray();
                                    variables.put(apiPostFormObject);
                                    Log.d(TAG, "erreur" + variables);
                                    NewProcess body = null;
                                    try {
                                        body = new NewProcess(pro_uid, tas_uid, variables.getJSONObject(0));
                                        Log.d(TAG, "onClick: " + body);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    Call<PostForm> call = userClient.postCase(token, body);
                                    call.enqueue(new Callback<PostForm>() {
                                        @Override
                                        public void onResponse(Call<PostForm> call, Response<PostForm> response) {
                                            if (response.isSuccessful()) {

                                                Snackbar snackbar = Snackbar
                                                        .make(getView(), "record saved ", Snackbar.LENGTH_LONG);

                                                snackbar.show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<PostForm> call, Throwable t) {

                                            System.out.println(t.toString());
                                        }
                                    });
                                }
                            });


                            myLayout.addView(button);
                            break;

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        ScrollView scrollView = new ScrollView(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myLayout.setLayoutParams(layoutParams);
        scrollView.addView(myLayout);
        return scrollView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
