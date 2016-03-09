
package tv.theautismchannel.amazon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CollectionDemoActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    public static final String STR = "http://tac.e10.tv/PanaXML/TAC_Pana_Main.xml";
    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;
    public static BitmapDrawable drawableBg = null;
    public static BitmapDrawable leftArrowFoc = null;
    public static BitmapDrawable rightArrowFoc = null;
    public static BitmapDrawable leftArrow = null;
    public static BitmapDrawable rightArrow = null;
    public static String jsonString = null;
    public static String[] catTitles = null;
    static JSONObject categories;
    static JSONArray category;
    static JSONArray series;
    static JSONObject serie;
    static String bkg;
    static String backgrounds;
    static String posters;

    static BitmapDrawable[] arrowsDrawableArray;
    static String []imagesUri;
    static int count;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_collection_demo);
        Intent intent = new Intent();
        intent.setClass(this, PreLoadingActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        final Intent finishIntent = new Intent("finish_activity");
//        PagerTitleStrip pagerTitleStrip = (PagerTitleStrip)findViewById(R.id.pager_title_strip);
//        pagerTitleStrip.setVisibility(View.INVISIBLE);
        //Get information about background in JSON
        RetrieveFeedTask backgroundWork = (RetrieveFeedTask) new RetrieveFeedTask(new RetrieveFeedTask.AsyncResponse() {

            @Override
            public void processFinish(String output) throws JSONException {
                //Here you will receive the result fired from async class
                //of onPostExecute(result) method.
                //output is a full json string
                CollectionDemoActivity.jsonString = output;
                CollectionDemoActivity.categories = (JSONObject) new JSONTokener(output).nextValue();
                JSONObject categoriesObj = CollectionDemoActivity.categories.getJSONObject("categories");
                //Background link of the start page
                backgrounds = categoriesObj.getString("backgrounds");
                posters = categoriesObj.getString("thumbs");
                CollectionDemoActivity.bkg = categoriesObj.getString("bkg");
                String backgroundlink = backgrounds + bkg;
                // Link to the navigation arrows
                imagesUri  =new String [5];
                imagesUri[0] = backgrounds + categoriesObj.getString("la_low");
                imagesUri[1] = backgrounds + categoriesObj.getString("la_high");
                imagesUri[2] = backgrounds + categoriesObj.getString("ra_low");
                imagesUri[3] = backgrounds + categoriesObj.getString("ra_high");
                imagesUri[4] = backgrounds + categoriesObj.getString("bkg");
                arrowsDrawableArray = new BitmapDrawable[5];
                arrowsDrawableArray[0]=CollectionDemoActivity.leftArrow;
                arrowsDrawableArray[1]= CollectionDemoActivity.leftArrowFoc;
                arrowsDrawableArray[2]=CollectionDemoActivity.rightArrow;
                arrowsDrawableArray[3]=CollectionDemoActivity.rightArrowFoc;
                arrowsDrawableArray[4]=CollectionDemoActivity.drawableBg;
                count=0;
                    downloadFewImages(imagesUri[0], finishIntent);


                CollectionDemoActivity.category = categoriesObj.getJSONArray("category");
                //Get the first category JSONObject
//                JSONObject categoryOne = CollectionDemoActivity.category.getJSONObject(0);
                // Save category titles to shared preferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                //Title of the categories
                CollectionDemoActivity.catTitles = new String[category.length()];
                for (int x = 0; x < category.length(); x++) {
                    CollectionDemoActivity.catTitles[x] = category.getJSONObject(x).getString("title");
                }
            }
        }).execute(STR);
    }

    //Download few images
    void downloadFewImages(String imageUri, final Intent intentLocal){
        final ImageLoader fewImageLoader = ImageLoader.getInstance();
        fewImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        fewImageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if(count<arrowsDrawableArray.length) {
                            arrowsDrawableArray[count] = new BitmapDrawable(getResources(), loadedImage);
                          fewImageLoader.destroy();
                            System.out.println(count + " " + imagesUri[count] + " " + arrowsDrawableArray[count]);
                            count++;
                            if (count<arrowsDrawableArray.length)
                            downloadFewImages(imagesUri[count], intentLocal);
                        }
                        createUI(intentLocal);
                    }
                }
        );
    }

    public void selectPage(int page) {
        mViewPager.setCurrentItem(page);
    }

    public void createUI(Intent intent) {
        // If all images downloaded - create UI
        if( arrowsDrawableArray[0]==null){
        }else if ( arrowsDrawableArray[1]==null){
        }else if( arrowsDrawableArray[2]==null){
        }else if( arrowsDrawableArray[3]==null){
        }else if(arrowsDrawableArray[4]==null){
        }else {
            // Create an adapter that when requested, will return a fragment representing an object in
            // the collection.
            //
            // ViewPager and its adapters use support library fragments, so we must use
            // getSupportFragmentManager.
            mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());

            // Set up action bar.
//        final ActionBar actionBar = getActionBar();

            // Specify that the Home button should show an "Up" caret, indicating that touching the
            // button will take the user one step up in the application's hierarchy.
//        actionBar.setDisplayHomeAsUpEnabled(false);

            // Set up the ViewPager, attaching the adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mDemoCollectionPagerAdapter);
            sendBroadcast(intent);
        }
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1); // Our object is just an integer :-P
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 9-object collection.
            int catInt ;
            try {
                catInt = (int)(CollectionDemoActivity.catTitles.length/3);
            } catch (Exception e) {
                catInt = 3;
            }
            return catInt;
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            String title = "Loading content...";
            try {
                if (CollectionDemoActivity.catTitles[pos] == null) {
                    return title;
                } else {
                    title = CollectionDemoActivity.catTitles[pos];
                    return title;
                }
            } catch (Exception e) {
                return title;
            }
        }

    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DemoObjectFragment extends Fragment implements AdapterView.OnItemClickListener {

        public static final String ARG_OBJECT = "Category";
        Bundle args = null;
        View rootView;
        static String[] seriesTitle = null;
        ListView seriesListOne;
        ListView seriesListTwo;
        ListView seriesListThree;
        private boolean justDemo = false;
        private static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
            args = getArguments();
            seriesListOne = (ListView) rootView.findViewById(R.id.series_title_one);
            cresteSeriesView(seriesListOne, getCategoryNum(0));
            seriesListTwo = (ListView) rootView.findViewById(R.id.series_title_two);
            cresteSeriesViewOne(seriesListTwo, getCategoryNum(1));
            seriesListThree = (ListView) rootView.findViewById(R.id.series_title_three);
            cresteSeriesViewTwo(seriesListThree, getCategoryNum(2));

             //Set page title
            TextView titleOne = (TextView) rootView.findViewById(R.id.textTitleOne);
            TextView titleTwo = (TextView) rootView.findViewById(R.id.textTitleTwo);
            TextView titleThree = (TextView) rootView.findViewById(R.id.textTitleThree);
            // page 1 of 3
            TextView pageNum = (TextView) rootView.findViewById(R.id.text_page_num);
            pageNum.setText("PAGE "+Integer.toString((args.getInt(ARG_OBJECT))) +" of "+Integer.toString((int)(CollectionDemoActivity.catTitles.length/3)));
            //Program guide
            TextView programGuide = (TextView) rootView.findViewById(R.id.text_program_guide);
            programGuide.setText(R.string.program_guide);

            titleOne.setText(getPageTitle(getCategoryNum(0)));
            titleTwo.setText(getPageTitle(getCategoryNum(1)));
            titleThree.setText(getPageTitle(getCategoryNum(2)));
            ImageView buttonRight = (ImageView) rootView.findViewById(R.id.right);
            buttonRight.setBackground(arrowsDrawableArray[2]);
            buttonRight.setOnFocusChangeListener(focusListenerRight);
            buttonRight.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    toRight();
                }
            });
            ImageView buttonLeft = (ImageView) rootView.findViewById(R.id.left);
            buttonLeft.setBackground(arrowsDrawableArray[0]);
            buttonLeft.setOnFocusChangeListener(focusListenerLeft);
            buttonLeft.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    toLeft();
                }
            });

            rootView.setBackground(arrowsDrawableArray[4]);
            Log.d("onCreateview", "Set bg in Fragment");

            // Hide arrows at the start and the end of categories list
            if ((args.getInt(ARG_OBJECT) - 1)==0){
                buttonLeft.setVisibility(View.INVISIBLE);
            } else if ((args.getInt(ARG_OBJECT))==(int)(CollectionDemoActivity.catTitles.length/3)){
                buttonRight.setVisibility(View.INVISIBLE);
            }
