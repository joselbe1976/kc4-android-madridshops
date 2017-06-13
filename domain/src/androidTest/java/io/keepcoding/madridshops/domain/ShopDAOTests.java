package io.keepcoding.madridshops.domain;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.keepcoding.madridshops.domain.managers.db.ShopDAO;
import io.keepcoding.madridshops.domain.model.Shop;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ShopDAOTests {
    @Test
    public void given_shop_DAO_inserts_shop() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        ShopDAO sut = new ShopDAO(appContext);
        Shop shop = Shop.of(1, "Shop test").
                        setAddress("address").
                        setLatitude(10).
                        setLongitude(11);

        long id = sut.insert(shop);
        assertTrue(id > 0);

    }

    @Test
    public void given_inserted_shops_DAO_queries_all_shops() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        ShopDAO sut = new ShopDAO(appContext);
        Shop shop = Shop.of(1, "Shop test").
                setAddress("address").
                setLatitude(10).
                setLongitude(11);

        long id = sut.insert(shop);

        List<Shop> shops = sut.query();

        assertNotNull(shops);
        assertTrue(shops.size() >= 1);

    }
}
