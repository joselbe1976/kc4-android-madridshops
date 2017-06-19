package io.keepcoding.madridshops.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import io.keepcoding.madridshops.R;
import io.keepcoding.madridshops.domain.interactors.GetAllShopsInteractor;
import io.keepcoding.madridshops.domain.interactors.GetAllShopsInteractorCompletion;
import io.keepcoding.madridshops.domain.interactors.GetAllShopsInteractorImpl;
import io.keepcoding.madridshops.domain.interactors.InteractorErrorCompletion;
import io.keepcoding.madridshops.domain.managers.network.GetAllShopsManagerImpl;
import io.keepcoding.madridshops.domain.managers.network.NetworkManager;
import io.keepcoding.madridshops.domain.model.Shop;
import io.keepcoding.madridshops.domain.model.Shops;
import io.keepcoding.madridshops.fragments.ShopsFragment;
import io.keepcoding.madridshops.navigator.Navigator;
import io.keepcoding.madridshops.views.OnElementClick;

public class ShopListActivity extends AppCompatActivity {

    ShopsFragment shopsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        shopsFragment = (ShopsFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shop_list__fragment_shops);

        // obtain shops list

        NetworkManager manager = new GetAllShopsManagerImpl(this);
        GetAllShopsInteractor getAllShopsInteractor = new GetAllShopsInteractorImpl(manager);
        getAllShopsInteractor.execute(
            new GetAllShopsInteractorCompletion() {
                @Override
                public void completion(Shops shops) {
                    System.out.println("Hello hello");
                    shopsFragment.setShops(shops);
                    shopsFragment.setOnElementClickListener(new OnElementClick<Shop>() {
                        @Override
                        public void clickedOn(@NonNull Shop element, int position) {
                            // TODO: finish
                            Navigator.navigateFromShopListActivityToShopDetailActivity(ShopListActivity.this, element, position);
                        }
                    });
                }
            },
            new InteractorErrorCompletion() {
                @Override
                public void onError(String errorDescription) {

                }
            }
        );


    }
}
