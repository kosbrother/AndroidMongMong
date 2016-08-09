package com.kosbrother.mongmongwoo.shoppingcart;

import android.content.Context;
import android.content.SharedPreferences;

import com.kosbrother.mongmongwoo.entity.postorder.UnableToBuyModel;
import com.kosbrother.mongmongwoo.model.Product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartManagerTest {

    private String itemsJsonString =
            "[\n" +
                    "  {\n" +
                    "    \"specs\": [\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/194/napkins_spec_0.png\"\n" +
                    "        },\n" +
                    "        \"style\": \"粉色小熊\",\n" +
                    "        \"stock_amount\": 20,\n" +
                    "        \"id\": 194\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/195/napkins_spec_1.png\"\n" +
                    "        },\n" +
                    "        \"style\": \"綠色樹\",\n" +
                    "        \"stock_amount\": 14,\n" +
                    "        \"id\": 195\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/196/napkins_spec_2.png\"\n" +
                    "        },\n" +
                    "        \"style\": \"黑底白樹\",\n" +
                    "        \"stock_amount\": 8,\n" +
                    "        \"id\": 196\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/197/napkins_spec_3.png\"\n" +
                    "        },\n" +
                    "        \"style\": \"白底黑樹\",\n" +
                    "        \"stock_amount\": 7,\n" +
                    "        \"id\": 197\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"cover\": {\n" +
                    "      \"icon\": {\n" +
                    "        \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item/cover/66/icon_napkins_3.png\"\n" +
                    "      },\n" +
                    "      \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item/cover/66/napkins_3.png\"\n" +
                    "    },\n" +
                    "    \"slug\": \"可愛清新衛生棉包-女生便利包\",\n" +
                    "    \"selectedSpec\": {\n" +
                    "      \"style_pic\": {\n" +
                    "        \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/196/napkins_spec_2.png\"\n" +
                    "      },\n" +
                    "      \"style\": \"黑底白樹\",\n" +
                    "      \"stock_amount\": 8,\n" +
                    "      \"id\": 196\n" +
                    "    },\n" +
                    "    \"name\": \"可愛清新衛生棉包 女生便利包\",\n" +
                    "    \"price\": 49,\n" +
                    "    \"id\": 66,\n" +
                    "    \"final_price\": 49,\n" +
                    "    \"special_price\": 0,\n" +
                    "    \"categoryId\": 0,\n" +
                    "    \"buy_count\": 4\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"specs\": [\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/664/s1.jpg\"\n" +
                    "        },\n" +
                    "        \"style\": \"粉色\",\n" +
                    "        \"stock_amount\": 4,\n" +
                    "        \"id\": 664\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/665/s2.jpg\"\n" +
                    "        },\n" +
                    "        \"style\": \"綠色\",\n" +
                    "        \"stock_amount\": 8,\n" +
                    "        \"id\": 665\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/666/s3.jpg\"\n" +
                    "        },\n" +
                    "        \"style\": \"藍色\",\n" +
                    "        \"stock_amount\": 9,\n" +
                    "        \"id\": 666\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/667/s4.jpg\"\n" +
                    "        },\n" +
                    "        \"style\": \"紫色\",\n" +
                    "        \"stock_amount\": 11,\n" +
                    "        \"id\": 667\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"cover\": {\n" +
                    "      \"icon\": {\n" +
                    "        \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item/cover/152/icon_a1.jpg\"\n" +
                    "      },\n" +
                    "      \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item/cover/152/a1.jpg\"\n" +
                    "    },\n" +
                    "    \"slug\": \"0度夏日歡樂雙層分享冰杯\",\n" +
                    "    \"selectedSpec\": {\n" +
                    "      \"style_pic\": {\n" +
                    "        \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/664/s1.jpg\"\n" +
                    "      },\n" +
                    "      \"style\": \"粉色\",\n" +
                    "      \"stock_amount\": 4,\n" +
                    "      \"id\": 664\n" +
                    "    },\n" +
                    "    \"name\": \"0度夏日歡樂雙層分享冰杯\",\n" +
                    "    \"price\": 199,\n" +
                    "    \"id\": 152,\n" +
                    "    \"final_price\": 199,\n" +
                    "    \"special_price\": 0,\n" +
                    "    \"categoryId\": 0,\n" +
                    "    \"buy_count\": 2\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"specs\": [\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/3068/s1.jpg\"\n" +
                    "        },\n" +
                    "        \"style\": \"檸檬星球\",\n" +
                    "        \"stock_amount\": 0,\n" +
                    "        \"id\": 3068\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/3069/s2.jpg\"\n" +
                    "        },\n" +
                    "        \"style\": \"流星沙漏\",\n" +
                    "        \"stock_amount\": 4,\n" +
                    "        \"id\": 3069\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/3070/s3.jpg\"\n" +
                    "        },\n" +
                    "        \"style\": \"薄荷菊花\",\n" +
                    "        \"stock_amount\": 0,\n" +
                    "        \"id\": 3070\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"style_pic\": {\n" +
                    "          \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/3071/s4.jpg\"\n" +
                    "        },\n" +
                    "        \"style\": \"玻璃馬卡龍\",\n" +
                    "        \"stock_amount\": 3,\n" +
                    "        \"id\": 3071\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"cover\": {\n" +
                    "      \"icon\": {\n" +
                    "        \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item/cover/681/icon_a1.jpg\"\n" +
                    "      },\n" +
                    "      \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item/cover/681/a1.jpg\"\n" +
                    "    },\n" +
                    "    \"slug\": \"森林星空版束口袋\",\n" +
                    "    \"selectedSpec\": {\n" +
                    "      \"style_pic\": {\n" +
                    "        \"url\": \"https://mongmongwoo-staging.storage.googleapis.com/uploads/item_spec/style_pic/3071/s4.jpg\"\n" +
                    "      },\n" +
                    "      \"style\": \"玻璃馬卡龍\",\n" +
                    "      \"stock_amount\": 3,\n" +
                    "      \"id\": 3071\n" +
                    "    },\n" +
                    "    \"name\": \"森林星空版束口袋\",\n" +
                    "    \"price\": 350,\n" +
                    "    \"id\": 681,\n" +
                    "    \"final_price\": 350,\n" +
                    "    \"special_price\": 0,\n" +
                    "    \"categoryId\": 0,\n" +
                    "    \"buy_count\": 1\n" +
                    "  }\n" +
                    "]";

    private ShoppingCartManager shoppingCartManager;

    @Mock
    SharedPreferences mMockSharedPreferences;

    @Mock
    private Context context;

    @Mock
    private SharedPreferences.Editor mMockEditor;


    @Before
    public void setUp() throws Exception {
        shoppingCartManager = createMockShoppingCartManager();
    }

    @Test
    public void testGetShoppingCarItemSize() throws Exception {
        assertEquals(3, shoppingCartManager.getShoppingCarItemSize());
    }

    @Test
    public void testRemoveUnableToBuyFromShoppingCart_onStockAmount() throws Exception {
        List<UnableToBuyModel> unableToBuyModels = new ArrayList<>();
        UnableToBuyModel unableToBuyModel = mock(UnableToBuyModel.class);
        when(unableToBuyModel.getSpecStockAmount()).thenReturn(0);
        when(unableToBuyModel.isOffShelf()).thenReturn(false);
        when(unableToBuyModel.getSpecId()).thenReturn(196);
        unableToBuyModels.add(unableToBuyModel);

        List<Product> wishProducts = shoppingCartManager.removeUnableToBuyFromShoppingCart(unableToBuyModels);
        int wishProductSize = wishProducts.size();

        assertEquals(1, wishProductSize);
    }

    @Test
    public void testRemoveUnableToBuyFromShoppingCart_offShelf() throws Exception {
        List<UnableToBuyModel> unableToBuyModels = new ArrayList<>();
        UnableToBuyModel unableToBuyModel = mock(UnableToBuyModel.class);
        when(unableToBuyModel.getSpecStockAmount()).thenReturn(0);
        when(unableToBuyModel.isOffShelf()).thenReturn(true);
        when(unableToBuyModel.getSpecId()).thenReturn(196);
        unableToBuyModels.add(unableToBuyModel);

        List<Product> wishProducts = shoppingCartManager.removeUnableToBuyFromShoppingCart(unableToBuyModels);
        int wishProductSize = wishProducts.size();

        assertEquals(1, wishProductSize);
    }

    @Test
    public void testRemoveUnableToBuyFromShoppingCart_stockNotEnough() throws Exception {
        List<UnableToBuyModel> unableToBuyModels = new ArrayList<>();
        UnableToBuyModel unableToBuyModel = mock(UnableToBuyModel.class);
        when(unableToBuyModel.getSpecStockAmount()).thenReturn(1);
        when(unableToBuyModel.isOffShelf()).thenReturn(false);
        when(unableToBuyModel.getSpecId()).thenReturn(196);
        unableToBuyModels.add(unableToBuyModel);

        List<Product> wishProducts = shoppingCartManager.removeUnableToBuyFromShoppingCart(unableToBuyModels);
        int wishProductSize = wishProducts.size();

        assertEquals(1, wishProductSize);
    }

    @Test
    public void testRemoveUnableToBuyFromShoppingCart_twoUnableToBuy() throws Exception {
        List<UnableToBuyModel> unableToBuyModels = new ArrayList<>();
        UnableToBuyModel unableToBuyModel1 = mock(UnableToBuyModel.class);
        when(unableToBuyModel1.getSpecStockAmount()).thenReturn(0);
        when(unableToBuyModel1.isOffShelf()).thenReturn(false);
        when(unableToBuyModel1.getSpecId()).thenReturn(196);
        unableToBuyModels.add(unableToBuyModel1);

        UnableToBuyModel unableToBuyModel2 = mock(UnableToBuyModel.class);
        when(unableToBuyModel2.getSpecStockAmount()).thenReturn(0);
        when(unableToBuyModel2.isOffShelf()).thenReturn(true);
        when(unableToBuyModel2.getSpecId()).thenReturn(664);
        unableToBuyModels.add(unableToBuyModel2);

        List<Product> wishProducts = shoppingCartManager.removeUnableToBuyFromShoppingCart(unableToBuyModels);
        int wishProductSize = wishProducts.size();

        assertEquals(2, wishProductSize);
    }

    private ShoppingCartManager createMockShoppingCartManager() {
        when(context.getSharedPreferences(ShoppingCartManager.PREFS_NAME, Context.MODE_PRIVATE)).
                thenReturn(mMockSharedPreferences);
        ShoppingCartManager.init(context);

        when(mMockSharedPreferences.getString(eq(ShoppingCartManager.SHOPPING_CAR), anyString()))
                .thenReturn(itemsJsonString);

        when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);
        return ShoppingCartManager.getInstance();
    }
}