//            System.out.println((args.getInt(ARG_OBJECT)));

            return rootView;
        }
        // Return category number on start page
        public int getCategoryNum(int pos) {
            int[] catNum = new int[3];
            int page = (args.getInt(ARG_OBJECT))-1;

                catNum[0] = (page*3);
                catNum[1] = (page*3)+1;
                catNum[2] = page*3+2;

            return catNum[pos];
        }
        public CharSequence getPageTitle(int pos) {
            String title = "Loading content...";
            try {
                if (CollectionDemoActivity.catTitles[pos] == null) {
                    return title;
                } else {
                    title = CollectionDemoActivity.catTitles[pos];
                    return title;
                }
            } catch (Exception e) {
                return title;
            }
        }

        public void toRight() {
            ((CollectionDemoActivity) getActivity()).selectPage(args.getInt(ARG_OBJECT));
        }

        public void toLeft() {
            ((CollectionDemoActivity) getActivity()).selectPage(args.getInt(ARG_OBJECT) - 2);
        }
        private View.OnFocusChangeListener focusListenerLeft = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackground(arrowsDrawableArray[1]);
                } else {
                    v.setBackground(arrowsDrawableArray[0]);
                }
            }
        };
        private View.OnFocusChangeListener focusListenerRight = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackground(arrowsDrawableArray[3]);
                } else {
                    v.setBackground(arrowsDrawableArray[2]);
                }
            }
        };

        // Create listView for first collum
        public void cresteSeriesView(ListView seriesList, int catNum) {

            if (seriesTitle(catNum) != null) {
                seriesList.setAdapter(new ArrayAdapter<String>(getActivity(),
                        R.layout.list_black_text, R.id.list_content, seriesTitle(catNum)));
                // Set onClick listener to ListView
                seriesList.setOnItemClickListener(itemClickListenerOne);
            }
        }
        // Create listView for second collum
        public void cresteSeriesViewOne(ListView seriesList, int catNum) {

            if (seriesTitle(catNum) != null) {
                seriesList.setAdapter(new ArrayAdapter<String>(getActivity(),
                        R.layout.list_black_text, R.id.list_content, seriesTitle(catNum)));
                // Set onClick listener to ListView
                seriesList.setOnItemClickListener(itemClickListenerTwo);
            }
        }
        // Create listView for second collum
        public void cresteSeriesViewTwo(ListView seriesList, int catNum) {

            if (seriesTitle(catNum) != null) {
                seriesList.setAdapter(new ArrayAdapter<String>(getActivity(),
                        R.layout.list_black_text, R.id.list_content, seriesTitle(catNum)));
                // Set onClick listener to ListView
                seriesList.setOnItemClickListener(itemClickListenerThree);
            }
        }
        // Get all series title by category number
        private String[] seriesTitle(int catNum ){
            String[] seriesTitle = null;
            JSONArray series;
            JSONObject serie;
            // get series Title from Json
            try {
                seriesTitle = null;
                series = CollectionDemoActivity.category.getJSONObject(catNum).getJSONArray("series");
                seriesTitle = new String[series.length()];
                String[] epiTitles = null;
                for (int x = 0; x < series.length(); x++) {
                    seriesTitle[x] = series.getJSONObject(x).getString("title");
//                    epiTextColor[x] = category.getJSONObject(x).getString("epi_text_color");
                }
            } catch (Exception e) {
                // We have only one serie
                series = null;
                try {
                    serie = CollectionDemoActivity.category.getJSONObject(catNum).getJSONObject("series");
                    String[] someTitleArrey = {serie.getString("title")};
//                    String[] someepiTextColorArrey = {serie.getString("epi_text_color")};
                    seriesTitle = someTitleArrey;
                } catch (Exception e1) {
                }
            }
            return seriesTitle;
        }
        private AdapterView.OnItemClickListener itemClickListenerOne = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long id) {
                 System.out.println("itemClickListenerOne");
                catNumGlob = getCategoryNum(0);
                createEpiArray(position);
            }
        };
        private AdapterView.OnItemClickListener itemClickListenerTwo = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long id) {
                System.out.println("itemClickListenerTwo");
                catNumGlob = getCategoryNum(1);
                createEpiArray(position);
            }
        };
        private AdapterView.OnItemClickListener itemClickListenerThree = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long id) {
                catNumGlob = getCategoryNum(2);
                createEpiArray(position);
            }
        };

        int catNumGlob=0;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         //Do nothing here
        }

        public void createEpiArray(int position) {
            // Then you start a new Activity via Intent
            Intent intent = new Intent();
            intent.setClass(getActivity(), EpiActivity.class);
            // put to Intent background folder
            intent.putExtra("backgrounds", CollectionDemoActivity.backgrounds);
            intent.putExtra("thumbs", CollectionDemoActivity.posters);
            JSONArray seriesForIntent;
            JSONObject serieForIntent =null;

            try {
                seriesForIntent = CollectionDemoActivity.category.getJSONObject(catNumGlob).getJSONArray("series");
            } catch (Exception e) {
                // We have only one serie
                seriesForIntent = null;
                try {
                    serieForIntent = CollectionDemoActivity.category.getJSONObject(catNumGlob).getJSONObject("series");
                } catch (Exception e1) {
                }
            }
            // if we have only one serie, handle it here
            if (seriesForIntent == null) {

                try {
                    JSONArray episodes = serieForIntent.getJSONArray("episode");
                    intent.putExtra("JSON", episodes.toString());
                } catch (Exception e) {
                    try {
                        JSONObject episod = serieForIntent.getJSONObject("episode");

                        intent.putExtra("JSON", episod.toString());
                    } catch (Exception e1) {
                    }
                }
            } else {
                // If we have array of series handle them here
                try {
                    JSONArray episodes = seriesForIntent.getJSONObject(position).getJSONArray("episode");
                    intent.putExtra("JSON", episodes.toString());
                } catch (Exception e) {
                    try {
                        JSONObject episod = seriesForIntent.getJSONObject(position).getJSONObject("episode");
                        intent.putExtra("JSON", episod.toString());
                    } catch (Exception e1) {
                    }
                }
            }
            //Put to the Intent episode background epi_epiback
            String epi_epiback = null;
            String epiTextColor = null;
            String series_info = null;
            String epi_bkg_color = null;
            String epi_bioback = null;
            String title =null;

            try {
                epi_epiback = serieForIntent.getString("epi_epiback");
                epi_bioback = serieForIntent.getString("epi_bioback");
                epiTextColor = serieForIntent.getString("epi_text_color");
                series_info = serieForIntent.getString("series_info");
                epi_bkg_color = serieForIntent.getString("epi_bkg_color");
                title  = serieForIntent.getString("title");
            } catch (Exception e1) {
                try {
                    epi_epiback = seriesForIntent.getJSONObject(position).getString("epi_epiback");
                    epiTextColor = seriesForIntent.getJSONObject(position).getString("epi_text_color");
                    series_info = seriesForIntent.getJSONObject(position).getString("series_info");
                    epi_bkg_color = seriesForIntent.getJSONObject(position).getString("epi_bkg_color");
                    epi_bioback= seriesForIntent.getJSONObject(position).getString("epi_bioback");
                    title  = seriesForIntent.getJSONObject(position).getString("title");
                } catch (Exception e2) {
                }
            }

            intent.putExtra("epi_epiback", epi_epiback);
            intent.putExtra("seriesTitle", title);
            intent.putExtra("epiTextColor", "#"+epiTextColor);
            intent.putExtra("series_info", series_info);
            intent.putExtra("epi_bkg_color", "#" + epi_bkg_color);
            intent.putExtra("epi_bioback", epi_bioback);
            //Put arrows to intent
            intent.putExtra("leftArrow",createBitmapFromDrawable(arrowsDrawableArray[0]));
            intent.putExtra("leftArrowFoc", createBitmapFromDrawable(arrowsDrawableArray[1]));
            intent.putExtra("rightArrow", createBitmapFromDrawable(arrowsDrawableArray[2]));
            intent.putExtra("rightArrowFoc", createBitmapFromDrawable(arrowsDrawableArray[3]));

            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        }

        private Bitmap createBitmapFromDrawable(BitmapDrawable d){
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            return bitmap;
        }

    }

}
