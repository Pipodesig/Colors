package tv.theautismchannel.amazon;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class EpiActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    static BitmapDrawable drawableBg;
    static String epi_epiback;
    static String epiTextColor;
    static String series_info;
    static String seriesTitle;
    static String epi_bkg_color;
    static String epi_bioback;
    static JSONArray episodes = null;
    static JSONObject episod = null;
    static String[] epiTitles = null;
    static String[] epithumbs = null;
    static String output;
    static String backgrounds;
    static String posters;
    static String[] description;
    static String[] epi_num;
    static BitmapDrawable[] arrowsDrawableArray = null;
    static int textColor;
    LinearLayout[] epiLayouts;
    static Bitmap leftArrow;
    static Bitmap leftArrowFoc;
    static Bitmap rightArrow;
    static Bitmap rightArrowFoc;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String[] imagesUri;
    private int count;

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fadein_main, R.anim.fadeout_epi);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_epi);

        final Intent finishIntent = new Intent("finish_activity");
        //Get all data from previous activity
        Intent receiveIntent = getIntent();
        output = receiveIntent.getStringExtra("JSON");
        backgrounds = receiveIntent.getStringExtra("backgrounds");
        posters = receiveIntent.getStringExtra("thumbs");
        epi_epiback = receiveIntent.getStringExtra("epi_epiback");
        epi_bioback = receiveIntent.getStringExtra("epi_bioback");
        epiTextColor = receiveIntent.getStringExtra("epiTextColor");
        series_info = receiveIntent.getStringExtra("series_info");
        epi_bkg_color = receiveIntent.getStringExtra("epi_bkg_color");
        seriesTitle = receiveIntent.getStringExtra("seriesTitle");
        //Get arrows from intent
        leftArrow = (Bitmap) receiveIntent.getParcelableExtra("leftArrow");
        leftArrowFoc = (Bitmap) receiveIntent.getParcelableExtra("leftArrowFoc");
        rightArrow = (Bitmap) receiveIntent.getParcelableExtra("rightArrow");
        rightArrowFoc = (Bitmap) receiveIntent.getParcelableExtra("rightArrowFoc");
//        epi_num = receiveIntent.getStringExtra("epi_num");
//        description = receiveIntent.getStringExtra("description");
        //Set data to loading activity and Start it
        Intent intent = new Intent();
        intent.setClass(this, PreLoadingActivity.class);
        intent.putExtra("seriesTitle", seriesTitle);
        intent.putExtra("epi_bkg_color", epi_bkg_color);
        intent.putExtra("epiTextColor", epiTextColor);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//            //Set second page as a start page
