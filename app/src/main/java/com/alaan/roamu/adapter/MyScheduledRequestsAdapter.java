package com.alaan.roamu.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.acitivities.HomeActivity;
import com.alaan.roamu.custom.Utils;
import com.alaan.roamu.fragement.AcceptedDetailFragment;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.pojo.firebaseRide;
import com.alaan.roamu.session.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MyScheduledRequestsAdapter extends RecyclerView.Adapter<MyScheduledRequestsAdapter.Holder> {
    List<PendingRequestPojo> list;

    TextView date_time_search;
    private String date_time_value;
    String date_time = "";
    String time_value = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

    public MyScheduledRequestsAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.scheduled_request_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final PendingRequestPojo pojo = list.get(position);
        Utils utils = new Utils();
        holder.from_add.setText(pojo.getpickup_address());
        holder.to_add.setText(pojo.getDrop_address());
        holder.drivername.setText(pojo.getDriver_name());
        holder.time.setText(pojo.getTime());
        holder.date.setText(pojo.getDate());

        if (pojo.ride_type.equalsIgnoreCase("1")) {
            holder.switchCompat.setChecked(true);
            DrawableCompat.setTintList(DrawableCompat.wrap(holder.switchCompat.getThumbDrawable()), new ColorStateList(holder.states, holder.thumbColors));
        }

        holder.Post_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.itemView);
                popup.inflate(R.menu.post_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_edit:
                                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(view.getContext());
                                LayoutInflater li = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View mView = li.inflate(R.layout.dialog_update_scheduled_ride_layout, null);

                                TextView pickup_location = (TextView) mView.findViewById(R.id.pickup_search_location);
                                TextView drop_location = (TextView) mView.findViewById(R.id.search_drop_location);
                                date_time_search = (TextView) mView.findViewById(R.id.Time_date_search);
                                final EditText mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
                                final EditText etNotes = (EditText) mView.findViewById(R.id.etNotes);
                                final EditText mPickupPoint = (EditText) mView.findViewById(R.id.etPickupPoint);

                                pickup_location.setText(pojo.getpickup_address());
                                drop_location.setText(pojo.getDrop_address());
                                date_time_search.setText(pojo.getDate() + " " + pojo.getTime());
                                mPassengers.setText(pojo.getBooked_set());
                                etNotes.setText(pojo.ride_notes);
                                mPickupPoint.setText(pojo.ride_pickup_point);

                                Button btnSubmit_DUOL = (Button) mView.findViewById(R.id.btnSubmit_DUOL);
                                Button btnCancel_DUOL = (Button) mView.findViewById(R.id.btnCancel_DUOL);

                                date_time_search.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        datePicker(view);
                                    }
                                });

                                mBuilder.setView(mView);
                                final android.app.AlertDialog dialog = mBuilder.create();
                                dialog.show();

                                btnSubmit_DUOL.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        update_scheduled_travels(pojo.getRide_id(), pojo.getpickup_address(), pojo.getDrop_address(), pojo.getpickup_location(), pojo.getdrop_location(), mPassengers.getText().toString(),
                                                date_time_value, time_value, mPickupPoint.getText().toString(), "", String.valueOf(etNotes.getText()));
                                        dialog.dismiss();
                                    }
                                });
                                btnCancel_DUOL.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                return true;

                            case R.id.nav_delete:
                                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
                                alertDialog.setTitle(view.getContext().getString(R.string.do_you_want_to_delete));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, view.getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, view.getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        delete_scheduled_travels(pojo.getRide_id());
                                        dialog.dismiss();
                                    }
                                });

                                alertDialog.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.itemView);
                popup.inflate(R.menu.post_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_edit:
                                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(view.getContext());
                                LayoutInflater li = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View mView = li.inflate(R.layout.dialog_update_scheduled_ride_layout, null);

                                TextView pickup_location = (TextView) mView.findViewById(R.id.pickup_search_location);
                                TextView drop_location = (TextView) mView.findViewById(R.id.search_drop_location);
                                date_time_search = (TextView) mView.findViewById(R.id.Time_date_search);
                                final EditText mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
                                final EditText etNotes = (EditText) mView.findViewById(R.id.etNotes);
                                final EditText mPickupPoint = (EditText) mView.findViewById(R.id.etPickupPoint);

                                pickup_location.setText(pojo.getpickup_address());
                                drop_location.setText(pojo.getDrop_address());
                                date_time_search.setText(pojo.getDate() + " " + pojo.getTime());
                                mPassengers.setText(pojo.getBooked_set());
                                etNotes.setText(pojo.ride_notes);
                                mPickupPoint.setText(pojo.ride_pickup_point);

                                Button btnSubmit_DUOL = (Button) mView.findViewById(R.id.btnSubmit_DUOL);
                                Button btnCancel_DUOL = (Button) mView.findViewById(R.id.btnCancel_DUOL);

                                date_time_search.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        datePicker(view);
                                    }
                                });

                                mBuilder.setView(mView);
                                final android.app.AlertDialog dialog = mBuilder.create();
                                dialog.show();

                                btnSubmit_DUOL.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        update_scheduled_travels(pojo.getRide_id(), pojo.getpickup_address(), pojo.getDrop_address(), pojo.getpickup_location(), pojo.getdrop_location(), mPassengers.getText().toString(),
                                                date_time_value, time_value, mPickupPoint.getText().toString(), "", String.valueOf(etNotes.getText()));
                                        dialog.dismiss();
                                    }
                                });
                                btnCancel_DUOL.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                return true;

                            case R.id.nav_delete:
                                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
                                alertDialog.setTitle(view.getContext().getString(R.string.do_you_want_to_delete));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, view.getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, view.getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        delete_scheduled_travels(pojo.getRide_id());
                                        dialog.dismiss();
                                    }
                                });

                                alertDialog.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utils.haveNetworkConnection(buttonView.getContext())) {
                    DrawableCompat.setTintList(DrawableCompat.wrap(holder.switchCompat.getThumbDrawable()), new ColorStateList(holder.states, holder.thumbColors));

                    if (isChecked) {
                        travel_type_change(holder, pojo.getRide_id(), "1", false);
                    } else {
                        travel_type_change(holder, pojo.getRide_id(), "0", false);
                    }

                } else {
                    Toast.makeText(buttonView.getContext(), buttonView.getContext().getString(R.string.network_not_available), Toast.LENGTH_LONG).show();

                }
            }
        });

        BookFont(holder, holder.f);
        BookFont(holder, holder.t);
        BookFont(holder, holder.dn);
        BookFont(holder, holder.dt);
        MediumFont(holder, holder.from_add);
        MediumFont(holder, holder.to_add);
        MediumFont(holder, holder.date);
    }

    public void travel_type_change(final MyScheduledRequestsAdapter.Holder holder, String ride_id, String status, Boolean what) {
        Log.i("ibrahim","travel_type_change");

        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("ride_type", status);
        Server.setHeader(SessionManager.getKEY());
        Server.post("api/user/scheduled_ride_type_change", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Log.i("ibrahim","status");

                        if (response.has("ride_id")) {
                            String ride_id = response.getString("ride_id");
                            if(ride_id != null && !ride_id.contains("null")){
                                addRideFirebase(ride_id, "", "REQUESTED", "", "");
                            }
                        }

                        if (what) {
                        } else {
                            if (status.equals("1")) {
                                holder.switchCompat.setChecked(true);
                            } else {
                                holder.switchCompat.setChecked(false);
                            }
                        }

                    } else {
//                        Toast.makeText(getContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
//                    Toast.makeText(getContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Log.e("FAIl", throwable.toString() + ".." + errorResponse);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("FAIl", throwable.toString() + ".." + errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("FAIl", throwable.toString() + ".." + responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    public void addRideFirebase(String ride_id_param, String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(String.valueOf(ride_id_param));
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_status", ride_status);
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    public void update_scheduled_travels(String ride_id, String pickup_address, String drop_address, String pickup_location, String drop_location, String a_set, String s_date, String s_time, String PickupPoint, String pickup_point_location, String tr_notes) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("booked_set", a_set);
        params.put("date", s_date);
        params.put("time", s_time);
        params.put("ride_notes", tr_notes);
        params.put("ride_pickup_point", PickupPoint);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post("api/user/update_scheduled_rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "response.toString()");
                Log.i("ibrahim", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());

                        if (response.has("data") && response.getJSONArray("data").length() > 0) {

                        }
                    } else {
                        String data = response.getJSONObject("data").toString();
//                        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    public void delete_scheduled_travels(String ride_id) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post("api/user/delete_scheduled_rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                    } else {
                        String data = response.getJSONObject("data").toString();
//                        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    private void datePicker(View view) {

//        setLocale("en", getActivity());
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                        date_time = String.format("%04d-%02d-%02d", year, 1 + monthOfYear, dayOfMonth);
                        date_time = formatDateWithPattern1(String.format("%04d-%02d-%02d", year, 1 + monthOfYear, dayOfMonth));
                        Log.i("ibrahim", date_time);
                        //date_time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        tiemPicker(view);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker(View view) {
//        setLocale("en", getActivity());
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
//                        date_time_value = date_time + " " + hourOfDay + ":" + minute;
                        date_time_value = date_time + " " + formatDateWithPattern2(String.format("%02d:%02d", hourOfDay, minute));
                        time_value = hourOfDay + ":" + minute;
                        date_time_search.setText(date_time + " " + formatDateWithPattern2(String.format("%02d:%02d", hourOfDay, minute)));
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    static String formatDateWithPattern1(String strDate) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = fmt.parse(strDate);
            return fmt.format(date);
        } catch (ParseException pe) {
            return "Date";
        }
    }

    static String formatDateWithPattern2(String strDate) {
        SimpleDateFormat fmt = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        try {
            Date date = fmt.parse(strDate);
            return fmt.format(date);
        } catch (ParseException pe) {
            return "Date";
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView drivername, from_add, to_add, date, time;
        TextView f, t, dn, dt;
        ImageView Post_more;
        Switch switchCompat;
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{
                Color.RED,
                Color.GREEN,
        };

        public Holder(View itemView) {
            super(itemView);
            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);
            dn = (TextView) itemView.findViewById(R.id.drivername);
            dt = (TextView) itemView.findViewById(R.id.datee);
            drivername = (TextView) itemView.findViewById(R.id.txt_drivername);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            Post_more = (ImageView) itemView.findViewById(R.id.img);
            switchCompat = (Switch) itemView.findViewById(R.id.travel_schedule);
            switchCompat.setChecked(false);
            DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getThumbDrawable()), new ColorStateList(states, thumbColors));
        }
    }

    public void BookFont(Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}