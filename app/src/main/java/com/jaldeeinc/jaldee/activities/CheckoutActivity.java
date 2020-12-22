package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;

import com.jaldeeinc.jaldee.Interface.IAddressInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.AddressAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.Address;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private Context mContext;
    private IAddressInterface iAddressInterface;
    ArrayList<Address> addressList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapter;

    @BindView(R.id.tv_name)
    CustomTextViewMedium tvName;

    @BindView(R.id.tv_number)
    CustomTextViewMedium tvMobileNumber;

    @BindView(R.id.tv_mailId)
    CustomTextViewMedium tvEmailId;

    @BindView(R.id.tv_address)
    CustomTextViewMedium tvDeliveryAddress;

    @BindView(R.id.tv_changeAddress)
    CustomTextViewSemiBold tvChangeAddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(CheckoutActivity.this);
        mContext = CheckoutActivity.this;


        getAddressList();
    }


    private void getAddressList() {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<Address>> call = apiService.getDeliveryAddress(addressList);
        call.enqueue(new Callback<ArrayList<Address>>() {
            @Override
            public void onResponse(Call<ArrayList<Address>> call, Response<ArrayList<Address>> response) {

                try {

                    if (response.code() == 200) {
                        addressList = response.body();

                        if (addressList != null && addressList.size() > 0) {

                            updateUI(addressList);

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Address>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });
    }

    private void updateUI(ArrayList<Address> addressList) {

        if (addressList != null && addressList.size() >0){

            tvName.setText(addressList.get(0).getFirstName() +" "+ addressList.get(0).getLastName());
            tvEmailId.setText(addressList.get(0).getEmail());
            tvMobileNumber.setText(addressList.get(0).getAddress());
            String address = addressList.get(0).getLandMark()+","+ addressList.get(0).getAddress()+","+ addressList.get(0).getCity()+ ","+ addressList.get(0).getPostalCode();
            tvDeliveryAddress.setText(address);

        }
    }

}