package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IAddressInterface;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ItemsActivity;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.adapter.AddressAdapter;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.adapter.VirtualFieldAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.Address;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressDialog extends Dialog {

    private Context context;
    private IAddressInterface iAddressInterface;
    ArrayList<Address> addressList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapter;

    @BindView(R.id.rv_address)
    RecyclerView rvAddress;

    @BindView(R.id.ll_addNewAddress)
    LinearLayout llAddNewAddress;

    @BindView(R.id.ll_newAddressLayout)
    LinearLayout llNewAddressLayout;

    @BindView(R.id.tv_save)
    CustomTextViewSemiBold tvSave;

    @BindView(R.id.et_firstName)
    CustomEditTextRegular etFirstName;

    @BindView(R.id.et_lastName)
    CustomEditTextRegular etLastName;

    @BindView(R.id.et_areaAndFlat)
    CustomEditTextRegular etAreaAndFlat;

    @BindView(R.id.et_city)
    CustomEditTextRegular etCity;

    @BindView(R.id.et_pincode)
    CustomEditTextRegular etPinCode;

    @BindView(R.id.et_landmark)
    CustomEditTextRegular etLandMark;

    @BindView(R.id.et_mobile)
    CustomEditTextRegular etMobile;

    @BindView(R.id.et_email)
    CustomEditTextRegular etMailId;

    @BindView(R.id.tv_errorMessage)
    CustomTextViewItalicSemiBold tvErrorMessage;

    Animation animShake, slideUp, slideRight;


    public AddressDialog(@NonNull Context context, ArrayList<Address> addressList) {
        super(context);
        this.context = context;
        this.addressList = addressList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_dialog);
        ButterKnife.bind(this);

        animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        slideRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
        linearLayoutManager = new LinearLayoutManager(context);
        rvAddress.setLayoutManager(linearLayoutManager);
        addressAdapter = new AddressAdapter(addressList, context, true, iAddressInterface);
        rvAddress.setAdapter(addressAdapter);


        // api call
        getAddressList();

        llAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUI();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String email = etMailId.getText().toString();
                String mobileNumber = etMobile.getText().toString();
                String address = etAreaAndFlat.getText().toString();
                String city = etCity.getText().toString();
                String pinCode = etPinCode.getText().toString();
                String landMark = etLandMark.getText().toString();

                if (!firstName.trim().equalsIgnoreCase("") && !lastName.trim().equalsIgnoreCase("") && !email.trim().equalsIgnoreCase("") && !mobileNumber.trim().equalsIgnoreCase("") && !address.trim().equalsIgnoreCase("") && !city.trim().equalsIgnoreCase("") && !pinCode.trim().equalsIgnoreCase("") && !landMark.trim().equalsIgnoreCase("")) {

                    tvErrorMessage.setVisibility(View.GONE);
                    saveAddress(firstName, lastName, email, mobileNumber, address, city, pinCode, landMark);
                } else {

                    tvErrorMessage.setVisibility(View.VISIBLE);
                    tvErrorMessage.setAnimation(animShake);
                }

            }
        });

    }

    private void saveAddress(String firstName, String lastName, String email, String mobileNumber, String address, String city, String pinCode, String landMark) {

        Address obj = new Address();
        obj.setFirstName(firstName);
        obj.setLastName(lastName);
        obj.setEmail(email);
        obj.setPhoneNumber(mobileNumber);
        obj.setAddress(address);
        obj.setCity(city);
        obj.setPostalCode(pinCode);
        obj.setLandMark(landMark);
        addressList.add(obj);

        ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ArrayList<Address>> call = apiService.getDeliveryAddress(addressList);
        call.enqueue(new Callback<ArrayList<Address>>() {
            @Override
            public void onResponse(Call<ArrayList<Address>> call, Response<ArrayList<Address>> response) {

                try {

                    if (response.code() == 200) {
                        addressList = response.body();

                        if (addressList != null && addressList.size() > 0) {

                            linearLayoutManager = new LinearLayoutManager(context);
                            rvAddress.setLayoutManager(linearLayoutManager);
                            addressAdapter = new AddressAdapter(addressList, context, false, iAddressInterface);
                            rvAddress.setAdapter(addressAdapter);

                            updateUI();
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

    private void getAddressList() {
        ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ArrayList<Address>> call = apiService.getDeliveryAddress(addressList);
        call.enqueue(new Callback<ArrayList<Address>>() {
            @Override
            public void onResponse(Call<ArrayList<Address>> call, Response<ArrayList<Address>> response) {

                try {

                    if (response.code() == 200) {
                        addressList = response.body();

                        if (addressList != null && addressList.size() > 0) {

                            linearLayoutManager = new LinearLayoutManager(context);
                            rvAddress.setLayoutManager(linearLayoutManager);
                            addressAdapter = new AddressAdapter(addressList, context, false, iAddressInterface);
                            rvAddress.setAdapter(addressAdapter);

                            updateUI();
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

    private void updateUI() {

        if (addressList != null && addressList.size() > 0) {

            llNewAddressLayout.startAnimation(slideRight);
            llNewAddressLayout.setVisibility(View.GONE);
            rvAddress.setVisibility(View.VISIBLE);
            llAddNewAddress.setVisibility(View.VISIBLE);
            rvAddress.startAnimation(slideUp);

        } else {

            rvAddress.setVisibility(View.GONE);
            rvAddress.startAnimation(slideRight);
            llAddNewAddress.setVisibility(View.GONE);
            llNewAddressLayout.setVisibility(View.VISIBLE);
            llNewAddressLayout.startAnimation(slideUp);

        }
    }

}
