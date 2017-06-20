package io.keepcoding.madridshops.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keepcoding.madridshops.R;
import io.keepcoding.madridshops.domain.interactors.GetAllShopsFromCacheInteractor;
import io.keepcoding.madridshops.domain.interactors.GetAllShopsFromCacheInteractorImpl;
import io.keepcoding.madridshops.domain.interactors.GetIfAllShopsAreCachedInteractorImpl;
import io.keepcoding.madridshops.domain.interactors.GetIfAllShopsAreCachedInteractor;
import io.keepcoding.madridshops.domain.interactors.GetAllShopsInteractor;
import io.keepcoding.madridshops.domain.interactors.GetAllShopsInteractorCompletion;
import io.keepcoding.madridshops.domain.interactors.GetAllShopsInteractorImpl;
import io.keepcoding.madridshops.domain.interactors.InteractorErrorCompletion;
import io.keepcoding.madridshops.domain.interactors.SaveAllShopsIntoCacheInteractor;
import io.keepcoding.madridshops.domain.interactors.SaveAllShopsIntoCacheInteractorImpl;
import io.keepcoding.madridshops.domain.interactors.SetAllShopsAreCachedInteractor;
import io.keepcoding.madridshops.domain.interactors.SetAllShopsAreCachedInteractorImpl;
import io.keepcoding.madridshops.domain.managers.cache.GetAllShopsFromCacheManager;
import io.keepcoding.madridshops.domain.managers.cache.GetAllShopsFromCacheManagerDAOImpl;
import io.keepcoding.madridshops.domain.managers.cache.SaveAllShopsIntoCacheManager;
import io.keepcoding.madridshops.domain.managers.cache.SaveAllShopsIntoCacheManagerDAOImpl;
import io.keepcoding.madridshops.domain.managers.network.GetAllShopsManagerImpl;
import io.keepcoding.madridshops.domain.managers.network.NetworkManager;
import io.keepcoding.madridshops.domain.model.Shop;
import io.keepcoding.madridshops.domain.model.Shops;
import io.keepcoding.madridshops.fragments.ShopsFragment;
import io.keepcoding.madridshops.navigator.Navigator;
import io.keepcoding.madridshops.views.OnElementClick;

public class ShopListActivity extends AppCompatActivity {

    @BindView(R.id.activity_shop_list__progress_bar) ProgressBar progressBar;

    ShopsFragment shopsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        ButterKnife.bind(this);

        shopsFragment = (ShopsFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shop_list__fragment_shops);

        GetIfAllShopsAreCachedInteractor getIfAllShopsAreCachedInteractor = new GetIfAllShopsAreCachedInteractorImpl(this);
        getIfAllShopsAreCachedInteractor.execute(new Runnable() {
            @Override
            public void run() {
                // all cached already, no need to download things, just read from DB
                readDataFromCache();
            }
        }, new Runnable() {
            @Override
            public void run() {
                // nothing cached yet
                obtainShopsList();
            }
        });
    }

    private void readDataFromCache() {
        GetAllShopsFromCacheManager getAllShopsFromCacheManager = new GetAllShopsFromCacheManagerDAOImpl(this);
        GetAllShopsFromCacheInteractor getAllShopsFromCacheInteractor = new GetAllShopsFromCacheInteractorImpl(getAllShopsFromCacheManager);
        getAllShopsFromCacheInteractor.execute(new GetAllShopsInteractorCompletion() {
            @Override
            public void completion(@NonNull Shops shops) {
                configShopsFragment(shops);
            }
        });
    }


    private void obtainShopsList() {
        progressBar.setVisibility(View.VISIBLE);

        NetworkManager manager = new GetAllShopsManagerImpl(this);
        GetAllShopsInteractor getAllShopsInteractor = new GetAllShopsInteractorImpl(manager);
        getAllShopsInteractor.execute(
            new GetAllShopsInteractorCompletion() {
                @Override
                public void completion(Shops shops) {
                    SaveAllShopsIntoCacheManager saveManager = new SaveAllShopsIntoCacheManagerDAOImpl(getBaseContext());
                    SaveAllShopsIntoCacheInteractor saveInteractor = new SaveAllShopsIntoCacheInteractorImpl(saveManager);
                    saveInteractor.execute(shops, new Runnable() {
                        @Override
                        public void run() {
                            SetAllShopsAreCachedInteractor setAllShopsCachedInteractor = new SetAllShopsAreCachedInteractorImpl(getBaseContext());
                            setAllShopsCachedInteractor.execute(true);
                        }
                    });

                    configShopsFragment(shops);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            },
            new InteractorErrorCompletion() {
                @Override
                public void onError(String errorDescription) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        );
    }

    private void configShopsFragment(final Shops shops) {
        shopsFragment.setShops(shops);
        shopsFragment.setOnElementClickListener(new OnElementClick<Shop>() {
            @Override
            public void clickedOn(@NonNull Shop element, int position) {
                // TODO: finish
                Navigator.navigateFromShopListActivityToShopDetailActivity(ShopListActivity.this, element, position);
            }
        });
    }
}