//            ((EpiActivity)getActivity()).selectPage(2);
        //Create list of episodes title and thumbs names
        createListOfEpisodes();
        imagesUri = new String[epithumbs.length + 2];
        imagesUri[0] = backgrounds + epi_epiback;
        imagesUri[1] = backgrounds + epi_bioback;
        for (int x = 2; x < imagesUri.length; x++) {
            imagesUri[x] = posters + epithumbs[x - 2];
        }
        arrowsDrawableArray = new BitmapDrawable[imagesUri.length];
        count = 0;
        downloadFewImages(imagesUri[0], finishIntent);
        if (epiTextColor != null) {
            try {
                textColor = Color.parseColor(epiTextColor);
            } catch (RuntimeException e) {
                textColor = Color.parseColor("#0" + epiTextColor.substring(1));
            }
        }
    }


    //Download few images
    void downloadFewImages(String imageUri, final Intent intentLocal) {
        final ImageLoader fewImageLoader = ImageLoader.getInstance();
        fewImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        fewImageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (count < arrowsDrawableArray.length) {
                            arrowsDrawableArray[count] = new BitmapDrawable(getResources(), loadedImage);
                            fewImageLoader.destroy();
                            System.out.println(count + " " + imagesUri[count] + " " + arrowsDrawableArray[count]);
                            count++;
                            if (count < arrowsDrawableArray.length)
                                downloadFewImages(imagesUri[count], intentLocal);
                        }
                        createUI(intentLocal);
                    }
                }
        );
    }

    void createListOfEpisodes() {
        // Create list of episodes
        try {
            episodes = (JSONArray) new JSONTokener(output).nextValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                episod = (JSONObject) new JSONTokener(output).nextValue();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        if (episodes != null) {
            epiTitles = new String[episodes.length()];
            epithumbs = new String[episodes.length()];
            epiTitles = getEpisodesAttribute(episodes, "title");
            epithumbs = getEpisodesAttribute(episodes, "thumb");
            epi_num = getEpisodesAttribute(episodes, "epi_num");
            description = getEpisodesAttribute(episodes, "description");
        } else {
            try {
                String[] oneEpiTitle = {episod.getString("title")};
                String[] oneEpiThumb = {episod.getString("thumb")};
                String[] oneEpiNum = {episod.getString("epi_num")};
                String[] oneEpiDesc = {episod.getString("description")};
                epiTitles = oneEpiTitle;
                epithumbs = oneEpiThumb;
                epi_num = oneEpiNum;
                description = oneEpiDesc;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    private String[] getEpisodesAttribute(JSONArray episodes, String attribute) {
        String[] epiTitleArray = new String[episodes.length()];
        try {
            for (int x = 0; x < episodes.length(); x++) {
                epiTitleArray[x] = episodes.getJSONObject(x).getString(attribute);
            }
        } catch (Exception w) {
        }
        return epiTitleArray;
    }

    void createUI(Intent intent) {
        if (allImagesDownloaded()) {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            sendBroadcast(intent);
        }

    }

    public boolean allImagesDownloaded() {
        boolean next = true;
        for (int x = 0; x < arrowsDrawableArray.length; x++) {
            if (arrowsDrawableArray[x] == null) {
                next = false;
            }
        }
        return next;
    }


    public void selectPage(int page) {
        mViewPager.setCurrentItem(page);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView epiList;
        private String backgroundlink;
        private  TextView[] epiNum;
        TextView epi_num_1 = null;
        TextView epi_num_2= null;
        TextView epi_num_3= null;
        TextView epi_num_4= null;
        TextView epi_num_5= null;
        TextView epi_num_6= null;
        TextView epi_num_7= null;
        TextView epi_num_8= null;
        private int [] epiNumId;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private BitmapDrawable createDrawableFromBitmap(Bitmap bitmap) {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            return drawable;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_epi, container, false);

            //Create navigation arrows
            ImageView buttonRight = (ImageView) rootView.findViewById(R.id.right);
            buttonRight.setBackground(createDrawableFromBitmap(rightArrow));

            buttonRight.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    toRight();
                }
            });
            ImageView buttonLeft = (ImageView) rootView.findViewById(R.id.left);
            buttonLeft.setBackground(createDrawableFromBitmap(leftArrow));
            buttonLeft.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    toLeft();
                }
            });
            // Text in up left corner
            TextView upLeft = (TextView) rootView.findViewById(R.id.text_up_left);
            upLeft.setTextColor(textColor);
            // Text in up left corner
            TextView upRight = (TextView) rootView.findViewById(R.id.text_up_right);
            upRight.setTextColor(textColor);

            LinearLayout mainEpiLayout = (LinearLayout) rootView.findViewById(R.id.main_epi_layout);
//            LinearLayout colomOne = (LinearLayout) rootView.findViewById(R.id.colom_one);
//            LinearLayout colomOTwo = (LinearLayout) rootView.findViewById(R.id.colom_two);

            //Declarate all epiLayouts elements

            initLayoutsElements();
            epiNum  = new TextView[] {epi_num_1, epi_num_2, epi_num_3, epi_num_4, epi_num_5, epi_num_6, epi_num_7, epi_num_8};
            epiNumId = new int[]{R.id.epi_num_1,R.id.epi_num_2,R.id.epi_num_3,R.id.epi_num_4,R.id.epi_num_5,R.id.epi_num_6,R.id.epi_num_7,R.id.epi_num_8,};
            int pageNumber = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
