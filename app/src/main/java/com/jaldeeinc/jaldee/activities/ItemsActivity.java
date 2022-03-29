package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.Interface.IDeleteImagesInterface;
import com.jaldeeinc.jaldee.Interface.IDialogInterface;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailFileImageAdapter;
import com.jaldeeinc.jaldee.adapter.ImagePreviewAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AutofitTextView;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.SelectedItemsDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class ItemsActivity extends AppCompatActivity implements IItemInterface, IDialogInterface, IDeleteImagesInterface, ISaveNotes {

    @BindView(R.id.tv_homeDeliveryRadiuse)
    CustomTextViewItalicSemiBold tvHomeDeliveryRadiuse;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.tv_catalogName)
    CustomTextViewSemiBold tvCataLogName;

    @BindView(R.id.tv_description)
    CustomTextViewMedium tvDescription;

    @BindView(R.id.ll_homeDelivery)
    LinearLayout llHomeDelivery;

    @BindView(R.id.ll_storePickup)
    LinearLayout llStorePickup;

    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    @BindView(R.id.ll_noItems)
    LinearLayout llNoItems;

    @BindView(R.id.iv_catalogImage)
    BorderImageView ivCatalogImage;

    @BindView(R.id.shimmer)
    SkeletonLoadingView shimmer;

    @BindView(R.id.cv_arrow)
    CardView cvArrow;

    @BindView(R.id.cv_itemsCart)
    CardView cvItemsCart;

    @BindView(R.id.cv_shoppingList)
    CardView cvShoppingList;

    @BindView(R.id.cv_listCheckout)
    CardView cvListCheckOut;

    @BindView(R.id.rv_images)
    RecyclerView rvImages;

    @BindView(R.id.ll_viewcart)
    LinearLayout llViewCart;

    @BindView(R.id.tv_itemsCount)
    CustomTextViewSemiBold tvItemsCount;

    @BindView(R.id.tv_subTotal)
    AutofitTextView tvSubTotal;

    @BindView(R.id.tv_totalDiscount)
    AutofitTextView tvDisCountedPrice;

    private Catalog catalogInfo;
    private Context mContext;
    private int catalogId, accountId, uniqueId;
    private SearchViewDetail mBusinessDataList = new SearchViewDetail();
    private ItemsAdapter itemsAdapter;
    private IItemInterface iItemInterface;
    private IDialogInterface iDialogInterface;
    private GridLayoutManager gridLayoutManager;
    private DatabaseHandler db;
    private SelectedItemsDialog selectedItemsDialog;
    Typeface font_style;
    ArrayList<String> imagePathList = new ArrayList<>();
    ArrayList<String> uploadedImagesList = new ArrayList<>();
    ArrayList<ShoppingListModel> itemList = new ArrayList<>();
    private IDeleteImagesInterface iDeleteImagesInterface;
    private ImagePreviewAdapter mDetailFileAdapter;
    private CustomNotes customNotes;
    private ISaveNotes iSaveNotes;

    //files related
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    String[] imgExtsSupported = new String[]{"jpg", "jpeg", "png"};
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    private int GALLERY = 1, CAMERA = 2;
    private Uri mImageUri;
    File f;
    String path;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        ButterKnife.bind(ItemsActivity.this);
        mContext = ItemsActivity.this;
        iItemInterface = this;
        iDialogInterface = this;
        iDeleteImagesInterface = this;
        iSaveNotes = this;
        db = new DatabaseHandler(ItemsActivity.this);

        Intent i = getIntent();
        mBusinessDataList = (SearchViewDetail) i.getSerializableExtra("providerInfo");
        catalogInfo = (Catalog) i.getSerializableExtra("catalogInfo");
        accountId = i.getIntExtra("accountId", 0);
        uniqueId = i.getIntExtra("uniqueId", 0);

        Typeface font_semiBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
        Typeface font_medium = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-Regular.ttf");

        tvSubTotal.setTypeface(font_medium);
        tvDisCountedPrice.setTypeface(font_semiBold);

        gridLayoutManager = new GridLayoutManager(ItemsActivity.this, 2);
        rvItems.setLayoutManager(gridLayoutManager);
        ArrayList<CatalogItem> catItem = new ArrayList<>();
        for (CatalogItem citem : catalogInfo.getCatalogItemsList()) {  // for remove if item online view from provider is falls
            if (citem.getItems().isShowOnLandingPage()) {
                catItem.add(citem);
            }
        }
        itemsAdapter = new ItemsAdapter(catItem, this, true, iItemInterface, accountId, uniqueId, catalogInfo);
        rvItems.setAdapter(itemsAdapter);

        if (accountId == db.getAccountId()) {
            cvItemsCart.setVisibility(View.VISIBLE);
            tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");
            String amount = String.valueOf(db.getCartPrice());
            tvSubTotal.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));

        } else {

            cvItemsCart.setVisibility(View.GONE);
        }

        // set businessName
        if (mBusinessDataList != null && mBusinessDataList.getBusinessName() != null) {
            tvSpName.setText(mBusinessDataList.getBusinessName());
        }

        if (catalogInfo != null) {
            try {
                if (catalogInfo.getCatalogImagesList() != null && catalogInfo.getCatalogImagesList().size() > 0) {
                    shimmer.setVisibility(View.VISIBLE);
                    PicassoTrustAll.getInstance(ItemsActivity.this).load(catalogInfo.getCatalogImagesList().get(0).getUrl()).into(ivCatalogImage, new Callback() {
                        @Override
                        public void onSuccess() {

                            shimmer.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                            shimmer.setVisibility(View.GONE);
                            ivCatalogImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                        }
                    });
                } else {
                    Glide.with(mContext).load(R.drawable.ic_catalogue).into(ivCatalogImage);
                }

                if (catalogInfo.getCatLogName() != null) {
                    tvCataLogName.setText(catalogInfo.getCatLogName());
                }

                if (catalogInfo.getCatalogDescription() != null) {
                    tvDescription.setVisibility(View.VISIBLE);
                    tvDescription.setText(catalogInfo.getCatalogDescription());
                } else {

                    tvDescription.setVisibility(View.GONE);
                }

                if (catalogInfo.getHomeDelivery() != null && catalogInfo.getHomeDelivery().isHomeDelivery()) {
                    tvHomeDeliveryRadiuse.setText("(In " + catalogInfo.getHomeDelivery().getDeliveryRadius() + "\u00a0km radius)");
                    llHomeDelivery.setVisibility(View.VISIBLE);
                } else {
                    llHomeDelivery.setVisibility(View.GONE);
                }

                if (catalogInfo.getPickUp() != null && catalogInfo.getPickUp().isOrderPickUp()) {
                    llStorePickup.setVisibility(View.VISIBLE);
                } else {
                    llStorePickup.setVisibility(View.GONE);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        cvArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedItemsDialog = new SelectedItemsDialog(mContext, iDialogInterface);
                selectedItemsDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                selectedItemsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                selectedItemsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectedItemsDialog.show();
                selectedItemsDialog.setCancelable(false);
                DisplayMetrics metrics = ItemsActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                selectedItemsDialog.getWindow().setGravity(Gravity.BOTTOM);
                selectedItemsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        llViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ItemsActivity.this, CartActivity.class);
                intent.putExtra("providerAccountId", accountId);
                intent.putExtra("providerInfo", mBusinessDataList);
                intent.putExtra("catalogInfo", catalogInfo);
                intent.putExtra("serviceId", catalogInfo.getCatLogId());

                startActivity(intent);

            }
        });

        cvShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showOptions();
            }
        });

        cvListCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemList != null && itemList.size() > 0) {
                    getQuestionnaire(catalogInfo.getCatLogId(), accountId);
                }

            }
        });

    }

    private void showOptions() {

        try {

            Dialog dialog = new Dialog(ItemsActivity.this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = ItemsActivity.this.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout llGallery = dialog.findViewById(R.id.ll_gallery);
            LinearLayout llCamera = dialog.findViewById(R.id.ll_camera);

            llCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openCamera();
                    dialog.dismiss();
                }
            });

            llGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openGallery();
                    dialog.cancel();
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void refreshData() {

        ArrayList<CatalogItem> catalogItemsList = new ArrayList<>();
        for (CatalogItem citem : catalogInfo.getCatalogItemsList()) {  // for remove if item online view from provider is falls
            if (citem.getItems().isShowOnLandingPage()) {
                catalogItemsList.add(citem);
            }
        }
        catalogItemsList = updateCatalogItemsDiscount(catalogItemsList);
        catalogItemsList = updateCatalogItemsQuantity(catalogItemsList);
        gridLayoutManager = new GridLayoutManager(ItemsActivity.this, 2);
        rvItems.setLayoutManager(gridLayoutManager);
        itemsAdapter = new ItemsAdapter(catalogItemsList, this, false, iItemInterface, accountId, uniqueId, catalogInfo);
        rvItems.setAdapter(itemsAdapter);
        updateCartUI();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (catalogInfo != null && catalogInfo.getOrderType().equalsIgnoreCase(Constants.SHOPPINGCART)) {
            cvShoppingList.setVisibility(View.GONE);
            rvItems.setVisibility(View.VISIBLE);
            refreshData();
        } else {
            rvItems.setVisibility(View.GONE);
            cvItemsCart.setVisibility(View.GONE);
            cvShoppingList.setVisibility(View.VISIBLE);
            // set selected images in adapter
            updateImages();

            if (itemList != null && itemList.size() > 0) {
                cvListCheckOut.setVisibility(View.VISIBLE);
            } else {
                cvListCheckOut.setVisibility(View.GONE);
            }

            requestMultiplePermissions();

        }
    }

    private void updateImages() {

        if (itemList != null && itemList.size() > 0) {

            mDetailFileAdapter = new ImagePreviewAdapter(itemList, mContext, true, iDeleteImagesInterface);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ItemsActivity.this, 2);
            rvImages.setLayoutManager(mLayoutManager);
            rvImages.setAdapter(mDetailFileAdapter);
            mDetailFileAdapter.notifyDataSetChanged();
        }
    }


    private ArrayList<CatalogItem> updateCatalogItemsDiscount(ArrayList<CatalogItem> catalogItemsList) {

        catalogItemsList = catalogItemsList == null ? new ArrayList<>() : catalogItemsList;

        for (CatalogItem catalogItem : catalogItemsList) {

            if (catalogItem.getItems().isShowPromotionalPrice()) {

                catalogItem.getItems().setDiscountedPrice(catalogItem.getItems().getPromotionalPrice());
            } else {

                catalogItem.getItems().setDiscountedPrice(catalogItem.getItems().getPrice());

            }

            if (catalogItem.getItems().getItemImagesList() != null && catalogItem.getItems().getItemImagesList().size() > 0) {
                for (int i = 0; i < catalogItem.getItems().getItemImagesList().size(); i++) {
                    if (catalogItem.getItems().getItemImagesList().get(i).isDisplayImage()) {
                        catalogItem.getItems().setDisplayImage(catalogItem.getItems().getItemImagesList().get(i).getUrl());
                    }
                }
            }


        }

        return catalogItemsList;
    }

    private ArrayList<CatalogItem> updateCatalogItemsQuantity(ArrayList<CatalogItem> catalogItemsList) {

        ArrayList<CartItemModel> cartItemsList = new ArrayList<>();
        catalogItemsList = catalogItemsList == null ? new ArrayList<>() : catalogItemsList;
        cartItemsList = db.getCartItems();
        db.markItemsAsExpired();

        for (CartItemModel cartItem : cartItemsList) {

            for (CatalogItem catalogItem : catalogItemsList) {

                if (cartItem.getItemId() == catalogItem.getItems().getItemId()) {

                    catalogItem.getItems().setItemQuantity(cartItem.getQuantity());
                    CartItemModel item = new CartItemModel(catalogItem.getItems().getItemId(), catalogItem.getItems().getPrice(), catalogItem.getMaxQuantity(), catalogItem.getItems().getDiscountedPrice());
                    item.setQuantity(cartItem.getQuantity());
                    if (catalogItem.getItems().isShowPromotionalPrice()) {
                        item.setIsPromotional(1);
                    }
                    db.updateCartItem(item);
                }
            }
        }

        if (cartItemsList.size() == 0) {

            for (CatalogItem catalogItem : catalogItemsList) {

                catalogItem.getItems().setItemQuantity(0);

            }

        }

        return catalogItemsList;
    }


    @Override
    public void onItemClick(CatalogItem catalogItem) {

        Intent intent = new Intent(ItemsActivity.this, ItemDetailAcitvity.class);
        intent.putExtra("itemInfo", catalogItem);
        intent.putExtra("accountId", accountId);
        intent.putExtra("uniqueId", uniqueId);
        intent.putExtra("catalogInfo", catalogInfo);

        if (catalogInfo != null && catalogInfo.getCatalogItemsList() != null && catalogInfo.getCatalogItemsList().size() > 0) {
            ArrayList<CatalogItem> updatedList = new ArrayList<>();
            for (int i = 0; i < catalogInfo.getCatalogItemsList().size(); i++) {

                if (catalogInfo.getCatalogItemsList().get(i).getId() != catalogItem.getId()) {
                    updatedList.add(catalogInfo.getCatalogItemsList().get(i));
                }
            }
            intent.putExtra("catalogItems", updatedList);
        }
        intent.putExtra("providerInfo", mBusinessDataList);
        startActivity(intent);

    }

    @Override
    public void checkItemQuantity() {

        updateCartUI();

    }

    @Override
    public void onContinueClick() {

        refreshData();
        updateCartUI();
    }

    @Override
    public void onClearClick() {

        removeQuantity();
    }


    private void updateCartUI() {

        if (db.getCartCount() > 0) {

            cvItemsCart.setVisibility(View.VISIBLE);

            tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");

            if (db.getCartPrice() == db.getCartDiscountedPrice()) {

                tvSubTotal.setVisibility(View.GONE);
                tvDisCountedPrice.setVisibility(View.VISIBLE);
                String amount = String.valueOf(db.getCartPrice());
                tvDisCountedPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));

            } else {

                tvSubTotal.setVisibility(View.VISIBLE);
                String amount = String.valueOf(db.getCartPrice());
                tvSubTotal.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));
                tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvDisCountedPrice.setVisibility(View.VISIBLE);
                String discountedPrice = String.valueOf(db.getCartDiscountedPrice());
                tvDisCountedPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(discountedPrice)));
            }
        } else {

            cvItemsCart.setVisibility(View.GONE);
        }
    }


    private void removeQuantity() {

        for (int i = 0; i < catalogInfo.getCatalogItemsList().size(); i++) {

            catalogInfo.getCatalogItemsList().get(i).getItems().setItemQuantity(0);
        }
        refreshData();
    }

    // files related

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();fc
                            Toast.makeText(getApplicationContext(), "You Denied the Permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   mTxvBuy.setEnabled(true);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();

                        String mimeType = this.mContext.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        File photoFile = null;

                        try {
                            // Creating file
                            try {
                                photoFile = Config.createFile(mContext, extension, true);
                            } catch (IOException ex) {
                                Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Error occurred while creating the file");
                            }

                            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            // Copying
                            Config.copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                            //Log.d(TAG, "onActivityResult: " + e.toString());
                        }
                        String orgFilePath = photoFile.getAbsolutePath();
                        if (Arrays.asList(fileExtsSupported).contains(extension)) {
                            if (orgFilePath == null) {
                                orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
                            }
                        } else {
                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ShoppingListModel model = new ShoppingListModel();
                        model.setImagePath(orgFilePath);
                        itemList.add(model);

                        imagePathList.add(orgFilePath);

                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                        rvImages.setLayoutManager(mLayoutManager);
                        rvImages.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();

                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            String mimeType = this.mContext.getContentResolver().getType(uri);
                            String uriString = uri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            File photoFile = null;

                            try {
                                // Creating file
                                try {
                                    photoFile = Config.createFile(mContext, extension, true);
                                } catch (IOException ex) {
                                    Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                    // Log.d(TAG, "Error occurred while creating the file");
                                }

                                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                                // Copying
                                Config.copyStream(inputStream, fileOutputStream);
                                fileOutputStream.close();
                                inputStream.close();
                            } catch (Exception e) {
                                Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                                //Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                            String orgFilePath = photoFile.getAbsolutePath();
                            if (Arrays.asList(fileExtsSupported).contains(extension)) {

                                if (orgFilePath == null) {
                                    orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
                                }
                            } else {
                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ShoppingListModel model = new ShoppingListModel();
                            model.setImagePath(orgFilePath);
                            itemList.add(model);

                            imagePathList.add(orgFilePath);

                        }
                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                        rvImages.setLayoutManager(mLayoutManager);
                        rvImages.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                File photoFile = null;/////////
                // ///////
                try {//////////
                    photoFile = Config.createFile(mContext, "png", true);//////////
                } catch (IOException e) {/////////////
                    e.printStackTrace();///////////
                }///////////
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");/////////
                try (FileOutputStream out = new FileOutputStream(photoFile)) {////////////
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance////////////
                    // PNG is a lossless format, the compression factor (100) is ignored/////////
                } catch (IOException e) {////////////
                    e.printStackTrace();///////////
                }////////
                String path = photoFile.getAbsolutePath();////////

                if (path != null) {
                    mImageUri = Uri.parse(path);
                    ShoppingListModel model = new ShoppingListModel();
                    model.setImagePath(mImageUri.toString());
                    itemList.add(model);

                    imagePathList.add(mImageUri.toString());
                }
                DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                rvImages.setLayoutManager(mLayoutManager);
                rvImages.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();
            }
        }
    }


    private void openGallery() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY);

                    return;
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                }
            } else {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openCamera() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                    requestPermissions(new String[]{
                            Manifest.permission.CAMERA}, CAMERA);

                    return;
                } else {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent cameraIntent = new Intent();
                    cameraIntent.setType("image/*");
                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, CAMERA);
                }
            } else {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent cameraIntent = new Intent();
                cameraIntent.setType("image/*");
                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CAMERA);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void delete(int position, String imagePath) {
        itemList.remove(position);
        mDetailFileAdapter.notifyDataSetChanged();

        if (itemList != null && itemList.size() > 0) {

            for (int i = 0; i < itemList.size(); i++) {

                if (itemList.get(i).getImagePath().equalsIgnoreCase(imagePath)) {

                    itemList.remove(i);
                }
            }
        }
    }

    @Override
    public void addedNotes(int position) {

        showNotesDialog(position);
    }

    private void showNotesDialog(int position) {

        customNotes = new CustomNotes(mContext, position, iSaveNotes, itemList.get(position).getCaption());
        customNotes.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        customNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customNotes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customNotes.setCancelable(false);
        customNotes.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        customNotes.getWindow().setGravity(Gravity.BOTTOM);
        customNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void saveMessage(String caption, int position) {

        itemList.get(position).setCaption(caption);
        mDetailFileAdapter.notifyDataSetChanged();

    }

    private void getQuestionnaire(int serviceId, int providerAccountId) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Questionnaire> call = apiService.getOrdersQuestions(serviceId, providerAccountId);
        call.enqueue(new retrofit2.Callback<Questionnaire>() {

            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Questionnaire questionnaire = response.body();
                        BookingModel model = new BookingModel();
                        model.setAccountId(providerAccountId);
                        model.setCatalogInfo(catalogInfo);
                        model.setQuestionnaire(questionnaire);
                        model.setFrom(Constants.ORDERS);

                        if (questionnaire != null && questionnaire.getQuestionsList() != null) {
                            Intent intent = new Intent(ItemsActivity.this, CustomQuestionnaire.class);
                            intent.putExtra("data", model);
                            intent.putExtra("from", Constants.ORDERS);

                            intent.putExtra("IMAGESLIST", itemList);
                            intent.putExtra("uniqueId", uniqueId);
                            intent.putExtra("accountId", accountId);
                            intent.putExtra("catalogId", catalogInfo.getCatLogId());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ItemsActivity.this, CheckoutListActivity.class);
                            intent.putExtra("IMAGESLIST", itemList);
                            intent.putExtra("uniqueId", uniqueId);
                            intent.putExtra("accountId", accountId);
                            intent.putExtra("serviceId", catalogInfo.getCatLogId());
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }
}