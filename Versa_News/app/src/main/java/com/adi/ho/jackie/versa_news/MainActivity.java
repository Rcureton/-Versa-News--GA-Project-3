package com.adi.ho.jackie.versa_news;


import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import com.adi.ho.jackie.versa_news.Fragments.FashionFragment;


import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CollapsingToolbarLayout;

import com.adi.ho.jackie.versa_news.Fragments.PopularFragment;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.adi.ho.jackie.versa_news.Fragments.FoodFragment;
import com.adi.ho.jackie.versa_news.Fragments.HomeFragment;
import com.adi.ho.jackie.versa_news.Fragments.NewsFragment;
import com.adi.ho.jackie.versa_news.Fragments.SportsFragment;
import com.adi.ho.jackie.versa_news.Fragments.TechFragment;
import com.adi.ho.jackie.versa_news.Fragments.TravelFragment;
import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.antonyt.infiniteviewpager.InfiniteViewPager;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.adi.ho.jackie.versa_news.GSONClasses.ViceDataClass;
import com.adi.ho.jackie.versa_news.GSONClasses.ViceItemsClass;
import com.adi.ho.jackie.versa_news.GSONClasses.ViceSearchResultsClass;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import java.util.Random;


public class MainActivity extends AppCompatActivity implements PopularFragment.ChangeToolbarColor {
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    Button mChangeActivityButton;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    public static final String ARTICLEID = "ID";

    private int horizontalChilds;
    private int verticalChilds;
    private TabLayout tabLayout;