//            int epiNumberOnPage = pageNumber * 8 - (8 - position);
            int end;
            if (epiTitles.length>=8){
                end = 8;
            }else {
                end = epiTitles.length;
            }

            for (int position = 0; position < end; position++){
                System.out.println("end "+ end + "position " + position);
                epiNum[position]=(TextView) rootView.findViewById(epiNumId[position]);
                epiNum[position].setText(R.string.ep);
            }




            //Episode #1
            TextView epi_num_1 = (TextView) rootView.findViewById(R.id.epi_num_1);
            TextView epi_title_1 = (TextView) rootView.findViewById(R.id.epi_title_1);
            TextView epi_description_1 = (TextView) rootView.findViewById(R.id.epi_description_1);
            ImageView epi_thumb_1 = (ImageView) rootView.findViewById(R.id.epi_thumb_1);

            epi_num_1.setText(R.string.ep);
            epi_title_1.setText(epi_num[4] + " " + epiTitles[4]);
            epi_description_1.setText(description[4]);
            epi_thumb_1.setImageDrawable(arrowsDrawableArray[4]);
            //Episode #5
            TextView epi_num_5 = (TextView) rootView.findViewById(R.id.epi_num_5);
            TextView epi_title_5 = (TextView) rootView.findViewById(R.id.epi_title_5);
            TextView epi_description_5 = (TextView) rootView.findViewById(R.id.epi_description_5);
            ImageView epi_thumb_5 = (ImageView) rootView.findViewById(R.id.epi_thumb_5);
            epi_num_5.setText(R.string.ep);
            epi_title_5.setText(epi_num[3] + " " + epiTitles[3]);
            epi_description_5.setText(description[3]);
            epi_thumb_5.setImageDrawable(arrowsDrawableArray[3]);

            // how many pages we need
            int page = (int) ((Math.ceil((double) (epiTitles.length) / 8)));
            //Hide right arrow on the last page
            if (getArguments().getInt(ARG_SECTION_NUMBER) == page+1 ){
                buttonRight.setVisibility(View.INVISIBLE);
                rootView.setBackground(arrowsDrawableArray[0]);
            }
            // set page number text
            if(getArguments().getInt(ARG_SECTION_NUMBER)!=1){
                try {
                    upRight.setText("PAGE " + Integer.toString((getArguments().getInt(ARG_SECTION_NUMBER)) - 1) + " of " + Integer.toString(page));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                upLeft.setText(R.string.episodes);
                mainEpiLayout.setVisibility(View.VISIBLE);
                rootView.setBackground(arrowsDrawableArray[0]);
                upRight.setVisibility(View.VISIBLE);
            } else{
                rootView.setBackground(arrowsDrawableArray[1]);
                buttonLeft = (ImageView) rootView.findViewById(R.id.left);
                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.fadein_main, R.anim.fadeout_epi);
                    }
                });
                upLeft.setText(R.string.series_info);
                upRight.setVisibility(View.INVISIBLE);
                mainEpiLayout.setVisibility(View.INVISIBLE);
            }

//            for (int a = 0; a <= page; a++) {
//                setViewToPage(a, colomOne, colomOTwo);
//            }

