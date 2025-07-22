package com.prm.payment.checkout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.common.MainViewModel;
import com.prm.common.Navigator;
import com.prm.payment.R;
import com.prm.payment.zalo.Api.CreateOrder;

import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

@AndroidEntryPoint
public class CheckoutFragment extends Fragment {

    private CheckoutViewModel mViewModel;

    private MainViewModel mainViewModel;

    private Button btnContinue;

    @Inject
    Navigator navigator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        btnContinue = view.findViewById(R.id.btn_continue);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        // handle CreateOrder

        btnContinue.setOnClickListener(v -> {
            CreateOrder orderApi = new CreateOrder();

            try {
                JSONObject data = orderApi.createOrder("59000");
                String code = data.getString("return_code");
                String token = data.getString("zp_trans_token");

                Log.d("ZaloPay", "Create Order Response: " + data.toString());
                Log.d("ZaloPay", "Return Code: " + code);
                Log.d("ZaloPay", "Trans Token: " + token);

                Toast.makeText(requireActivity(), "return_code: " + code, Toast.LENGTH_LONG).show();

                if (code.equals("1")) {
                    Log.d("ZaloPay", "Starting payment process...");
                    processPayment(token);
                } else {
                    Log.e("ZaloPay", "Create order failed with code: " + code);
                    Toast.makeText(requireActivity(), "Create order failed: " + code, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("ZaloPay", "Error creating order", e);
                Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        return  view;
    }

    private void processPayment(String token) {
        Log.d("ZaloPay", "Processing payment with token: " + token);
        Log.d("ZaloPay", "Deep link: demozpdk://app");

        ZaloPaySDK.getInstance().payOrder(requireActivity(), token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    mViewModel.addPremiumUser(user.getUid());
                    mainViewModel.refreshUserStatus();
                    navigator.navigate(com.prm.common.R.string.route_payment_success);
                }
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                new AlertDialog.Builder(requireActivity())
                        .setTitle("User Cancel Payment")
                        .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                String errorMessage;
                if (zaloPayError == ZaloPayError.PAYMENT_APP_NOT_FOUND) {
                    errorMessage = "ZaloPay app not found. Please install ZaloPay app from Google Play Store to continue payment.";
                } else {
                    errorMessage = String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken);
                }

                new AlertDialog.Builder(requireActivity())
                        .setTitle("Payment Fail")
                        .setMessage(errorMessage)
                        .setPositiveButton("Install ZaloPay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Open Google Play Store to install ZaloPay
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("market://details?id=com.vng.zalopay"));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    // Fallback to web browser
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.vng.zalopay"));
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

    }
}