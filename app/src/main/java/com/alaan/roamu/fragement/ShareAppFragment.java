package com.alaan.roamu.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamu.R;
import com.alaan.roamu.Server.Server;
import com.alaan.roamu.pojo.PendingRequestPojo;
import com.alaan.roamu.pojo.promopojo;
import com.alaan.roamu.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.concurrent.ThreadLocalRandom;

import cz.msebera.android.httpclient.Header;

public class ShareAppFragment extends Fragment {

    View rootView;
    TextView textViewHeader, textViewBody, promocode;
    Button mSubmit;
    promopojo pojo;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_app, container, false);
        textViewHeader = (TextView) rootView.findViewById(R.id.textViewHeader);
        textViewBody = (TextView) rootView.findViewById(R.id.textViewBody);
        promocode = (TextView) rootView.findViewById(R.id.promocode);
        mSubmit = (Button) rootView.findViewById(R.id.FN_BTN);
        bundle = getArguments();
        if (bundle != null) {
            pojo = (promopojo) bundle.getSerializable("data");
            textViewHeader.setText(pojo.promo_desc);
            textViewBody.setText(pojo.promo_message);
            int randomNum = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                randomNum = ThreadLocalRandom.current().nextInt(999999, 999999999 + 1);
                promocode.setText(String.valueOf(randomNum));
            }
            int finalRandomNum = randomNum;
            mSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ApplyPromoCode(finalRandomNum);

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "roamu");
                        String shareMessage = "\nLet me recommend you this application\n\n";
                        if (finalRandomNum > 0) {
                            shareMessage = shareMessage + "You will have your dissacount when you install roamu" + "\n\n";
                            shareMessage = shareMessage + "please enter this Coupon after installing roamu: " + finalRandomNum + "\n\n";
                        }
                        shareMessage = shareMessage + "http://onelink.to/ve7c4k" + "\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));
                    } catch (Exception e) {
                        //e.toString();
                    }
                }
            });
        }
        return rootView;
    }

    public void ApplyPromoCode(int code) {
        RequestParams params = new RequestParams();
        params.put("promocode", code);
        params.put("wallet_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        String Url = "";

        Server.get("api/user/applyPromoCode/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }
}