//            // Create player buttons
//            final TextView playPreview = (TextView) rootView.findViewById(R.id.play_preview);
//            playPreview.setVisibility(View.INVISIBLE);
//            TextView playAll = (TextView) rootView.findViewById(R.id.play_all_seg);
//            playAll.setVisibility(View.INVISIBLE);
//            TextView playNext = (TextView) rootView.findViewById(R.id.play_next_seg);
//            playNext.setVisibility(View.INVISIBLE);
//
//            // Create playerPreview
////            ImageView playerPreview = (ImageView) rootView.findViewById(R.id.preview_img);
////            playerPreview.setVisibility(View.INVISIBLE);
//            // Add video player
//            String path1 = "http://tac.e10.tv/Feeds/hd1/RFC_12_FISH_1_1_392_HD1.mp4";
//            Uri uri = Uri.parse(path1);
//
//            final VideoView video = (VideoView) rootView.findViewById(R.id.videoView);
//            video.setVideoURI(uri);
            
            return rootView;
        }

        private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.ic_launcher);
                } else {
                    v.setBackgroundResource(R.drawable.ic_launcher);
                }
            }
        };

        public void toRight() {
            ((EpiActivity) getActivity()).selectPage(getArguments().getInt(ARG_SECTION_NUMBER));
        }

        public void toLeft() {
            ((EpiActivity) getActivity()).selectPage(getArguments().getInt(ARG_SECTION_NUMBER) - 2);
        }

        public void toMain() {
            // This is called when the Home (Up) button is pressed in the action bar.
            // Create a simple intent that starts the hierarchical parent activity and
            // use NavUtils in the Support Package to ensure proper handling of Up.
            Intent upIntent = new Intent(getActivity(), CollectionDemoActivity.class);
            if (NavUtils.shouldUpRecreateTask(getActivity(), upIntent)) {
                // This activity is not part of the application's task, so create a new task
                // with a synthesized back stack.
                TaskStackBuilder.from(getActivity())
                        // If there are ancestor activities, they should be added here.
                        .addNextIntent(upIntent)
                        .startActivities();
                getActivity().finish();
            } else {
                // This activity is part of the application's task, so simply
                // navigate up to the hierarchical parent activity.
                NavUtils.navigateUpTo(getActivity(), upIntent);
            }
        }

        public void initLayoutsElements(){

        }

        public void setViewToPage(int page, LinearLayout colomOne, LinearLayout colomTwo) {
            int end;
            boolean secondCol = true;
            int pageNumber = getArguments().getInt(ARG_SECTION_NUMBER) - 1;

//            if (epiTitles.length <= 4) {
//                end = epiTitles.length;
//                secondCol = false;
//            } else {
//                end = 5;
//            }
            for (int position = 0; position < epiTitles.length % 4; position++) {
                int epiNumberOnPage = pageNumber * 8 - (8 - position);
                System.out.println("epiNumberOnPage " + epiNumberOnPage + "pageNumber " + pageNumber + "position" + position);
                EpiView child = new EpiView(getActivity());
                if (pageNumber == 0) {

                } else {
//                        child.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                    child.setTitle(epi_num[epiNumberOnPage] + " " + epiTitles[epiNumberOnPage]);
                    child.setDescription(description[epiNumberOnPage]);
                    child.setThumb(arrowsDrawableArray[epiNumberOnPage + 2]);
//                    if (epiNumberOnPage<= 4) {
                        colomOne.addView(child);
                        System.out.println("colomOne.addView(child);");
//                    } else {
//                        colomTwo.addView(child);
//                        System.out.println("colomOne.addView(child);");
//                    }
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle view click here
                            toRight();
                        }
                    });

                }
            }
//            if (secondCol) {
//            for (int position = 0; position < epiTitles.length % 4; position++) {
//                if (pageNumber == 0) {
//
//                } else {
//                    int epiNumberOnPage = pageNumber * 8 - (8 - position);
//                    EpiView child = new EpiView(getActivity());
//                    child.setTitle(epi_num[epiNumberOnPage] + " " + epiTitles[epiNumberOnPage]);
//                    child.setDescription(description[epiNumberOnPage]);
//                    child.setThumb(arrowsDrawableArray[epiNumberOnPage + 2]);
//                    child.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // Handle view click here
//                            toRight();
//                        }
//                    });
//                    System.out.println("epiNumberOnPage " + epiNumberOnPage + "pageNumber " + pageNumber + "position" + position);
//                    if (epiNumberOnPage<= 4) {
//                        colomOne.addView(child);
//                        System.out.println("colomOne.addView(child);");
//                    } else {
//                        colomTwo.addView(child);
//                        System.out.println("colomOne.addView(child);");
//                    }
//
//                }
//                }
//            }

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show total pages. 8 tpisodes per page
            // + 1 page for poster
            // +1 page for player
            int catInt;
            try {
                catInt = (int) (1 + (Math.ceil((double) (epiTitles.length) / 8)));
//            System.out.println("Episodes " +epiTitles.length + " Pages " + catInt + " Math.ceil " + Math.ceil((double)epiTitles.length/8));
            } catch (Exception e) {
                catInt = 3;
            }
            return catInt;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }

    }
}
