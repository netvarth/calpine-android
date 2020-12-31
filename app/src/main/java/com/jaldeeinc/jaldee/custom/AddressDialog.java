package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IAddressInterface;
import com.jaldeeinc.jaldee.Interface.IEditAddress;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.AddressAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.Address;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressDialog extends Dialog implements IEditAddress {

    private Context context;
    private IAddressInterface iAddressInterface;
    private IEditAddress iEditAddress;
    ArrayList<Address> addressList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AddressAdapter addressAdapter;
    private int position;

    @BindView(R.id.rv_address)
    RecyclerView rvAddress;

    @BindView(R.id.ll_addNewAddress)
    LinearLayout llAddNewAddress;

    @BindView(R.id.ll_newAddressLayout)
    LinearLayout llNewAddressLayout;

    @BindView(R.id.tv_save)
    CustomTextViewSemiBold tvSave;

    @BindView(R.id.tv_title)
    CustomTextViewSemiBold tvTitle;

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

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    Animation animShake, slideUp, slideRight;
    private boolean isEdit = false;


    public AddressDialog(@NonNull Context context, ArrayList<Address> addressList, IAddressInterface iAddressInterface) {
        super(context);
        this.context = context;
        this.addressList = addressList;
        this.iAddressInterface = iAddressInterface;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_dialog);
        ButterKnife.bind(this);
        iEditAddress = this;

        animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        slideRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);

        if (addressList != null && addressList.size() > 0) {
            linearLayoutManager = new LinearLayoutManager(context);
            rvAddress.setLayoutManager(linearLayoutManager);
            addressAdapter = new AddressAdapter(addressList, context, false, iEditAddress);
            rvAddress.setAdapter(addressAdapter);
            showListOfAddresses();

        } else {

            showAddNewAddress();
        }


        llAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isEdit = false;
                showAddNewAddress();
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getAddressList();

            }
        });


        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEdit) {

                    validation(true);

                } else {

                    validation(false);
                }
            }
        });

    }

    private void validation(boolean isEdit) {

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etMailId.getText().toString();
        String mobileNumber = etMobile.getText().toString();
        String address = etAreaAndFlat.getText().toString();
        String city = etCity.getText().toString();
        String pinCode = etPinCode.getText().toString();
        String landMark = etLandMark.getText().toString();

        if (!firstName.trim().equalsIgnoreCase("") && !lastName.trim().equalsIgnoreCase("") && !email.trim().equalsIgnoreCase("") && !mobileNumber.trim().equalsIgnoreCase("") && !address.trim().equalsIgnoreCase("") && !city.trim().equalsIgnoreCase("") && !pinCode.trim().equalsIgnoreCase("") && !landMark.trim().equalsIgnoreCase("")) {

            if (mobileNumber.trim().length() > 9) {
                tvErrorMessage.setVisibility(View.GONE);
                if (isEdit) {

                    saveEditedAddress(firstName, lastName, email, mobileNumber, address, city, pinCode, landMark);

                } else {
                    saveAddress(firstName, lastName, email, mobileNumber, address, city, pinCode, landMark);
                }
            } else {

                tvErrorMessage.setVisibility(View.VISIBLE);
                tvErrorMessage.setText("Enter valid mobile number");
                tvErrorMessage.setAnimation(animShake);
            }
        } else {

            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("* All fields are mandatory");
            tvErrorMessage.setAnimation(animShake);
        }
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
        Call<ResponseBody> call = apiService.getDeliveryAddress(addressList);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (response.code() == 200) {
                        getAddressList();
                        clearAllEditTexts();
                    }

                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });

    }

    private void saveEditedAddress(String firstName, String lastName, String email, String mobileNumber, String address, String city, String pinCode, String landMark) {

        addressList.get(position).setFirstName(firstName);
        addressList.get(position).setLastName(lastName);
        addressList.get(position).setEmail(email);
        addressList.get(position).setPhoneNumber(mobileNumber);
        addressList.get(position).setAddress(address);
        addressList.get(position).setCity(city);
        addressList.get(position).setPostalCode(pinCode);
        addressList.get(position).setLandMark(landMark);

        ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.getDeliveryAddress(addressList);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (response.code() == 200) {
                        getAddressList();
                        clearAllEditTexts();
                    }

                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });

    }

    private void getAddressList() {
        ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ArrayList<Address>> call = apiService.getDeliveryAddress();
        call.enqueue(new Callback<ArrayList<Address>>() {
            @Override
            public void onResponse(Call<ArrayList<Address>> call, Response<ArrayList<Address>> response) {

                try {

                    if (response.code() == 200) {
                        addressList.clear();
                        addressList = response.body();

                        if (addressList != null && addressList.size() > 0) {

                            showListOfAddresses();

                            linearLayoutManager = new LinearLayoutManager(context);
                            rvAddress.setLayoutManager(linearLayoutManager);
                            addressAdapter = new AddressAdapter(addressList, context, false, iEditAddress);
                            rvAddress.setAdapter(addressAdapter);

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


    private void showListOfAddresses() {

        llNewAddressLayout.startAnimation(slideRight);
        llNewAddressLayout.setVisibility(View.GONE);
        rvAddress.setVisibility(View.VISIBLE);
        llAddNewAddress.setVisibility(View.VISIBLE);
        rvAddress.startAnimation(slideUp);
        llBack.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
    }

    private void showAddNewAddress() {

        rvAddress.setVisibility(View.GONE);
        rvAddress.startAnimation(slideRight);
        llAddNewAddress.setVisibility(View.GONE);
        llNewAddressLayout.setVisibility(View.VISIBLE);
        llNewAddressLayout.startAnimation(slideUp);
        tvTitle.setVisibility(View.GONE);
        llBack.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAddressClick(Address address) {

        iAddressInterface.onSelectAddress(address);
        dismiss();
    }

    @Override
    public void onEditClick(Address address, int addressPosition) {

        position = addressPosition;   // this position is the position where we have to save the data in list
        isEdit = true;  // to check if it is from edit or add New
        showAddNewAddress();
        etFirstName.setText(address.getFirstName());
        etLastName.setText(address.getLastName());
        etAreaAndFlat.setText(address.getAddress());
        etCity.setText(address.getCity());
        etLandMark.setText(address.getLandMark());
        etPinCode.setText(address.getPostalCode());
        etMailId.setText(address.getEmail());
        etMobile.setText(address.getPhoneNumber());


    }

    private void clearAllEditTexts() {

        etFirstName.setText("");
        etLastName.setText("");
        etAreaAndFlat.setText("");
        etMobile.setText("");
        etMailId.setText("");
        etPinCode.setText("");
        etLandMark.setText("");
        etCity.setText("");
    }
}