    public Toolbar toolbar;
    private InfiniteViewPager viewPager;
    private List<Fragment> fragmentList;
    private List<ViceItemsClass> listViceArticles;
    private Bundle popularArticles;
    private ArrayList<String> urlArray;
    private ArrayList<String> headlineArray;
    private ArrayList<String> idArray;
    private AppBarLayout appBarLayout;
    private boolean loadingFinished = false;
    private ArrayList<String> colorArray;
    private ArrayList<String> statusColorArray;
    private HomeFragment homeFragment;
    private CollapsingToolbarLayout toolbarLayout;
    private PopularFragment popularFragment;
    Bundle popularBundle;
    private String popularImageUrl;
    private Window window;
    private View statusbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_layout);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        viewPager = (InfiniteViewPager) findViewById(R.id.viewpager);
        statusbar = (View)findViewById(R.id.statusBarBackground);

        listViceArticles = new ArrayList<>();
        urlArray = new ArrayList<>();
        headlineArray = new ArrayList<>();
        idArray = new ArrayList<>();
        popularArticles = new Bundle();
        colorArray = new ArrayList<>();
        statusColorArray = new ArrayList<>();
        homeFragment = new HomeFragment();


        window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        fillColorArrays();

        // Vice API URLs that data can be received through
        String getMostPopularURL = getResources().getString(R.string.get_most_popular);
        String getViceTodayURL = getResources().getString(R.string.get_vice_today);


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Call async task that gets the API data and show that data in the view.
        DownloadPopularArticlesAsyncTask downloadPopularArticlesAsyncTask = new DownloadPopularArticlesAsyncTask();
        downloadPopularArticlesAsyncTask.execute(getMostPopularURL);
    }
    /* Change toolbarcolor
     *
     */
    @Override
    public void changeColor(Palette.Swatch light, Palette.Swatch dark) {
        if (light != null) {
            toolbar.setBackgroundColor(light.getRgb());
        }
        if (dark !=null ){
            window.setStatusBarColor(dark.getRgb());
        }
    }


    private class GetDataAsyncTask extends AsyncTask<String, Void, List<ViceItemsClass>> {
        @Override
        protected List<ViceItemsClass> doInBackground(String... myURL) {
            String data = "";
            try {
                URL url = new URL(myURL[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                data = getInputData(inputStream);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // Convert the JSON data to Gson data
            Gson gson = new Gson();
            ViceSearchResultsClass results = gson.fromJson(data, ViceSearchResultsClass.class);

            Log.d("ASYNCTASK", "article id: " + results.getData().getItems().get(0).getId());
            Log.d("ASYNCTASK", results.getData().getItems().get(0).getTitle());

            /*
            To get the article ID, call: results.getData().getItems().get(0).getId();
            To get the category, call: results.getData().getItems().get(0).getCategory();
             */
            ViceDataClass item = results.getData();

            return item.getItems();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(List<ViceItemsClass> result) {
            listViceArticles = result;
            loadingFinished = true;
            for (int i = 0; i < 8; i++) {
                urlArray.add(listViceArticles.get(i).getImage());
                headlineArray.add(listViceArticles.get(i).getTitle());
                idArray.add(listViceArticles.get(i).getId());
            }
            popularArticles.putStringArrayList("POPULARURL", urlArray);
            popularArticles.putStringArrayList("POPULARHEADLINE", headlineArray);
            popularArticles.putStringArrayList("POPULARID", idArray);
            launchFragments();

            setImagesHomeFragment(urlArray, headlineArray, idArray);
            //Set images from home fragment

        }

        public String getInputData(InputStream stream) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String read;

            while ((read = br.readLine()) != null) {
                sb.append(read);
            }

            br.close();
            return sb.toString();
        }


    }

    private class DownloadPopularArticlesAsyncTask extends AsyncTask<String, Void, List<ViceItemsClass>> {


        @Override
        protected List<ViceItemsClass> doInBackground(String... myURL) {
            String data = "";
            try {
                URL url = new URL(myURL[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                data = getInputData(inputStream);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // Convert the JSON data to Gson data
            Gson gson = new Gson();
            ViceSearchResultsClass results = gson.fromJson(data, ViceSearchResultsClass.class);

            /*
            To get the article ID, call: results.getData().getItems().get(0).getId();
            To get the category, call: results.getData().getItems().get(0).getCategory();
             */
            ViceDataClass item = results.getData();

            return item.getItems();
        }

        public String getInputData(InputStream stream) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String read;

            while ((read = br.readLine()) != null) {
                sb.append(read);
            }

            br.close();
            return sb.toString();
        }

        @Override
        protected void onPostExecute(List<ViceItemsClass> viceItemsClasses) {
            //  popularBundle = new Bundle();
            popularFragment = new PopularFragment();


            popularImageUrl = viceItemsClasses.get(0).getImage();
            String getLatestURL = getResources().getString(R.string.get_latest);
            GetDataAsyncTask getDataAsyncTask = new GetDataAsyncTask();
            getDataAsyncTask.execute(getLatestURL);

        }
    }

    private void fillFragmentList() {
        fragmentList = new ArrayList<>();

        fragmentList.add(homeFragment);
        fragmentList.add(new NewsFragment());

        fragmentList.add(new FashionFragment());
        fragmentList.add(new TechFragment());
        fragmentList.add(new SportsFragment());
        fragmentList.add(new FoodFragment());
        fragmentList.add(new TravelFragment());
        fragmentList.add(popularFragment);


    }

    private void launchFragments() {
        fillFragmentList();

        viewPager.setAdapter(new InfinitePagerAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment frag = null;

                frag = fragmentList.get(position);

                frag.setArguments(popularArticles);

                return frag;
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        }));

        viewPager.setPageTransformer(true, new DepthPageTransformer());// viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (popularFragment != null && (popularFragment.popularImage.getDrawable() == null || popularFragment.popularImage.getVisibility() == View.INVISIBLE)) {
                Picasso.with(MainActivity.this).load(popularImageUrl).fit().into(popularFragment.popularImage);
            }

            //Color Animation
            Random rand = new Random();
            position = rand.nextInt(19);
            ColorDrawable toolbarColor = (ColorDrawable) toolbar.getBackground();

            Integer colorFrom = toolbarColor.getColor();
            Integer colorTo = Color.parseColor(colorArray.get(position));
            // Integer colorStatusFrom = getS
            //Integer colorStatusTo = Color.parseColor(statusColorArray.get(position));
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            //ValueAnimator colorStatusAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatusFrom, colorStatusTo);

            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    toolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });

//            colorStatusAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//                @Override
//                public void onAnimationUpdate(ValueAnimator animator) {
//                    if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
//                        getActivity().getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
//                    }
//                    if (currentapiVersion == Build.VERSION_CODES.KITKAT) {
//                        tintManager.setStatusBarTintColor((Integer) animator.getAnimatedValue());
//                    }
//                }
//            });
            colorAnimation.setDuration(1300);
            colorAnimation.setStartDelay(0);
            colorAnimation.start();
//            colorStatusAnimation.setDuration(1300);
//            colorStatusAnimation.setStartDelay(0);
//            colorStatusAnimation.start();

            // MainActivity.this.setTheme(R.style.ToolbarTheme1);


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void fillColorArrays() {


        String[] colorStrings = {"#004D40",
                "#00695C",
                "#00796B",
                "#00897B",
                "#009688",
                "#26A69A",
                "#4DB6AC",
                "#80CBC4",
                "#B2DFDB",
                "#1B5E20",
                "#2E7D32",
                "#388E3C",
                "#43A047",
                "#4CAF50",
                "#66BB6A",
                "#81C784",
                "#A5D6A7",
                "#C8E6C9",
                "#E8F5E9"};
        colorArray.addAll(Arrays.asList(colorStrings));

        statusColorArray.add("#000000");
        statusColorArray.add("#aa0000");
        statusColorArray.add("#00aa00");
        statusColorArray.add("#0000aa");
        statusColorArray.add("#ff0000");
        statusColorArray.add("#00ff00");
        statusColorArray.add("#9900ff");
        statusColorArray.add("#ff00ff");


    }

    public void setImagesHomeFragment(ArrayList<String> imageUrls, ArrayList<String> headlineArray, ArrayList<String> previewArray) {
        Picasso.with(MainActivity.this).load(imageUrls.get(0)).fit().into(homeFragment.popularImage1);
        Picasso.with(homeFragment.getContext()).load(imageUrls.get(1)).fit().into(homeFragment.popularImage2);
        Picasso.with(homeFragment.getContext()).load(imageUrls.get(2)).fit().into(homeFragment.popularImage3);
        Picasso.with(homeFragment.getContext()).load(imageUrls.get(3)).fit().into(homeFragment.popularImage4);
        Picasso.with(homeFragment.getContext()).load(imageUrls.get(4)).fit().into(homeFragment.popularImage5);
        Picasso.with(homeFragment.getContext()).load(imageUrls.get(5)).fit().into(homeFragment.popularImage6);
        Picasso.with(homeFragment.getContext()).load(imageUrls.get(6)).fit().into(homeFragment.popularImage7);
        Picasso.with(homeFragment.getContext()).load(imageUrls.get(7)).fit().into(homeFragment.popularImage8);
    }
    public void setStatusBarBgColor(View statusBar,int color){
            //status bar height
            int actionBarHeight = getActionBarHeight();
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = actionBarHeight + statusBarHeight;
            statusBar.setBackgroundColor(color);

    }
    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}


